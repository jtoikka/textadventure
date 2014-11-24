package o1.inventory

import o1.adventure.render.ResourceManager
import o1.adventure.render2D.Image2D
import java.io.File
import javax.imageio.ImageIO

object Pellet {
  val defaultName = "Pellet" // Should be in resourceManager
  val defaultDesc = "pellet"
  def apply(n: String, d:String) = new Pellet(n, d)
  def apply() = new Pellet(defaultName, defaultDesc)
}

class Pellet(name: String, desc: String) extends Item(name, desc){ 
  var icon = "icon_pellet"
}