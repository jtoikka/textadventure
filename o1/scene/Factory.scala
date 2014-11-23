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

/**
 * The Factory object is a collection of functions that can be used to create
 * entities with various characteristics.
 */

object Factory {

  //  def createPlayer() = {
  //    var player = new Entity()
  //
  //    var spatialComp = new SpatialComponent()
  //    player.addComponent(spatialComp)
  //
  //    var collisionComponent = new CollisionComponent(0.6f, CollisionComponent.CIRCLE)
  //    player.addComponent(collisionComponent)
  //
  //    var inputComponent = new InputComponent()
  //    player.addComponent(inputComponent)
  //    player
  //  }

  def createCamera(followEntity: Entity) = {
    var camera = new Entity()

    var spatialComp = new SpatialComponent()
    camera.addComponent(spatialComp)

    var followComponent = new FollowComponent(followEntity)
    camera.addComponent(followComponent)

    camera
  }
  //  def createSphere() = {
  //    var sphere = new Entity()
  //    var spatialComp = new SpatialComponent()
  //
  //    sphere.addComponent(spatialComp)
  //
  //    var renderComp = new RenderComponent("sphere")
  //
  //    sphere.addComponent(renderComp)
  //    sphere
  //  }

  //  def createCube() = {
  //    var sphere = new Entity()
  //    var spatialComp = new SpatialComponent()
  //
  //    sphere.addComponent(spatialComp)
  //
  //    var renderComp = new RenderComponent("cube")
  //
  //    sphere.addComponent(renderComp)
  //    sphere
  //  }

  //  def createMonkey() = {
  //    var monkey = new Entity()
  //    var spatialComp = new SpatialComponent()
  //
  //    monkey.addComponent(spatialComp)
  //
  //    var renderComp = new RenderComponent("monkey")
  //
  //    var collisionComponent = new CollisionComponent(1.0f, CollisionComponent.CIRCLE)
  //    monkey.addComponent(collisionComponent)
  //
  //    monkey.addComponent(renderComp)
  //    monkey
  //  }

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

    cof.eventHandlers = scala.collection.immutable.Map(
      (EventType.E_COLLISION, (event, delta) => {
        val entityA = event.args(0).asInstanceOf[Entity]
        val entityB = event.args(1).asInstanceOf[Entity]

        if (entityA == cof) {
          if (entityB.getComponent(DamageComponent.id).isDefined) {
            cof.destroy = true
          }
        }
      }))

    val rotateComp = new RotateComponent(rateUp = 0.1f)
    cof.addComponent(rotateComp)

    var spatialComp = new SpatialComponent()
    spatialComp.position = position
    spatialComp.position.y = 0.8f

    cof.addComponent(spatialComp)

    val physicsComp = new PhysicsComponent(Vec3(direction.x, 0.2f, direction.z), Vec3(0.0f, -0.0981f, 0.0f))
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
      case "player" => Some(createPlayer(node))
      case "door" => Some(createDoor(node))
      case "rupee" => Some(createRupee(node))
      case "key" => Some(createKey(node))
      case "shop" => Some(createShop(node))
      case "chest" => Some(createChest(node))
      case "levelTrigger" => Some(createLevelTrigger(node))
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

    val entity = new Entity()
    entity.eventHandlers = scala.collection.immutable.Map()
    entity.description = "chest"

    val spatialComp = new SpatialComponent()
    spatialComp.position = Vec3(loc.x * 2 / 16, 0.0f, loc.y * 2 / 16)
    spatialComp.forward = Vec3(1.0f, 0, 0)
    entity.addComponent(spatialComp)

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
    val level = ((node \ "properties" \ "property").filter(a => (a \ "@name").text == "level") \ "@value")
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
//          EventManager.addEvent()
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
    entity.addComponent(collisionComponent)

    entity
  }

  def createDoor(node: Node) = {
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
            entity.dispose()
            entity.destroy = true
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
    spatialComp.position = Vec3(loc.x * 2 / 16, 0.0f, loc.y * 2 / 16)
    spatialComp.forward = Vec3(1.0f, 0, 0)
    entity.addComponent(spatialComp)

    var renderComp = new RenderComponent("door", Some("door_tex"))
    entity.addComponent(renderComp)

    var collisionComponent = new CollisionComponent(
      w / 16, CollisionComponent.SQUARE,
      halfWidth = w / 2, halfHeight = h / 2)
    entity.addComponent(collisionComponent)

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
            }
            if (entityB.getComponent(BreakableComponent.id).isDefined) {
              entityB.destroy = true
            }
          }
        }
      }))

    val spatialComp = new SpatialComponent()
    spatialComp.position = Vec3(loc.x * 2 / 16, 1.0f, loc.y * 2 / 16)
    entity.addComponent(spatialComp)

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

    player.description = "player"

    val spatialComp = new SpatialComponent()
    spatialComp.position = Vec3(loc.x * 2 / 16 + 0.0001f, 1.2f, loc.y * 2 / 16)
    player.addComponent(spatialComp)

    val inventoryComponent = new InventoryComponent()
    player.addComponent(inventoryComponent)

    val collisionComponent = new CollisionComponent(0.3f, CollisionComponent.CIRCLE)
    player.addComponent(collisionComponent)

    val inputComponent = new InputComponent()
    player.addComponent(inputComponent)

    val playerComponent = new PlayerComponent()
    player.addComponent(playerComponent)

    EventManager.addEvent(new Event(Vector(player), EventType.E_PLAYER_CREATION))

    player
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