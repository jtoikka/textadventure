package o1.inventory

import o1.adventure.render2D.Image2D

trait Drawable {
  
  // Icon will be used in invetory screen
  var icon: Image2D
  def getImage = icon
  def setImage(i: Image2D) = icon = i
}