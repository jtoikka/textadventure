package o1.adventure.render

import scala.collection.mutable.Map
import o1.adventure.render2D.Shape

object ResourceManager {
  val meshes = Map[String, Mesh]()
  val shapes = Map[String, Shape]() 
  
  meshes("sphere") = Mesh("data/sphere.obj")
  meshes("monkey") = Mesh("data/monkey.obj")
  meshes("cube") = Mesh("data/cube.obj")
  meshes("plate") = Mesh("data/4x4_plate_floor.obj")
  meshes("floor") = Mesh("data/floor.obj")
  meshes("floor").luminosity = 0.6f
  meshes("testMap") = Mesh("data/spaceStation2.obj")
  meshes("testMap").luminosity = 1.0f
}