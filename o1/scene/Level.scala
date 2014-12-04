package o1.scene

import o1.adventure.render.ResourceManager

object Level {
  /**
   * Loads a level from a "Tiled" [map] into the given [scene].
   * 
   * @param scene The scene to load the level into.
   * @param map The resource name of a "Tiled" map (see ResourceManager).
   */
  def loadMap(scene: Scene, map: String): Unit = {
    // println("loadamap")
    scene.world = Some(new World(map))

    // entities
    val xml = ResourceManager.maps(map)
    val objectGroups = collection.mutable.Map[String, scala.xml.Node]()

    for (layer <- xml \ "objectgroup") {
      val name = layer \ "@name"
      objectGroups(name.text) = layer
    }
    // main object layer. 
    val objects = objectGroups("objects") \ "object"
    for (obj <- objects) {
      val ent = Factory.createEntity(obj)
      if (ent.isDefined)
        scene.addEntity(ent.get)
    }
  }
}