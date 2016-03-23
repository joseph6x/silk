/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.silkframework.rule.similarity

import org.silkframework.config.Prefixes
import org.silkframework.entity.{Entity, Index}
import org.silkframework.rule.Operator
import org.silkframework.rule.input.Input
import org.silkframework.runtime.resource.ResourceManager
import org.silkframework.runtime.serialization.{Serialization, XmlFormat}
import org.silkframework.runtime.validation.ValidationException
import org.silkframework.util.{DPair, Identifier, GlobalV}

import scala.xml.Node

/**
 * A comparison computes the similarity of two inputs.
 */
case class Comparison(id: Identifier = Operator.generateId,
                      required: Boolean = false,
                      weight: Int = 1,
                      threshold: Double = 0.0,
                      indexing: Boolean = true,
                      metric: DistanceMeasure,
                      inputs: DPair[Input]) extends SimilarityOperator {

  require(weight > 0, "weight > 0")
  require(threshold >= 0.0, "threshold >= 0.0")

  /**
   * Computes the similarity between two entities.
   *
   * @param entities The entities to be compared.
   * @param limit The confidence limit.
   * @return The confidence as a value between -1.0 and 1.0.
   */
  override def apply(entities: DPair[Entity], limit: Double): Option[Double] = {
    val values1 = inputs.source(entities.source)
    val values2 = inputs.target(entities.target)

    var endpoint1=GlobalV.DataSources.find { x => x.id==entities.source.desc.Endpoint }.get.plugin.parameters.get("endpointURI").get
    var endpoint2=GlobalV.DataSources.find { x => x.id==entities.target.desc.Endpoint }.get.plugin.parameters.get("endpointURI").get
    var uri1=entities.source.uri
    var uri2=entities.target.uri
    
    if (values1.isEmpty || values2.isEmpty)
      None
    else {
      var distance=0.0;
      if (endpoint1==endpoint2 && uri1==uri2){
        distance=1
      }else{
        distance = metric.apply_Validation(uri1,endpoint1,uri2,endpoint2,values1, values2, threshold * (1.0 - limit))  
      }
      //val distance = metric(values1, values2, threshold * (1.0 - limit))
      if (distance == 0.0 && threshold == 0.0)
        Some(1.0)
      else if (distance <= 2.0 * threshold)
        Some(1.0 - distance / threshold)
      else if (!required)
        Some(-1.0)
      else
        None
    }
  }

  /**
   * Indexes an entity.
   *
   * @param entity The entity to be indexed
   * @param limit The similarity threshold.
   * @return A set of (multidimensional) indexes. Entities within the threshold will always get the same index.
   */
  override def index(entity: Entity, sourceOrTarget: Boolean, limit: Double): Index = {
    val values = if(sourceOrTarget) inputs.source(entity) else inputs.target(entity)

    val distanceLimit = threshold * (1.0 - limit)

    metric.index(values, distanceLimit)
  }

  override def children = inputs.toSeq

  override def withChildren(newChildren: Seq[Operator]) = {
    copy(inputs = DPair.fromSeq(newChildren.collect{ case input: Input => input }))
  }
}

object Comparison {

  /**
   * XML serialization format.
   */
  implicit object ComparisonFormat extends XmlFormat[Comparison] {

    import Serialization._

    def read(node: Node)(implicit prefixes: Prefixes, resources: ResourceManager): Comparison = {
      val id = Operator.readId(node)
      val inputs = node.child.filter(n => n.label == "Input" || n.label == "TransformInput").map(fromXml[Input])
      if(inputs.size != 2) throw new ValidationException("A comparison must have exactly two inputs ", id, "Comparison")

      try {
        val requiredStr = (node \ "@required").text
        val threshold = (node \ "@threshold").headOption.map(_.text.toDouble).getOrElse(0.0)
        val weightStr = (node \ "@weight").text
        val indexingStr = (node \ "@indexing").text
        val metric = DistanceMeasure((node \ "@metric").text, Operator.readParams(node))

        Comparison(
          id = id,
          required = if (requiredStr.isEmpty) false else requiredStr.toBoolean,
          threshold = threshold,
          weight = if (weightStr.isEmpty) 1 else weightStr.toInt,
          indexing = if (indexingStr.isEmpty) true else indexingStr.toBoolean,
          inputs = DPair(inputs(0), inputs(1)),
          metric = metric
        )
      } catch {
        case ex: Exception => throw new ValidationException(ex.getMessage, id, "Comparison")
      }
    }

    def write(value: Comparison)(implicit prefixes: Prefixes): Node = {
      value.metric match {
        case DistanceMeasure(plugin, params) =>
          <Compare id={value.id} required={value.required.toString} weight={value.weight.toString} metric={plugin.id} threshold={value.threshold.toString} indexing={value.indexing.toString}>
            {toXml(value.inputs.source)}{toXml(value.inputs.target)}{params.map {
            case (name, v) => <Param name={name} value={v}/>
          }}
          </Compare>
      }
    }
  }
}
