package o1.adventure.render2D

object Shape{
  var defColor = 1
}

/**
 * Shape is anything that can be drawn
 * @param color1 is the main color of the shape
 * @param color2 is the fill color of the shape
 **/
abstract class Shape(var color1: Int, var color2: Int ) {
  def this() = this(Shape.defColor,Shape.defColor)
  def this(color: Int) = this(color,color)
}