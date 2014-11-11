package o1.adventure.render

import scala.collection.mutable.Map
import o1.adventure.render2D.Shape
import java.awt.image.Raster
import javax.imageio.ImageIO
import java.io.File
import java.awt.image.BufferedImage
import o1.event.Event
import o1.event.EventType._
import scala.xml.XML._
import scala.collection.mutable.Buffer
import scala.reflect.api._
import o1.adventure.render2D.Image2D

object ResourceManager {
  val meshes = Map[String, Mesh]()
  val shapes = Map[String, Shape]()
//  val images = Map[String, BufferedImage]()
  val images = Map[String, Image2D]()
  val textures = Map[String, Texture]()
  val strings = Map[String, String]().withDefaultValue("NoString")
  val maps = Map[String, scala.xml.Elem]()
  // val entityInfo = Map[String, scala.xml.Node]()  
//  val database = Map[Class[_], Map[String,_]]()
  
  val xml = loadFile("data/resourceManager.xml")
  val XMLmeshes = xml \ "meshes" \ "item"
  val XMLimages = xml \ "images" \ "item"
  val XMLtextures = xml \ "textures" \ "item"
  val XMLstrings = xml \ "strings" \ "item"
  val XMLmaps = xml \ "maps" \ "item"
  
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
    images(name.text) = new Image2D(ImageIO.read(new File(path.text)),true)
  }
  
  // Load textures
  for (m <- XMLtextures) {
    val name = (m \ "@name")
    val path = (m \ "@path")
    println("Loaded texture: " + name + ", " + path)
    textures(name.text) = new Texture(path.text)
  }
  
  // Load strings
  for (m <- XMLstrings) {
    val name = (m \ "@name")
    val str = (m \ "string")
    println("Loaded string: " + name)
    strings(name.text) = str.text
  }
    // Load maps
  for (m <- XMLmaps) {
    val name = (m \ "@name")
    val path = (m \ "@path")
    println("Loaded map: " + name)
    maps(name.text) = loadFile(path.text)
  }
}