package o1.scene

import scala.collection.mutable.Buffer
import o1.inventory.Coffee
import scala.xml.Node
import o1.math.Vec2
import o1.math.Vec3
import o1.inventory.Page
import o1.event.EventType
import o1.event.EventManager
import o1.event.Event
import o1.adventure.render2D.Dialog
import o1.adventure.render2D.Rectangle2D
import o1.event.Listener
import o1.inventory.Rupee
import o1.inventory.Key
import o1.inventory.Item
import o1.math.Utility
import o1.math.Vec4

/**
 * The Factory object is a collection of functions that can be used to create
 * entities with various characteristics.
 */

object Factory {

  def createCamera(followEntity: Entity) = {
    var camera = new Entity()

    var spatialComp = new SpatialComponent()
    camera.addComponent(spatialComp)

    var followComponent = new FollowComponent(followEntity)
    camera.addComponent(followComponent)

    camera
  }

  def createCoffee() = {
    var cof = new Entity()
    var spatialComp = new SpatialComponent()

    cof.description = "Coffee"

    cof.addComponent(spatialComp)

    var renderComp = new RenderComponent("coffee")

    var collisionComponent = new CollisionComponent(1.0f, CollisionComponent.CIRCLE)
    collisionComponent.isActive = false
    cof.addComponent(collisionComponent)

    cof.eventHandlers = scala.collection.immutable.Map(
      (EventType.E_COLLISION, (event, delta) => {
        val entityA = event.args(0).asInstanceOf[Entity]
        val entityB = event.args(1).asInstanceOf[Entity]
        val entBinventory = entityB.getComponent(InventoryComponent.id)

        if (entityA == cof && entBinventory.isDefined) {
          println("page pickup")
          entBinventory.get.inv.addItem(cof.getComponent(InventoryItemComponent.id).get.invItem)
          cof.destroy = true
        }
      }))
    val invComponent = new InventoryItemComponent(Coffee())

    cof.addComponent(invComponent)

    cof.addComponent(renderComp)
    cof
  }

  def createCoffeeBullet(position: Vec3, direction: Vec3) = {
    var cof = new Entity()

    cof.description = "Coffee bullet"

    val rotateComp = new RotateComponent(rateUp = 0.1f)
    cof.addComponent(rotateComp)

    var spatialComp = new SpatialComponent()
    spatialComp.position = position
    spatialComp.position.y = 0.8f

    cof.addComponent(spatialComp)
    
    cof.eventHandlers = scala.collection.immutable.Map(
      (EventType.E_COLLISION, (event, delta) => {
        val entityA = event.args(0).asInstanceOf[Entity]
        val entityB = event.args(1).asInstanceOf[Entity]

        if (entityA == cof) {
          if (entityB.getComponent(DamageComponent.id).isDefined) {
            cof.destroy = true
            EventManager.addEvent(
                new Event(Vector(spatialComp.position), 
                EventType.E_EXPLOSION))
          }
        }
      }))

    val physicsComp = new PhysicsComponent(Vec3(direction.x, 0.3f, direction.z), Vec3(0.0f, -0.0981f, 0.0f))
    cof.addComponent(physicsComp)

    var renderComp = new RenderComponent("coffee")

    var collisionComponent =
      new CollisionComponent(
        0.15f,
        CollisionComponent.CIRCLE,
        collisionType = CollisionComponent.COFFEE)
    collisionComponent.isActive = true
    collisionComponent.collidesWith.clear()
    collisionComponent.collidesWith += CollisionComponent.DEFAULT
    collisionComponent.collidesWith.clear()
    collisionComponent.collidesWith += CollisionComponent.DEFAULT
    cof.addComponent(collisionComponent)

    var damageComp = new DamageComponent(1, DamageComponent.ENEMY)
    cof.addComponent(damageComp)

    var breakableComp = new BreakableComponent()
    cof.addComponent(breakableComp)

    cof.addComponent(renderComp)
    cof
  }

  def createFloor() = {
    var floor = new Entity()
    var spatialComp = new SpatialComponent()

    floor.addComponent(spatialComp)

    var renderComp = new RenderComponent("floor")

    floor.addComponent(renderComp)
    floor
  }

  // Switch for entity creation , mapSize: Vec2, blockSize: Int
  def createEntity(node: Node): Option[Entity] = {
    val typeName = (node \ "@type").text
    val ent = typeName match {
      case "coffee" => Some(createCoffee(node))
      case "page" => Some(createPage(node))
      case "monkey" => Some(createMonkey(node))
      case "enemy" => Some(createTestEnemy(node))
//      case "player" => Some(createPlayer(node))
      case "door" => Some(createDoor(node))
      case "openDoor" => Some(createOpenDoor(node))
      case "rupee" => Some(createRupee(node))
      case "key" => Some(createKey(node))
      case "shop" => Some(createShop(node))
      case "chest" => Some(createChest(node))
      case "levelTrigger" => Some(createLevelTrigger(node))
      case "breakableWall" => Some(createBreakableWall(node))
      case "unbreakableWall" => Some(createUnbreakableWall(node))
      case _ => None
    }
    ent
  }

  def createCoffee(node: Node) = {
    // TODO: Fix magic size and location conversion
    val name = (node \ "@name").text
    val typeName = (node \ "@name").text
    val loc = Vec2((node \ "@x").text.toFloat, (node \ "@y").text.toFloat)
    val size = (node \ "@width").text.toFloat

    val entity = new Entity()

    entity.description = "coffee"

    val spatialComp = new SpatialComponent()
    spatialComp.position = Vec3((loc.x * 2) / 16, 0.5f, loc.y * 2 / 16)
    spatialComp.scale = Vec3(2.0f, 2.0f, 2.0f)
    entity.addComponent(spatialComp)

    entity.eventHandlers = scala.collection.immutable.Map(
      (EventType.E_COLLISION, (event, delta) => {
        val entityA = event.args(0).asInstanceOf[Entity]
        val entityB = event.args(1).asInstanceOf[Entity]
        val entBinventory = entityB.getComponent(InventoryComponent.id)

        if (entityA == entity && entBinventory.isDefined) {
          println("cofeee pickup")
          entBinventory.get.inv.addItem(entity.getComponent(InventoryItemComponent.id).get.invItem)
          entity.destroy = true
        }
      }), (EventType.E_INTERACTION, (event, delta) => {
        val entityA = event.args(0).asInstanceOf[Option[Entity]]
        val entityB = event.args(1).asInstanceOf[Option[Entity]]

        if (entityB.isDefined && entityB.get == entity) {
          println("Coffee Interaction")
        }
      }))
    val invComponent = new InventoryItemComponent(Coffee())
    entity.addComponent(invComponent)

    var renderComp = new RenderComponent("coffee")
    entity.addComponent(renderComp)

    val rotateComp = new RotateComponent(-0.2f)
    entity.addComponent(rotateComp)

    var collisionComponent = new CollisionComponent(size / 16, CollisionComponent.CIRCLE)
    collisionComponent.isActive = false
    entity.addComponent(collisionComponent)
    entity
  }
  def createKey(node: Node) = {
    // TODO: Fix magic size and location conversion
    val name = (node \ "@name").text
    val typeName = (node \ "@name").text
    val loc = Vec2((node \ "@x").text.toFloat, (node \ "@y").text.toFloat)
    val size = (node \ "@width").text.toFloat

    val entity = new Entity()

    entity.description = "key"

    val spatialComp = new SpatialComponent()
    spatialComp.position = Vec3((loc.x * 2) / 16, 0.25f, loc.y * 2 / 16)
    spatialComp.scale = Vec3(1.0f, 1.0f, 1.0f)
    entity.addComponent(spatialComp)

    entity.eventHandlers = scala.collection.immutable.Map(
      (EventType.E_COLLISION, (event, delta) => {
        val entityA = event.args(0).asInstanceOf[Entity]
        val entityB = event.args(1).asInstanceOf[Entity]
        val entBinventory = entityB.getComponent(InventoryComponent.id)

        if (entityA == entity && entBinventory.isDefined) {
          println("key pickup")
          entBinventory.get.inv.addItem(entity.getComponent(InventoryItemComponent.id).get.invItem)
          entity.destroy = true
        }
      }), (EventType.E_INTERACTION, (event, delta) => {
        val entityA = event.args(0).asInstanceOf[Option[Entity]]
        val entityB = event.args(1).asInstanceOf[Option[Entity]]

        if (entityB.isDefined && entityB.get == entity) {
          println("key Interaction")
        }
      }))
    val invComponent = new InventoryItemComponent(Key())
    entity.addComponent(invComponent)

    var renderComp = new RenderComponent("key", Some("door_tex"))
    entity.addComponent(renderComp)

    val rotateComp = new RotateComponent(-0.2f)
    entity.addComponent(rotateComp)

    var collisionComponent = new CollisionComponent(size / 16, CollisionComponent.CIRCLE)
    collisionComponent.isActive = false
    entity.addComponent(collisionComponent)
    entity
  }
  def createPage(node: Node) = {
    // TODO: Fix magic size and location conversion
    val name = "Pages" //(node \ "@name").text
    val typeName = (node \ "@name").text
    val loc = Vec2((node \ "@x").text.toFloat, (node \ "@y").text.toFloat)
    val size = (node \ "@width").text.toFloat

    val entity = new Entity()

    entity.description = "page"

    entity.eventHandlers = scala.collection.immutable.Map(
      (EventType.E_COLLISION, (event, delta) => {
        val entityA = event.args(0).asInstanceOf[Entity]
        val entityB = event.args(1).asInstanceOf[Entity]
        val entBinventory = entityB.getComponent(InventoryComponent.id)

        if (entityA == entity && entBinventory.isDefined) {
          println("page pickup")
          entBinventory.get.inv.addItem(entity.getComponent(InventoryItemComponent.id).get.invItem)
          entity.destroy = true
        }
      }), (EventType.E_INTERACTION, (event, delta) => {
        val entityA = event.args(0).asInstanceOf[Option[Entity]]
        val entityB = event.args(1).asInstanceOf[Option[Entity]]

        if (entityB.isDefined && entityB.get == entity) {
          println("page Interaction")
        }
      }))

    val spatialComp = new SpatialComponent()
    spatialComp.position = Vec3(loc.x * 2 / 16, 0.5f, loc.y * 2 / 16)
    entity.addComponent(spatialComp)

    val invComponent = new InventoryItemComponent(Page())
    entity.addComponent(invComponent)

    var renderComp = new RenderComponent("page")
    entity.addComponent(renderComp)

    var faceCameraComp = new FaceCameraComponent()
    entity.addComponent(faceCameraComp)

    // TODO: Fix magic size conversion
    var collisionComponent = new CollisionComponent(size / 16, CollisionComponent.CIRCLE)
    collisionComponent.isActive = false
    entity.addComponent(collisionComponent)
    entity
  }

  def createMonkey(node: Node) = {
    // TODO: Fix magic size and location conversion
    val name = (node \ "@name").text
    val typeName = (node \ "@name").text
    val loc = Vec2((node \ "@x").text.toFloat, (node \ "@y").text.toFloat)
    val size = (node \ "@width").text.toFloat

    val entity = new Entity()

    entity.description = "monkey"

    val spatialComp = new SpatialComponent()
    spatialComp.position = Vec3(loc.x * 2 / 16, 0.8f, loc.y * 2 / 16)
    entity.addComponent(spatialComp)

    var renderComp = new RenderComponent("monkey")
    entity.addComponent(renderComp)

    var faceCameraComp = new FaceCameraComponent()
    entity.addComponent(faceCameraComp)

    var collisionComponent = new CollisionComponent(
      size / 16, CollisionComponent.CIRCLE)
    entity.addComponent(collisionComponent)
    entity
  }

  def createShop(node: Node) = {
    // TODO: Fix magic size and location conversion
    val name = (node \ "@name").text
    val typeName = (node \ "@name").text
    val loc = Vec2((node \ "@x").text.toFloat, (node \ "@y").text.toFloat)
    //    val rotation = (node \ "@name").text
    val w = (node \ "@width").text.toFloat / 8
    val h = (node \ "@height").text.toFloat / 8

    val entity = new Entity()
    entity.eventHandlers = scala.collection.immutable.Map(
      (EventType.E_INTERACTION, (event, delta) => {
        val player = event.args(0).asInstanceOf[Option[Entity]]
        val entityB = event.args(1).asInstanceOf[Option[Entity]]

        if (entityB.isDefined && entityB.get == entity) {
          println("Shop Interaction")
          val d = Factory.createDialog(Vector(
            ("Coffee, 1 rupees", new Event(Vector(player, Coffee(), 1, 1), EventType.E_BUY)),
            ("Key, 5 rupees", new Event(Vector(player, Key(), 1, 5), EventType.E_BUY))),
            "Do you want to buy something?", None, 40, 6)
          EventManager.addEvent(new Event(Vector(d, entity.hashCode()), EventType.E_THROW_DIALOG))
        }
      }), (EventType.E_BUY, (event, delta) => {
        val player = event.args(0).asInstanceOf[Some[Entity]]
        val item = event.args(1).asInstanceOf[Item]
        val count = event.args(2).asInstanceOf[Int]
        val pricePerItem = event.args(3).asInstanceOf[Int]
        if (!player.get.getComponent(InventoryComponent.id).get.inv.removeOfType(Rupee(), pricePerItem)) {
          val d = Factory.createDialog(Vector(
            ("Ok.. :(", new Event(Vector(), EventType.E_NONE))),
            "You dont have enough munny", None, 40, 6)
          EventManager.addEvent(new Event(Vector(d, entity.hashCode()), EventType.E_THROW_DIALOG))
        } else {
          player.get.getComponent(InventoryComponent.id).get.inv.addItem(item)
          val d = Factory.createDialog(Vector(
            ("Ok", new Event(Vector(), EventType.E_NONE))),
            "You got one " + item.desc, None, 40, 6)
          EventManager.addEvent(new Event(Vector(d, entity.hashCode()), EventType.E_THROW_DIALOG))
        }

      }))
    entity.description = "shop"

    val spatialComp = new SpatialComponent()
    spatialComp.position = Vec3(loc.x * 2 / 16, 0.0f, loc.y * 2 / 16)
    spatialComp.forward = Vec3(1.0f, 0, 0)
    entity.addComponent(spatialComp)

    var renderComp = new RenderComponent("coffee_machine", Some("coffee_machine"))
    entity.addComponent(renderComp)

    var collisionComponent = new CollisionComponent(
      w / 16, CollisionComponent.SQUARE,
      halfWidth = w / 2, halfHeight = h / 2)
    entity.addComponent(collisionComponent)

    entity
  }
  def createChest(node: Node) = {
    // TODO: Fix magic size and location conversion
    val name = (node \ "@name").text
    val typeName = (node \ "@name").text
    val loc = Vec2((node \ "@x").text.toFloat, (node \ "@y").text.toFloat)
    //    val rotation = (node \ "@name").text
    val w = (node \ "@width").text.toFloat / 8
    val h = (node \ "@height").text.toFloat / 8

    val pagesString = ((node \ "properties" \ "property").filter(a => (a \ "@name").text == "pages") \ "@value").text
    val keysString = ((node \ "properties" \ "property").filter(a => (a \ "@name").text == "keys") \ "@value").text
    val rupeesString = ((node \ "properties" \ "property").filter(a => (a \ "@name").text == "rupees") \ "@value").text

    val pages = if (!pagesString.isEmpty()) pagesString.toInt else 0
    val keys = if (!keysString.isEmpty()) keysString.toInt else 0
    val rupees = if (!rupeesString.isEmpty()) rupeesString.toInt else 0

    val entity = new Entity()
    entity.eventHandlers = scala.collection.immutable.Map(
      (EventType.E_INTERACTION, (event, delta) => {
        val player = event.args(0).asInstanceOf[Option[Entity]]
        val entityB = event.args(1).asInstanceOf[Option[Entity]]

        if (entityB.isDefined && entityB.get == entity) {
          println("Door Interaction")
          val d = Factory.createDialog(Vector(
            ("Yes", new Event(Vector(player, entityB), EventType.E_OPEN_CHEST)),
            ("No", new Event(Vector(false, entity.hashCode()), EventType.E_NONE))),
            "Do you want to open the chest with a key?", None, 40, 6)
          EventManager.addEvent(new Event(Vector(d, entity.hashCode()), EventType.E_THROW_DIALOG))
        }
      }), (EventType.E_OPEN_CHEST, (event, delta) => {
        val player = event.args(0).asInstanceOf[Option[Entity]]
        val entityB = event.args(1).asInstanceOf[Option[Entity]]

        if (player.isDefined && entityB.isDefined && entityB.get == entity &&
          player.get.getComponent(InventoryComponent.id).isDefined) {
          if (player.get.getComponent(InventoryComponent.id).get.inv.removeOneOfType(Key())) {
            entity.dispose()
            entity.destroy = true
            val chestItems = entityB.get.getComponent(InventoryComponent.id).get.inv.getAllItems()

            var dialogString = "You got some new items!\n"
            if (chestItems.count(i => i.isInstanceOf[Page]) > 0)
              dialogString += "Pages " + chestItems.count(i => i.isInstanceOf[Page]) + "\n"
            if (chestItems.count(i => i.isInstanceOf[Rupee]) > 0)
              dialogString += "Rupees " + chestItems.count(i => i.isInstanceOf[Rupee]) + "\n"
            if (chestItems.count(i => i.isInstanceOf[Key]) > 0)
              dialogString += "Keys " + chestItems.count(i => i.isInstanceOf[Key]) + "\n"

            val d = Factory.createDialog(Vector(
              ("Ok", new Event(Vector(), EventType.E_NONE))),
              dialogString, None, 40, 6)
            EventManager.addEvent(new Event(Vector(d, entity.hashCode()), EventType.E_THROW_DIALOG))

            chestItems.foreach(f => player.get.getComponent(InventoryComponent.id).get.inv.addItem(f))
          } else {
            val d = Factory.createDialog(Vector(
              ("Ok.. :(", new Event(Vector(), EventType.E_NONE))),
              "You need key to open the chest", None, 40, 6)
            EventManager.addEvent(new Event(Vector(d, entity.hashCode()), EventType.E_THROW_DIALOG))
          }
        }
      }))
    entity.description = "chest"

    val spatialComp = new SpatialComponent()
    spatialComp.position = Vec3(loc.x * 2 / 16, 0.0f, loc.y * 2 / 16)
    spatialComp.forward = Vec3(1.0f, 0, 0)
    entity.addComponent(spatialComp)

    val inventoryComponent = new InventoryComponent()
    entity.addComponent(inventoryComponent)

    for (i <- 0 until pages) {
      inventoryComponent.inv.addItem(Page())
    }
    for (i <- 0 until keys) {
      inventoryComponent.inv.addItem(Key())
    }
    for (i <- 0 until rupees) {
      inventoryComponent.inv.addItem(Rupee())
    }
    var renderComp = new RenderComponent("chest", Some("chest"))
    entity.addComponent(renderComp)

    var collisionComponent = new CollisionComponent(
      w / 16, CollisionComponent.SQUARE,
      halfWidth = w / 2, halfHeight = h / 2)
    entity.addComponent(collisionComponent)

    entity
  }

  def createLevelTrigger(node: Node) = {
    // TODO: Fix magic size and location conversion
    val name = (node \ "@name").text
    val typeName = (node \ "@name").text
    val level = ((node \ "properties" \ "property").filter(a => (a \ "@name").text == "level") \ "@value").text
    val spawn = ((node \ "properties" \ "property").filter(a => (a \ "@name").text == "spawn") \ "@value").text
    val loc = Vec2((node \ "@x").text.toFloat, (node \ "@y").text.toFloat)
    //    val rotation = (node \ "@name").text
    val w = (node \ "@width").text.toFloat / 8
    val h = (node \ "@height").text.toFloat / 8

    val entity = new Entity()
    entity.eventHandlers = scala.collection.immutable.Map(
      (EventType.E_COLLISION, (event, delta) => {
        val entityA = event.args(0).asInstanceOf[Entity]
        val entityB = event.args(1).asInstanceOf[Entity]

        if (entityA == entity && entityB.getComponent(PlayerComponent.id).isDefined) {
          EventManager.addEvent(new Event(Vector(level, spawn), EventType.E_LOAD_NEW_MAP))
        }
      }))
    entity.description = "levelTrigger"

    val spatialComp = new SpatialComponent()
    spatialComp.position = Vec3(loc.x * 2 / 16, 0.0f, loc.y * 2 / 16)
    spatialComp.forward = Vec3(1.0f, 0, 0)
    entity.addComponent(spatialComp)

    var collisionComponent = new CollisionComponent(
      w / 16, CollisionComponent.SQUARE,
      halfWidth = w / 2, halfHeight = h / 2)
    collisionComponent.isActive = false

    entity.addComponent(collisionComponent)

    entity
  }

  def createDoor(node: Node) = {
    // TODO: Fix magic size and location conversion
    val name = (node \ "@name").text
    val typeName = (node \ "@name").text
    val loc = Vec2((node \ "@x").text.toFloat, (node \ "@y").text.toFloat)
    val rotation = if (!(node \ "@rotation").text.isEmpty()) (node \ "@rotation").text.toInt else 0
//    println("Door rotation: " + rotation)
    val w = (node \ "@width").text.toFloat / 8 + 0.1f
    val h = (node \ "@height").text.toFloat / 8 + 0.1f

    val entity = new Entity()
    entity.eventHandlers = scala.collection.immutable.Map(
      (EventType.E_INTERACTION, (event, delta) => {
        val player = event.args(0).asInstanceOf[Option[Entity]]
        val entityB = event.args(1).asInstanceOf[Option[Entity]]

        if (entityB.isDefined && entityB.get == entity) {
          println("Door Interaction")
          val d = Factory.createDialog(Vector(
            ("Yes", new Event(Vector(player, entityB), EventType.E_OPEN_DOOR)),
            ("No", new Event(Vector(false, entity.hashCode()), EventType.E_NONE))),
            "Do you want to open the door?", None, 40, 6)
          EventManager.addEvent(new Event(Vector(d, entity.hashCode()), EventType.E_THROW_DIALOG))
        }
      }), (EventType.E_OPEN_DOOR, (event, delta) => {
        val player = event.args(0).asInstanceOf[Option[Entity]]
        val entityB = event.args(1).asInstanceOf[Option[Entity]]

        if (player.isDefined && entityB.isDefined && entityB.get == entity &&
          player.get.getComponent(InventoryComponent.id).isDefined) {
          if (player.get.getComponent(InventoryComponent.id).get.inv.removeOneOfType(Key())) {
            // entity.dispose()
            // entity.destroy = true
            entity.removeComponent(RenderComponent.id)
            entity.removeComponent(CollisionComponent.id)
          } else {
            val d = Factory.createDialog(Vector(
              ("Ok.. :(", new Event(Vector(), EventType.E_NONE))),
              "You need key to open door", None, 40, 6)
            EventManager.addEvent(new Event(Vector(d, entity.hashCode()), EventType.E_THROW_DIALOG))
          }
        }
      }))
    entity.description = "door"

    val spatialComp = new SpatialComponent()
    spatialComp.forward = (Utility.rotateY(((rotation / 360f) * 2 * Math.PI).toFloat) * Vec4(0, 0, 1, 0)).xyz

    spatialComp.position = Vec3(loc.x * 2 / 16, 0.0f, loc.y * 2 / 16)
    //    spatialComp.forward = Vec3(1.0f, 0, 0)
    entity.addComponent(spatialComp)

    var renderComp = new RenderComponent("door", Some("door_tex"))
    entity.addComponent(renderComp)
    if (rotation == 0) {
      var collisionComponent = new CollisionComponent(
        w / 16, CollisionComponent.SQUARE,
        halfWidth = w / 2, halfHeight = h / 2)
      entity.addComponent(collisionComponent)
    } else {
      var collisionComponent = new CollisionComponent(
        h / 16, CollisionComponent.SQUARE,
        halfWidth = h / 2, halfHeight = w / 2)
      entity.addComponent(collisionComponent)
    }
    entity
  }

  def createOpenDoor(node: Node) = {
    // TODO: Fix magic size and location conversion
    val name = (node \ "@name").text
    val typeName = (node \ "@name").text
    val loc = Vec2((node \ "@x").text.toFloat, (node \ "@y").text.toFloat)
    //    val rotation = (node \ "@name").text
    val w = (node \ "@width").text.toFloat / 8 + 0.1f
    val h = (node \ "@height").text.toFloat / 8 + 0.1f

    val entity = new Entity()

    entity.description = "open door"

    val spatialComp = new SpatialComponent()
    spatialComp.position = Vec3(loc.x * 2 / 16, 0.0f, loc.y * 2 / 16)
    entity.addComponent(spatialComp)

    var renderComp = new RenderComponent("doorTop", Some("testTex"))
    entity.addComponent(renderComp)

    entity
  }

  def createTestEnemy(node: Node) = {
    // TODO: Fix magic size and location conversion
    val name = (node \ "@name").text
    val typeName = (node \ "@name").text
    val loc = Vec2((node \ "@x").text.toFloat, (node \ "@y").text.toFloat)
    val size = (node \ "@width").text.toFloat
    val entity: Entity = new Entity()

    entity.description = "test enemy"
    
    val spatialComp = new SpatialComponent()
    spatialComp.position = Vec3(loc.x * 2 / 16, 1.0f, loc.y * 2 / 16)
    entity.addComponent(spatialComp)

    entity.eventHandlers = scala.collection.immutable.Map(
      (EventType.E_COLLISION, (event, delta) => {
        val entityA = event.args(0).asInstanceOf[Entity]
        val entityB = event.args(1).asInstanceOf[Entity]
        if (entityA == entity && !entityB.destroy) {
          val damageComponent = entityB.getComponent(DamageComponent.id)
          if (damageComponent.isDefined && damageComponent.get.canDamage == DamageComponent.ENEMY) {
            val healthComp = entity.getComponent(HealthComponent.id).get
            healthComp.hp -= 1
            if (healthComp.hp <= 0) {
              entity.destroy = true
              EventManager.addEvent(new Event(Vector(spatialComp.position), 
                  EventType.E_EXPLOSION))
            }
            if (entityB.getComponent(BreakableComponent.id).isDefined) {
              entityB.destroy = true
            }
          }
        }
      }))

    val renderComp = new RenderComponent("test_enemy", Some("test_enemy_tex"))
    entity.addComponent(renderComp)

    val faceCameraComp = new FaceCameraComponent()
    entity.addComponent(faceCameraComp)

    val AIComponent = new AIComponent("psychobot")
    entity.addComponent(AIComponent)

    val damageComp = new DamageComponent(2, DamageComponent.PLAYER)
    entity.addComponent(damageComp)

    val healthComp = new HealthComponent(4)
    entity.addComponent(healthComp)

    var collisionComponent = new CollisionComponent(size / 16, CollisionComponent.CIRCLE)
    entity.addComponent(collisionComponent)
    entity
  }

  def createRupee(node: Node) = {
    // TODO: Fix magic size and location conversion
    val name = "Pages" //(node \ "@name").text
    val typeName = (node \ "@name").text
    val loc = Vec2((node \ "@x").text.toFloat, (node \ "@y").text.toFloat)
    val size = (node \ "@width").text.toFloat

    val entity = new Entity()

    entity.description = "rupee"

    entity.eventHandlers = scala.collection.immutable.Map(
      (EventType.E_COLLISION, (event, delta) => {
        val entityA = event.args(0).asInstanceOf[Entity]
        val entityB = event.args(1).asInstanceOf[Entity]
        val entBinventory = entityB.getComponent(InventoryComponent.id)

        if (entityA == entity && entBinventory.isDefined) {
          println("Rupee pickup")
          entBinventory.get.inv.addItem(entity.getComponent(InventoryItemComponent.id).get.invItem)
          entity.destroy = true
        }
      }), (EventType.E_INTERACTION, (event, delta) => {
        val entityA = event.args(0).asInstanceOf[Option[Entity]]
        val entityB = event.args(1).asInstanceOf[Option[Entity]]

        if (entityB.isDefined && entityB.get == entity) {
          println("Rupee Interaction")
        }
      }))

    val spatialComp = new SpatialComponent()
    spatialComp.position = Vec3(loc.x * 2 / 16, 0.0f, loc.y * 2 / 16)
    spatialComp.scale = Vec3(0.5f, 0.5f, 0.5f)
    entity.addComponent(spatialComp)

    val invComponent = new InventoryItemComponent(Rupee())
    entity.addComponent(invComponent)

    var renderComp = new RenderComponent("rupee")
    entity.addComponent(renderComp)

    val rotateComp = new RotateComponent(-0.2f)
    entity.addComponent(rotateComp)
    // TODO: Fix magic size conversion
    var collisionComponent = new CollisionComponent(size / 16, CollisionComponent.CIRCLE)
    collisionComponent.isActive = false
    entity.addComponent(collisionComponent)
    entity
  }

  def createPlayer(node: Node) = {
    val name = (node \ "@name").text
    val typeName = (node \ "@name").text
    val loc = Vec2((node \ "@x").text.toFloat, (node \ "@y").text.toFloat)
    val size = (node \ "@width").text.toFloat

    val player = new Entity()

    val healthComponent = new HealthComponent(3)
    player.addComponent(healthComponent)

    player.eventHandlers = scala.collection.immutable.Map(
      (EventType.E_COLLISION, (event, delta) => {
        val entityA = event.args(0).asInstanceOf[Entity]
        val entityB = event.args(1).asInstanceOf[Entity]

        val damageComponent = entityB.getComponent(DamageComponent.id)

        if (entityA == player && damageComponent.isDefined) {
          if (damageComponent.get.canDamage == DamageComponent.PLAYER) {
            if (healthComponent.invulnerabilityTimer <= 0) {
              healthComponent.hp -= 1
              EventManager.addEvent(new Event(Vector(), EventType.E_PLAYER_DAMAGE))
              healthComponent.invulnerabilityTimer = 5.0f
              if (healthComponent.hp <= 0) {
                println("You're dead")
              }
            }
          }
        }
      }))

    player.description = "player"

    val spatialComp = new SpatialComponent()
    spatialComp.position =
      Vec3(loc.x * 2 / 16 + 0.0001f, 1.4f, loc.y * 2 / 16 + 0.0001f)
    player.addComponent(spatialComp)

    val inventoryComponent = new InventoryComponent()
    player.addComponent(inventoryComponent)

    val collisionComponent = 
      new CollisionComponent(
          0.3f, CollisionComponent.CIRCLE, 
          collisionType = CollisionComponent.PLAYER)
    player.addComponent(collisionComponent)
    
    var renderComp = new RenderComponent("chest", Some("chest"))
    player.addComponent(renderComp)
    
    val inputComponent = new InputComponent()
    player.addComponent(inputComponent)

    val playerComponent = new PlayerComponent()
    player.addComponent(playerComponent)

    EventManager.addEvent(new Event(Vector(player), EventType.E_PLAYER_CREATION))

    player
  }
  
  def createBreakableWall(node: Node) = {
    // TODO: Fix magic size and location conversion
    val loc = Vec2((node \ "@x").text.toFloat, (node \ "@y").text.toFloat)
    val w = (node \ "@width").text.toFloat / 8
    val h = (node \ "@height").text.toFloat / 8
    val entity: Entity = new Entity()
        
    val spatialComp = new SpatialComponent()
    spatialComp.position = Vec3(loc.x * 2 / 16, 0.0f, loc.y * 2 / 16)
    entity.addComponent(spatialComp)
    
    entity.eventHandlers = scala.collection.immutable.Map(
      (EventType.E_COLLISION, (event, delta) => {
        val entityA = event.args(0).asInstanceOf[Entity]
        val entityB = event.args(1).asInstanceOf[Entity]
        if (entityA == entity && !entityB.destroy) {
          val damageComponent = entityB.getComponent(DamageComponent.id)
          if (damageComponent.isDefined && damageComponent.get.canDamage == DamageComponent.ENEMY) {
            entity.destroy = true
            if (entityB.getComponent(BreakableComponent.id).isDefined) {
              entityB.destroy = true
            }
            EventManager.addEvent(new Event(Vector(spatialComp.position), 
                  EventType.E_EXPLOSION))
          }
        }
      }))
      
    spatialComp.scale = Vec3(w / 2, 1.0f, h / 2)

    val renderComp = new RenderComponent("uv_cube", Some("testTex"))
    entity.addComponent(renderComp)

    var collisionComponent = new CollisionComponent(
        h / 16, CollisionComponent.SQUARE,
        halfWidth = h / 2, halfHeight = w / 2)
    collisionComponent.isStatic = true
    
    entity.addComponent(collisionComponent)
    entity
  }
  
  def createUnbreakableWall(node: Node) = {
    // TODO: Fix magic size and location conversion
    val loc = Vec2((node \ "@x").text.toFloat, (node \ "@y").text.toFloat)
    val w = (node \ "@width").text.toFloat / 8
    val h = (node \ "@height").text.toFloat / 8
    val entity: Entity = new Entity()
        
    val spatialComp = new SpatialComponent()
    spatialComp.position = Vec3(loc.x * 2 / 16, 0.0f, loc.y * 2 / 16)
    entity.addComponent(spatialComp)
      
    spatialComp.scale = Vec3(w / 2, 1.0f, h / 2)

    val renderComp = new RenderComponent("uv_cube", Some("testTex"))
    entity.addComponent(renderComp)

    var collisionComponent = new CollisionComponent(
        h / 16, CollisionComponent.SQUARE,
        halfWidth = h / 2, halfHeight = w / 2)
    collisionComponent.isStatic = true
    
    entity.addComponent(collisionComponent)
    entity
  }
  
  def createExplosion(position: Vec3) = {
    var entity = new Entity()

    entity.description = "Explosion"

    var spatialComp = new SpatialComponent()
    spatialComp.position = position
    spatialComp.position.y += 1.0f

    entity.addComponent(spatialComp)
    
    var faceCameraComp = new FaceCameraComponent()
    entity.addComponent(faceCameraComp)

    var renderComp = new RenderComponent("test_enemy", Some("exp1"))
    entity.addComponent(renderComp)

    var animationComp = new AnimationComponent(Vector("exp1", "exp2", "exp3", "exp4", "exp5"), 0.8)
    entity.addComponent(animationComp)
    
    var deathTimerComp = new DeathTimerComponent(0.8 * 5)
    entity.addComponent(deathTimerComp)
    
    entity
  }

  def createDialog(options: Vector[(String, Event)],
                   text: String, parent: Option[Listener],
                   w: Int, h: Int): Dialog = {
    var dialogOptions: Array[Tuple2[String, Event]] = Array[Tuple2[String, Event]](
      ("First Choice", new Event(Vector("EkaValinta", parent.hashCode()), EventType.E_ANSWER_DIALOG)),
      ("Second Choice", new Event(Vector("TokaValinta", parent.hashCode()), EventType.E_ANSWER_DIALOG)))

    var dialog = new Dialog(parent,
      new Rectangle2D(w, h, true), text,
      options)
    dialog
  }
}