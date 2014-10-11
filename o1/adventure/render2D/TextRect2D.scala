package o1.adventure.render2D

class TextRect2D(var rect: Rectangle2D, var text: String)
  extends Rectangle2D(rect.w, rect.h, rect.defFill) {

  // Default values
  var offX = 1
  var offY = 1
  var offMinusX = 1
  var offMinusY = 1
  var centerText = false
  
  def this(rect: Rectangle2D) = this(rect, "")

  /* Constructors -------------------------------------------------------------------*/
  //  def this(p1: Point2D, p2: Point2D, text: String)
  //    = this(new Rectangle2D(p1,p2,true,1,1),text)
  //    
  //  def this(x1: Int, y1: Int, x2: Int, y2: Int, text: String)
  //    = this(new Point2D(x1,y1),new Point2D(x2,y2),text)
  /* --------------------------------------------------------------------------------*/
}