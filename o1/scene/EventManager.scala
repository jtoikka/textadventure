package o1.scene

import scala.collection.mutable.Buffer

object EventType {
  val E_COLLISION = 1
}

class Event(val args: Vector[Any], val eventType: Int) {}

class Listener(val eventTypes: Vector[Int]) {
  val events = Buffer[Event]()
  
  def contains(eventType: Int) = {
    eventTypes.contains(eventType)
  }
  
  def addEvent(event: Event) = {
    events += event
  }
}

object EventManager {
  val listeners = Buffer[Listener]()
  val events = Buffer[Event]()

  def addListener(listener: Listener) = {
    listeners += listener
  }

  def removeListener(listener: Listener) = {
    val index = listeners.indexOf(listener)
    if (index >= 0)
      listeners.remove(index)
  }

  def addEvent(event: Event) = {
    events += event
  }
  
  def delegateEvents() = {
    for (event <- events) {
      for (listener <- listeners) {
        if (listener.contains(event.eventType)) {
          listener.addEvent(event)
        }
      }
    }
    events.clear()
  }
}