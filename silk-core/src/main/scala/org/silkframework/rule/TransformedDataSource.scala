package org.silkframework.rule

import org.silkframework.config.TransformSpecification
import org.silkframework.dataset.DataSource
import org.silkframework.entity.{Entity, EntitySchema, Path}
import org.silkframework.util.Uri

/**
  * A data source that transforms all entities using a provided transformation.
  *
  * @param source The data source for retrieving the source entities.
  * @param transform The transformation
  */
class TransformedDataSource(source: DataSource, transform: TransformSpecification) extends DataSource {
  /**
    * Retrieves known generated types in this source.
    *
    * @param limit Restricts the number of types to be retrieved. No effect on this data source.
    */
  override def retrieveTypes(limit: Option[Int] = None): Traversable[(String, Double)] = {
    for(TypeMapping(name, typeUri) <- transform.rules) yield {
      (typeUri.toString, 1.0)
    }
  }

  /**
    * Retrieves all paths generated by this source.
    *
    * @param t The entity type for which paths shall be retrieved. No effect on this data source.
    * @param depth Only retrieve paths up to a certain length. No effect on this data source as all paths are of length one.
    * @param limit Restricts the number of paths to be retrieved. No effect on this data source.
    */
  override def retrievePaths(t: Uri, depth: Int = 1, limit: Option[Int] = None): IndexedSeq[Path] = {
    transform.rules.flatMap(_.target).map(Path(_)).distinct.toIndexedSeq
  }

  /**
    * Retrieves entities from this source which satisfy a specific entity schema.
    *
    * @param entitySchema The entity schema
    * @param limit        Limits the maximum number of retrieved entities
    * @return A Traversable over the entities. The evaluation of the Traversable may be non-strict.
    */
  override def retrieve(entitySchema: EntitySchema, limit: Option[Int]): Traversable[Entity] = {
    retrieveEntities(entitySchema, None, limit)
  }

  /**
    * Retrieves a list of entities from this source.
    *
    * @param entitySchema The entity schema
    * @param entities     The URIs of the entities to be retrieved.
    * @return A Traversable over the entities. The evaluation of the Traversable may be non-strict.
    */
  override def retrieveByUri(entitySchema: EntitySchema, entities: Seq[Uri]): Seq[Entity] = {
    retrieveEntities(entitySchema, Some(entities), None).toSeq
  }

  private def retrieveEntities(entitySchema: EntitySchema, entities: Option[Seq[Uri]], limit: Option[Int]) = {
    val subjectRule = transform.rules.find(_.target.isEmpty)
    val pathRules =
      for(path <- entitySchema.paths) yield {
        transform.rules.filter(_.target == path.propertyUri)
      }

    val sourceEntitySchema =
      EntitySchema(transform.selection.inputId,
        typeUri = transform.selection.typeUri,
        paths = pathRules.flatten.flatMap(_.paths).distinct.toIndexedSeq,
        filter = transform.selection.restriction
      )

    val sourceEntities = entities match {
      case Some(uris) => source.retrieveByUri(sourceEntitySchema, uris)
      case None => source.retrieve(sourceEntitySchema, limit)
    }

    for(entity <- sourceEntities.view) yield {
      val uri = subjectRule.flatMap(_(entity).headOption).getOrElse(entity.uri)
      val values =
        for(rules <- pathRules) yield {
          rules.flatMap(rule => rule(entity))
        }

      new Entity(uri, values, entitySchema)
    }
  }
}
