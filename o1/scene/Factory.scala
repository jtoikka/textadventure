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

/**
 * The Factory object is a collection of functions that can be used to create
 * entities with various characteristics.
 */

object Factory {

  def createPlayer() = {
    var player = new Entity()

    var spatialComp = new SpatialComponent()
    player.addComponent(spatialComp)

    var collisionComponent = new CollisionComponent(0.6f, CollisionComponent.CIRCLE)
    player.addComponent(collisionComponent)

    var inputComponent = new InputComponent()
    player.addComponent(inputComponent)
    player
  }

  def createCamera(followEntity: Entity) = {
    var camera = new Entity()

    var spatialComp = new SpatialComponent()
    camera.addComponent(spatialComp)

    var followComponent = new FollowComponent(followEntity)
    camera.addComponent(followComponent)

    camera
  }

  def createSphere() = {
    var sphere = new Entity()
    var spatialComp = new SpatialComponent()

    sphere.addComponent(spatialComp)

    var renderComp = new RenderComponent("sphere")

    sphere.addComponent(renderComp)
    sphere
  }

  def createCube() = {
    var sphere = new Entity()
    var spatialComp = new SpatialComponent()

    sphere.addComponent(spatialComp)

    var renderComp = new RenderComponent("cube")

    sphere.addComponent(renderComp)
    sphere
  }

  def createMonkey() = {
    var monkey = new Entity()
    var spatialComp = new SpatialComponent()

    monkey.addComponent(spatialComp)

    var renderComp = new RenderComponent("monkey")

    var collisionComponent = new CollisionComponent(1.0f, CollisionComponent.CIRCLE)
    monkey.addComponent(collisionComponent)

    monkey.addComponent(renderComp)
    monkey
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
    //    var faceCamComp = new FaceCameraComponent()
    //    cof.addComponent(faceCamComp)

    val rotateComp = new RotateComponent(rateUp = 0.1f)
    cof.addComponent(rotateComp)

    var spatialComp = new SpatialComponent()
    spatialComp.position = position
    spatialComp.position.y = 0.8f

    cof.addComponent(spatialComp)

    val physicsComp = new PhysicsComponent(Vec3(direction.x, 0.2f, direction.z), Vec3(0.0f, -0.0981f, 0.0f))
    cof.addComponent(physicsComp)

    var renderComp = new RenderComponent("coffee")

    var collisionComponent = new CollisionComponent(0.15f, CollisionComponent.CIRCLE)
    collisionComponent.isActive = true
    cof.addComponent(collisionComponent)

    var damageComp = new DamageComponent(1, DamageComponent.ENEMY)
    cof.addComponent(damageComp)

    var breakableComp = new BreakableComponent()
    cof.addComponent(breakableComp)

    cof.addComponent(renderComp)
    cof
  }

  def createPlate() = {
    var monkey = new Entity()
    var spatialComp = new SpatialComponent()

    monkey.addComponent(spatialComp)

    var renderComp = new RenderComponent("plate")

    monkey.addComponent(renderComp)
    monkey
  }

  def createLevel() = {
    var monkey = new Entity()
    var spatialComp = new SpatialComponent()

    monkey.addComponent(spatialComp)

    var renderComp = new RenderComponent("testMap")

    monkey.addComponent(renderComp)
    monkey
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
      case "door" => Some(createVerticalDoor(node))
      case "rupee" => Some(createRupee(node))
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
          println("page pickup")
          entBinventory.get.inv.addItem(entity.getComponent(InventoryItemComponent.id).get.invItem)
          entity.destroy = true
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
    spatialComp.position = Vec3(loc.x * 2 / 16, 0.5f, loc.y * 2 / 16)
    entity.addComponent(spatialComp)

    var renderComp = new RenderComponent("monkey")
    entity.addComponent(renderComp)

    var faceCameraComp = new FaceCameraComponent()
    entity.addComponent(faceCameraComp)

    var collisionComponent = new CollisionComponent(
      size / 16, CollisionComponent.SQUARE,
      halfWidth = 1.0f, halfHeight = 3.0f)
    entity.addComponent(collisionComponent)
    entity
  }

  def createVerticalDoor(node: Node) = {
    // TODO: Fix magic size and location conversion
    val name = (node \ "@name").text
    val typeName = (node \ "@name").text
    val loc = Vec2((node \ "@x").text.toFloat, (node \ "@y").text.toFloat)
    val w = (node \ "@width").text.toFloat / 8
    val h = (node \ "@height").text.toFloat / 8

    val entity = new Entity()
    
    entity.description = "door"

    val spatialComp = new SpatialComponent()
    spatialComp.position = Vec3(loc.x * 2 / 16, 0.0f, loc.y * 2 / 16)
    entity.addComponent(spatialComp)

    var renderComp = new RenderComponent("verticaldoor")
    entity.addComponent(renderComp)

    var collisionComponent = new CollisionComponent(
      w / 16, CollisionComponent.SQUARE,
      halfWidth = w / 2, halfHeight = h / 2)
    println(w / 2 + ", " + h / 2)
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

    val AIComponent = new AIComponent("lovebot")
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
          println("page pickup")
          entBinventory.get.inv.addItem(entity.getComponent(InventoryItemComponent.id).get.invItem)
          entity.destroy = true
        }
      }))

    val spatialComp = new SpatialComponent()
    spatialComp.position = Vec3(loc.x * 2 / 16, 0.5f, loc.y * 2 / 16)
    spatialComp.scale = Vec3(0.5f, 0.5f, 0.5f)
    entity.addComponent(spatialComp)

    val invComponent = new InventoryItemComponent(Page())
    entity.addComponent(invComponent)

    var renderComp = new RenderComponent("rupee")
    entity.addComponent(renderComp)

    //    var faceCameraComp = new FaceCameraComponent()
    //    entity.addComponent(faceCameraComp)

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

    val spatialComp = new SpatialComponent()
    spatialComp.position = Vec3(loc.x * 2 / 16, 1.2f, loc.y * 2 / 16)
    player.addComponent(spatialComp)

    player.addComponent(new InventoryComponent())

    val collisionComponent = new CollisionComponent(0.3f, CollisionComponent.CIRCLE)
    player.addComponent(collisionComponent)

    val inputComponent = new InputComponent()
    player.addComponent(inputComponent)
    EventManager.addEvent(new Event(Vector(player), EventType.E_PLAYER_CREATION))
    player
  }

  def createDialog(options: Vector[(String, Event)], 
      text: String,parent:Listener,
      w:Int,h:Int):Dialog = {
    var dialogOptions: Array[Tuple2[String, Event]] = Array[Tuple2[String, Event]](
      ("First Choice", new Event(Vector("EkaValinta", parent.hashCode()), EventType.E_ANSWER_DIALOG)),
      ("Second Choice", new Event(Vector("TokaValinta", parent.hashCode()), EventType.E_ANSWER_DIALOG)))

    var dialog = new Dialog(parent,
      new Rectangle2D(w, h, true),text,
      options)
    dialog
  }
}