package o1.adventure.render2D
import scala.math._

class Triangle2D(var p1: Point2D, var p2: Point2D, var p3: Point2D, var defFill: Boolean, color1: Int, color2: Int)
    extends Shape(color1, color2) {
  
  def boundingBox: Rectangle2D = {
    var max_X = max(max(p1.x, p2.x), p3.x)
    var max_Y = max(max(p1.y, p2.y), p3.y)
    var min_X = min(min(p1.x, p2.x), p3.x)
    var min_Y = min(min(p1.y, p2.y), p3.y)
    new Rectangle2D(max_X - min_X, max_Y - min_Y, false, color1, color2)
  }
  
  def getWidth: Int = boundingBox.getWidth
  def getHeight: Int = boundingBox.getHeight
}