package o1.inventory

import o1.adventure.render.ResourceManager
import o1.adventure.render2D.Image2D
import java.io.File
import javax.imageio.ImageIO

object Key{
  val defaultName = "Key" // Should be in resourceManager
  val defaultDesc = "Key"
  def apply(n: String,d:String ) = new Key(n,d)
  def apply() = new Key(defaultName,defaultDesc)
}

class Key(name: String, desc: String) extends Item(name, desc){ 
  var icon = "icon_key"
}