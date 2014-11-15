package o1.inventory

import o1.adventure.render.ResourceManager
import o1.adventure.render2D.Image2D
import java.io.File
import javax.imageio.ImageIO

object Rupee{
  val defaultName = "Rupee" // Should be in resourceManager
  val defaultDesc = "Munny!"
  def apply(n: String,d:String ) = new Rupee(n,d)
  def apply() = new Rupee(defaultName,defaultDesc)
}

class Rupee(name: String, desc: String) extends Item(name, desc){ 
  var icon = "icon_rupee"
}