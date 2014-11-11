package o1.event

import scala.collection.mutable.Map

object Input {
  val KEYUP = 0
  val KEYDOWN = 1
  val KEYPRESSED = 2
  val KEYRELEASED = 3

  def handleInput(
      keyMap: Map[scala.swing.event.Key.Value, Int],
      delta: Double) {
    var deltaFloat = delta.toFloat
    
    for (keyValue <- keyMap) {
      val key = keyValue._1
      val value = keyValue._2
      
      if (value != 0) {
        val e = new Event(Vector(keyValue),EventType.E_INPUT)
        EventManager.addEvent(e)
      }
    }
  }
}