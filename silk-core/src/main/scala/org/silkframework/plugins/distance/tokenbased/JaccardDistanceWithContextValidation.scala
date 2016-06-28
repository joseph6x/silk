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

package org.silkframework.plugins.distance.tokenbased

import org.silkframework.entity.Index
import org.silkframework.rule.similarity.DistanceMeasure
import org.silkframework.runtime.plugin.{Plugin, Param}
import edu.ucuenca.authorsrelatedness.Distance
import uk.ac.shef.wit.simmetrics.similaritymetrics.{SmithWatermanGotoh, JaroWinkler}
import org.silkframework.util.{GlobalV}
import scala.collection.mutable.ListBuffer

@Plugin(
  id = "jaccardwv",
  categories = Array("Tokenbased", "Recommended"),
  label = "Jaccard with validation",
  description = "Jaccard similarity coefficient, with context validation."
)
case class JaccardDistanceWithContextValidation(@Param(value = "The maximum semantic distance between authors' context", advanced = true)
  ContextDistance: Double = 0.70) extends DistanceMeasure {

  
  override def apply_Validation(u1:String, d1:String, u2:String,d2:String ,values1: Seq[String], values2: Seq[String], limit: Double): Double = {
    
    
    var dis= apply(values1,values2, limit)
    if (dis>limit){
      1.0  
    }else{
      var di=0.0
       try { 
         var Distance2 = new Distance()
        di=Distance2.NWD(u1,d1,u2,d2,"")
        System.out.println(u1+"|"+d1+"|"+u2+"|"+d2+"|"+di+" "+dis)
         // Distance2.close();
   } catch {
     case e: Exception => System.out.println("Warn: "+e+e.getStackTraceString)
   }
   if(di>ContextDistance){
        1.0
   }else{
        0.0
   }
    }
  }
  
  
  override def apply(values1: Seq[String], values2: Seq[String], limit: Double): Double = {
  	

	var w1 = new ListBuffer[String]()
	var w2 = new ListBuffer[String]()


  	var _values1 = values1.filter ( _.trim.nonEmpty ).distinct

  	var _values2 = values2.filter ( _.trim.nonEmpty ).distinct
  	var a_ = new SmithWatermanGotoh()
  	var b_ = new JaroWinkler()

  	var co=0

  	for(  x <- _values1 ){
  		for(  y <- _values2 ){

  			var xx=x.trim
  			var yy=y.trim

  			var v = 0.0
  			
  			v = b_.getSimilarity(xx,yy)
  			var exac = v == 1.0

  			var sub=_values1.size < _values2.size


  			if ( !sub && xx.startsWith(yy) && yy.length() <=3 || sub && yy.startsWith(xx) && xx.length() <=3   ){
  				v = 1.0
  			}else{
  				v= b_.getSimilarity(xx,yy)
  			}

  			if (exac){
  				v = 1.0
  			}


			if (  v>0.99){
				//System.out.println("Warn: "+xx+"  "+yy+"   "+v)
				if (w1.contains(xx) || w2.contains(yy))
				{

				}else	
				{
					co = co + 1
					w1+=xx
					w2+=yy
				}

			}  		
  		}
  	}

  	val min=Math.min(_values1.size, _values2.size)


//System.out.println("Warn2: "+co+"/"+min)

  	var c = 1.0-((co+0.0)/min)

	//System.out.println("Warn2: "+c)

	c


    //val set1 = values1.toSet
    //val set2 = values2.toSet

    //val intersectionSize = (set1 intersect set2).size
    //val unionSize = (set1 union set2).size

    //1.0 - intersectionSize.toDouble / unionSize
  }

  override def index(values: Seq[String], limit: Double) = {
    val valuesSet = values.toSet
    //The number of values we need to index
    val indexSize = math.round(valuesSet.size * limit + 0.5).toInt

    Index.oneDim(valuesSet.take(indexSize).map(_.hashCode))
  }
}
