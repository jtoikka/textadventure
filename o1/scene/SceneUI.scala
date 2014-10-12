package o1.scene

import o1.event.Listener
import o1.event.Event
import o1.event.EventType._
import scala.collection.mutable.Map

class SceneUI(
    val inputMap: Map[Tuple2[scala.swing.event.Key.Value, Int], (Float) => Unit]) 
    extends Scene with Listener {
  var defaultListener: Listener = this
  eventTypes = Vector[EventType](E_INPUT, E_DIALOG)
  
  def handleEvent(event: Event, delta: Float) = {
    if (event.eventType == E_INPUT) {
      val eventKey = event.args(0).asInstanceOf[Tuple2[scala.swing.event.Key.Value, Int]]
      if (inputMap.contains(eventKey)) {
        inputMap(eventKey)(delta)
      }
    }
  }
  
  def dispose() = {
    
  }
  
}