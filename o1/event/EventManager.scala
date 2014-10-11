package o1.event

import scala.collection.mutable.Buffer

object EventType {
  val E_COLLISION = 1
  val E_INPUT = 2
  val E_DIALOG = 3
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
  var activeInputListener: Option[Listener] = None

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
  def setActiveInputListener(listener: Listener) {
    activeInputListener = Some(listener)
  }

  def delegateEvents() = {
    for (event <- events) {
      if (event.eventType == EventType.E_INPUT) {
        if (activeInputListener.isDefined)
          activeInputListener.get.addEvent(event)
      } else {
        for (listener <- listeners) {
          if (listener.containsEventType(event.eventType)) {
            listener.addEvent(event)
          }
        }
      }
    }
    events.clear()
  }
}