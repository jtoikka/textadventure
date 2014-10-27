package o1.adventure.render

import scala.collection.mutable.Map
import o1.adventure.render2D.Shape
import java.awt.image.Raster
import javax.imageio.ImageIO
import java.io.File
import java.awt.image.BufferedImage
import o1.event.Event
import o1.event.EventType._

object ResourceManager {
  val meshes = Map[String, Mesh]()
  val shapes = Map[String, Shape]()
  val images = Map[String, BufferedImage]()
  val strings = Map[String, String]()
  
  meshes("sphere") = Mesh("data/sphere.obj")
  meshes("monkey") = Mesh("data/monkey.obj")
  meshes("cube") = Mesh("data/cube.obj")
  meshes("plate") = Mesh("data/4x4_plate_floor.obj")
  meshes("floor") = Mesh("data/floor.obj")
  meshes("floor").luminosity = 0.6f
  meshes("testMap") = Mesh("data/maps/00_testMap.obj")
  meshes("testMap").luminosity = 1.0f

  images("cross") = ImageIO.read(new File("data/logo.png"))

  strings("helpMenu") = "This text should wrap nicely. " +
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
    "Morbi placerat dui nec cursus bibendum. Integer auctor, " +
    "dui gravida semper maximus, arcu massa semper libero, " +
    "vitae laoreet nulla nulla non leo.\nThis should be alone on this line."
   
}