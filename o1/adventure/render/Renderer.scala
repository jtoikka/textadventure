package o1.adventure.render

import o1.scene._
object Renderer {
  val empty = '\u0000'
}
abstract class Renderer(val w: Int, val h: Int) {
  def renderScene(scene: Scene): Unit
  def display: String
  def clear(): Unit
}