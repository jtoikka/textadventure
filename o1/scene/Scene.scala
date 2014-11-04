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

  def loadMap(map: String) {
    // world
    world = Some(new World("00_testmap"))

    // entities
    val xml = ResourceManager.maps(map)
    val tileSize = (xml \ "@tilewidth").text.toInt
    println("tileSize " + tileSize)
    val objectGroups = Map[String, scala.xml.Node]()

    for (layer <- xml \ "objectgroup") {
      val name = layer \ "@name"
      objectGroups(name.text) = layer
    }

    // main object layer. 
    val objects = objectGroups("objects") \ "object"
    for (obj <- objects) {
      val ent = Factory.createEntity(obj,tileSize)
      if (ent.isDefined)
        addEntity(ent.get)
    }
    
    // player
    val player = (objectGroups("player") \ "object")(0)
    val playerEnt = Factory.createEntity(player,tileSize)
    addEntity(playerEnt.get)
    this.camera = Some(Factory.createCamera(playerEnt.get))
  }

  def addEntity(entity: Entity) {
    entities += entity
  }

  def removeEntity(entity: Entity) {
    val i = entities.indexOf(entity)
    if (i >= 0) {
      entities.remove(i)
    }
  }
  def clear() {
    entities.clear()
  }
}