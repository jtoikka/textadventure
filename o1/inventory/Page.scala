package o1.inventory

import o1.adventure.render.ResourceManager
import o1.adventure.render2D.Image2D
import java.io.File
import javax.imageio.ImageIO

object Page{
  val defaultName = "Page" // Should be in resourceManager
  val defaultDesc = "Page Desc"
  def apply(n: String,d:String ) = new Page(n,d)
  def apply() = new Page(defaultName,defaultDesc)
}

class Page(name: String, desc: String) extends Item(name, desc) with Drawable { 
  
  var icon: o1.adventure.render2D.Image2D = new Image2D(ImageIO.read(new File("data/cross.png")),false)
  def getDescription: String = desc
}