package o1.event

import scala.collection.mutable.Map

object Input {
  val KEYUP = 0
  val KEYDOWN = 1
  val KEYRELEASED = 2
  val KEYPRESSED = 3

  def handleInput(
      keyMap: Map[scala.swing.event.Key.Value, Int],
      delta: Double) {
    var deltaFloat = delta.toFloat
    
    for (keyValue <- keyMap) {
      val key = keyValue._1
      val value = keyValue._2
      
      if (value != 0) {
        EventManager.addEvent(new Event(Vector(keyValue), EventType.E_INPUT))
      }
    }
  }
}