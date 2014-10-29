package o1.adventure.render

import scala.collection.mutable.Map
import o1.adventure.render2D.Shape
import java.awt.image.Raster
import javax.imageio.ImageIO
import java.io.File
import java.awt.image.BufferedImage
import o1.event.Event
import o1.event.EventType._
import scala.xml.XML

object ResourceManager {
  val meshes = Map[String, Mesh]()
  val shapes = Map[String, Shape]()
  val images = Map[String, BufferedImage]()
  val strings = Map[String, String]()

  val xml = XML.loadFile("data/resourceManager.xml")
  val XMLmeshes = xml \ "meshes" \ "item"
  val XMLimages = xml \ "images" \ "item"
  val XMLstrings = xml \ "strings" \ "item"

  // Load meshes
  for (m <- XMLmeshes) {
    val name = (m \ "@name")
    val path = (m \ "@path")
    val lum = (m \ "luminosity")
    
    print("Loaded mesh: " + name + ", " + path)
    meshes(name.text) = Mesh(path.text)
    if(!lum.text.isEmpty){
      print(" , Luminosity: " + lum.text.toFloat)
      meshes(name.text).luminosity = lum.text.toFloat
    }
    print("\n")
  }
  
  // Load images
  for (m <- XMLimages) {
    val name = (m \ "@name")
    val path = (m \ "@path")
    println("Loaded image: " + name + ", " + path)
    images(name.text) = ImageIO.read(new File(path.text))
  }
  
  // Load strings
  for (m <- XMLstrings) {
    val name = (m \ "@name")
    val str = (m \ "string")
    println("Loaded string: " + name)
    strings(name.text) = str.text
  }
}