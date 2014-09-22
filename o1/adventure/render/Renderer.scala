package o1.adventure.render

import o1.scene._

abstract class Renderer(val w: Int, val h: Int) {
  def renderScene(scene: Scene): Unit
  def display: String
  def clear(): Unit
}