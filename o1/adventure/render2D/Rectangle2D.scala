package o1.adventure.render2D

class Rectangle2D(val w: Int, val h: Int, var defFill: Boolean, color1: Int, color2: Int)
    extends Shape(color1, color2) {

  /* Constructors -------------------------------------------------------------------*/
  def this(w: Int, h: Int, defFill: Boolean) = this(w, h, defFill, Shape.defColor, Shape.defColor)
  /* --------------------------------------------------------------------------------*/
  def getWidth: Int = w
  def getHeight: Int = h
}