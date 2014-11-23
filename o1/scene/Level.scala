package o1.scene

import o1.adventure.render.ResourceManager

object Level {
  /**
   * Loads a level from a "Tiled" [map] into the given [scene].
   * 
   * @param scene The scene to load the level into.
   * @param map The resource name of a "Tiled" map (see ResourceManager).
   */
  def loadMap(scene: Scene, map: String,spawn: String): Unit = {
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

    // playerSpawn
    
    val player = (objectGroups("player") \ "object")
//    println(player)
    // ((node \ "properties" \ "property").filter(a => (a \ "@name").text == "spawn") \ "@value").text
    val location = player.find(a => ((a \ "properties" \ "property").find(q => (q \ "@name").text == "name").get \ "@value").text == spawn)
//    println(location)
    val playerEnt = Factory.createEntity(location.get)
    scene.addEntity(playerEnt.get)
    scene.camera = Some(Factory.createCamera(playerEnt.get))
  }
}