package o1.adventure.render2D
import o1.math._

class Point2D(var x: Int, var y: Int){
  // TODO: Add stuff as needed
  
  def move(vec: Vec3): Point2D = {
    x += vec.x.toInt
    y += vec.y.toInt
    this
  }
  
//  def getHigher(point: Point2D) : Point2D = if (this.y < point.y) this else point
//  def getLower(point: Point2D) : Point2D = if (this.y > point.y) this else point
}