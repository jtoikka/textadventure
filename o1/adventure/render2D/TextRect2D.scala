package o1.adventure.render2D

class TextRect2D(var rect: Rectangle2D, var text: String, var centerText: Boolean, var textWrap: Boolean)
    extends Rectangle2D(rect.w, rect.h, rect.defFill) {

  // Default values
  var offX = 1
  var offY = 1
  var offMinusX = 1
  var offMinusY = 1
  
  def this(rect: Rectangle2D) = 
    this(rect, "", false, false)
  def this(rect: Rectangle2D, text: String) =
    this(rect, text, false, false)
}