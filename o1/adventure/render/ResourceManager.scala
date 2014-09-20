package o1.adventure.render

import scala.collection.mutable.Map

object ResourceManager {
  val meshes = Map[String, Mesh]()
                           
  meshes("sphere") = Mesh("data/sphere.obj")
  meshes("monkey") = Mesh("data/monkey.obj")
  meshes("cube") = Mesh("data/cube.obj")
}