package o1.scene

import scala.collection.mutable.MutableList

/**
 * A scene is a collection of entities used to build a game scene. It contains
 * a camera for rendering.
 */

class Scene {
  val entities = MutableList[Entity]()
  
  var camera: Option[Entity] = None
  
  var world: Option[World] = None
  
  def addEntity(entity: Entity) {
    entities += entity
  }
}