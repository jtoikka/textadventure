package o1.inventory

import o1.adventure.render.ResourceManager
import o1.adventure.render2D.Image2D
import java.io.File
import javax.imageio.ImageIO

object KillAll {
  val defaultName = "KillAll" // Should be in resourceManager
  val defaultDesc = "KillAll"
  def apply(n: String, d:String) = new Pellet(n, d)
  def apply() = new KillAll(defaultName, defaultDesc)
}

class KillAll(name: String, desc: String) extends Item(name, desc){ 
  var icon = "icon_scull"
}