package o1.inventory

import o1.adventure.render.ResourceManager
import scala.xml._
import java.io.File

object InventoryTestApp extends App {

  val xml = XML.loadFile("data/resourceManager.xml")
  val meshes = xml \ "meshes" \ "item"
  val images = xml \ "images" \ "item"
  val strings = xml \ "strings" \ "item"
  println("Meshes lenght: " + meshes.length)
  println("Images lenght: " + images.length)
  println("Strings lenght: " + strings.length)
  for (mesh <- meshes) {
    println("Mesh: " + mesh \ "@name" + ", with path " + mesh \ "@path")
  }
  for (image <- images) {
    println("Image: " + image \ "@name" + ", with path " + image \ "@path")
  }
  for (string <- strings) {
    println("String: " + string \ "@name" + " with text:")
    val text = (string \ "string").text
    println(text)
    
  }

}