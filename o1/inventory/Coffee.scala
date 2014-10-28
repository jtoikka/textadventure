package o1.inventory

import o1.adventure.render.ResourceManager
import o1.adventure.render2D.Image2D
import java.io.File
import javax.imageio.ImageIO

object Coffee{
  val defaultName = "Page" // Should be in resourceManager
  val defaultDesc = "Page Desc"
  def apply(n: String,d:String ) = new Coffee(n,d)
  def apply() = new Coffee(defaultName,defaultDesc)
}

class Coffee(name: String, desc: String) extends Item(name, desc) with Drawable { 
  
  def getDescription: String = desc

  var icon = Image2D("icon_coffee")
}