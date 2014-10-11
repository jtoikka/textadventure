package o1.event

import scala.collection.mutable.Buffer

object EventType extends Enumeration{
  type EventType = Value
  val E_COLLISION,
      E_INPUT,
      E_DIALOG = Value
      
//  val E_COLLISION = 1
//  val E_INPUT = 2
//  val E_DIALOG = 3
}
import o1.event.EventType._
class Event(val args: Vector[Any], val eventType: EventType) {}

trait Listener {
  var eventTypes = Vector[EventType]()
  val events = Buffer[Event]()
  var childListeners = Buffer[Listener]()

  EventManager.addListener(this)

  def handleEvents(delta: Float): Unit = {
    for (event <- events) {
      handleEvent(event, delta)
    }
    events.clear()
    
    if(!childListeners.isEmpty){
      for(i <-childListeners){
        i.handleEvents(delta)
      }
    }
  }
  
  def handleEvent(event: Event, delta: Float)

  def addEvent(event: Event) = {
    events += event
  }

  def containsEventType(eventType: EventType) = {
    eventTypes.contains(eventType)
  }
  def dispose()
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