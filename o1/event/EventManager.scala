package o1.event
import scala.collection.mutable.Buffer
import o1.screen.Screen

object EventType extends Enumeration {
  type EventType = Value
  val E_COLLISION, 
      E_BUY,
      E_OPEN_CHEST,
      E_INPUT, 
      E_DIALOG, 
      E_CHANGE_SCREEN, 
      E_CHANGE_SCENE, 
      E_GAME_EVENT, 
      E_GAME_UPDATE, 
      E_CHANGE_MAP, 
      E_LOAD_NEW_MAP, 
      E_CHANGE_HUD_INFO, 
      E_TEST, 
      E_LOOKING_AT, 
      E_PLAYER_CREATION, 
      E_INTERACTION, 
      E_THROW_DIALOG,
      E_ANSWER_DIALOG, 
      E_OPEN_DOOR, 
      E_GHOST_KILLED,
      E_NONE, 
      E_EXPLOSION,
      E_PLAYER_DAMAGE,
      E_OPEN_BOSS_DOOR,
      E_OPEN_LAST_DOOR,
      E_PLAYER_DEAD,
      E_RESET_GAME,
      E_CRAZY_ASSARI,
      E_VICTORY,
      E_SYSTEM_EXIT = Value
}

import o1.event.EventType._

class Event(val args: Vector[Any], val eventType: EventType) {}

trait Listener {
  //  var eventTypes = Vector[EventType]()
  val events = Buffer[Event]()
  var childListeners = Buffer[Listener]()
  var eventHandlers = Map[EventType, (Event, Float) => Unit]()

  EventManager.addListener(this)

  def handleEvents(delta: Float): Unit = {
    for (event <- events) {
      handleEvent(event, delta)
    }
    events.clear()

    if (!childListeners.isEmpty) {
      for (i <- childListeners) {
        i.handleEvents(delta)
      }
    }
  }

  def handleEvent(event: Event, delta: Float) {
    if (containsEventType(event.eventType)) {
      eventHandlers(event.eventType)(event, delta)
    }
  }

  def addEvent(event: Event) = {
    events += event
  }

  def containsEventType(eventType: EventType) = {
    eventHandlers.contains(eventType)
  }
  def dispose()
}

object EventManager {
  val listeners = Buffer[Listener]()
  val events = Buffer[Event]()
  private var activeInputListener: Option[Listener] = None
  private var lastActiveInputListener: Option[Listener] = None

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
    //    println("changeInputListener")
    if(activeInputListener.isDefined && 
        activeInputListener.get.isInstanceOf[Screen])
      lastActiveInputListener = activeInputListener
      
    activeInputListener = Some(listener)
  }

  def returnToLastInputListener() {
    activeInputListener = lastActiveInputListener
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
//    for (listener <- listeners) {
//      val count = listener.events.size
//      if (count > 1) {
//        println("Too many events: " + listener)
//      }
//    }
    events.clear()
  }
}