package o1.event

import scala.collection.mutable.Buffer

object EventType {
  val E_COLLISION = 1
  val E_INPUT = 2
}

class Event(val args: Vector[Any], val eventType: Int) {}

trait Listener {
  var eventTypes = Vector[Int]()
  val events = Buffer[Event]()
  EventManager.addListener(this)
  
  def handleEvents(delta: Float) = {
    for (event <- events) {
      handleEvent(event, delta)
    }
    events.clear()
  }
  
  def handleEvent(event: Event, delta: Float)
  
  def addEvent(event: Event) = {
    events += event
  }
  
  def containsEventType(eventType: Int) = {
    eventTypes.contains(eventType)
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
        if (listener.containsEventType(event.eventType)) {
          listener.addEvent(event)
        }
      }
    }
    events.clear()
  }
}