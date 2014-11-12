package o1.scene

import scala.collection.mutable.Map
import scala.collection.mutable.MutableList
import o1.event.EventType
import o1.event.Listener
import o1.event.Event
import o1.event.EventManager

/**
 * An entity is a container of components, that can represent any game element.
 * The entity and its components are solely composed of data; by passing the 
 * entity to various systems, this data can be utilized or modified. For
 * example, an entity might contain information about its position in the
 * game-world, and information about its physical characteristics; the game's
 * renderer can use these to create a visual representation of the entity.
 */
class Entity(listenerEventTypes: Vector[EventType.EventType]) extends Listener {  
    val components = Map[Class[_ <: Component], Component]()
  var destroy = false
  
  val children =  MutableList[Entity]()
  
  var description = ""
  
  def addComponent[T <: Component](component: T) = {
    components(component.getClass()) = component
  }
  
  def getComponent[T <: Component](componentClass: Class[T]) = {
    if (components.contains(componentClass))
      Some(components(componentClass).asInstanceOf[T])
    else 
      None
  }
  
  def addChild(entity: Entity) = {
    children += entity
  }
  
  def dispose = {
    EventManager.removeListener(this)
  }
  
  override def toString() = {
    description
  }
}