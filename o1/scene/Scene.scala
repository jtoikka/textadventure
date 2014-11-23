package o1.scene

import scala.collection.mutable.MutableList
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Buffer
import scala.collection.mutable.Map
import o1.adventure.render.ResourceManager
import o1.math.Vec2

/**
 * A scene is a collection of entities used to build a game scene. It contains
 * a camera for rendering.
 */

class Scene {
  //val entities = MutableList[Entity]() / Why mutable list?
  val entities = Buffer[Entity]()

  var camera: Option[Entity] = None

  var world: Option[World] = None

  def addEntity(entity: Entity) {
    entities += entity
  }

  def removeEntity(entity: Entity) {
    val i = entities.indexOf(entity)
    if (i >= 0) {
      entities.remove(i)
    }
    entity.dispose()
  }
  
  def clear() {
    entities.foreach(_.dispose())
    entities.clear()
  }
}