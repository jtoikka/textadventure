package o1.adventure.render2D

import scala.collection.mutable.Map

object ResourceManager2D {
  val shapes = Map[String, Shape]()
  
  shapes("testRectangle") = new Rectangle2D(20,10,false)
  shapes("testRectangle2") = new Rectangle2D(20,10,true)
  shapes("testTriangle") = new Triangle2D(5,5,50,45,0,60,1)
}