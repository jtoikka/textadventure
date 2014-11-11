package o1.scene

import scala.collection.mutable.Buffer
import o1.inventory.Coffee
import scala.xml.Node
import o1.math.Vec2
import o1.math.Vec3
import o1.inventory.Page
import o1.event.EventType

/**
 * The Factory object is a collection of functions that can be used to create
 * entities with various characteristics.
 */

object Factory {

  def createPlayer() = {
    var player = new Entity(Vector())

    var spatialComp = new SpatialComponent()
    player.addComponent(spatialComp)

    var collisionComponent = new CollisionComponent(0.6f, Buffer[Int]())
    player.addComponent(collisionComponent)

    var inputComponent = new InputComponent()
    player.addComponent(inputComponent)
    player
  }

  def createCamera(followEntity: Entity) = {
    var camera = new Entity(Vector())

    var spatialComp = new SpatialComponent()
    camera.addComponent(spatialComp)

    var followComponent = new FollowComponent(followEntity)
    camera.addComponent(followComponent)

    camera
  }

  def createSphere() = {
    var sphere = new Entity(Vector())
    var spatialComp = new SpatialComponent()

    sphere.addComponent(spatialComp)

    var renderComp = new RenderComponent("sphere")

    sphere.addComponent(renderComp)
    sphere
  }

  def createCube() = {
    var sphere = new Entity(Vector())
    var spatialComp = new SpatialComponent()

    sphere.addComponent(spatialComp)

    var renderComp = new RenderComponent("cube")

    sphere.addComponent(renderComp)
    sphere
  }

  def createMonkey() = {
    var monkey = new Entity(Vector())
    var spatialComp = new SpatialComponent()

    monkey.addComponent(spatialComp)

    var renderComp = new RenderComponent("monkey")

    var collisionComponent = new CollisionComponent(1.0f, Buffer[Int]())
    monkey.addComponent(collisionComponent)

    monkey.addComponent(renderComp)
    monkey
  }

  def createCoffee() = {
    var cof = new Entity(Vector())
    var spatialComp = new SpatialComponent()

    cof.addComponent(spatialComp)

    var renderComp = new RenderComponent("coffee")

    var collisionComponent = new CollisionComponent(1.0f, Buffer[Int]())
    collisionComponent.isActive = false
    cof.addComponent(collisionComponent)

    val invComponent = new InventoryItemComponent(Coffee())

    cof.addComponent(invComponent)

    cof.addComponent(renderComp)
    cof
  }
  
  def createCoffeeBullet(position: Vec3, direction: Vec3) = {
    var cof = new Entity(Vector(EventType.E_COLLISION))
    
    cof.eventHandle = (event, delta) => {
      val entityA = event.args(0).asInstanceOf[Entity]
      val entityB = event.args(1).asInstanceOf[Entity]
      
      val intersection = event.args(2).asInstanceOf[Vec2]
      
      val physics = cof.getComponent(PhysicsComponent.id).get
      
//      if (entityB.getComponent(DamageComponent.id).isDefined){
//        if (intersection.x.abs < intersection.y.abs) {
//          physics.velocity.x = 0.0f
//        } else {
//          physics.velocity.z = 0.0f
//        }
//      }
      if (entityA == cof) {
        if (entityB.getComponent(DamageComponent.id).isDefined) {
          cof.destroy = true
        }
      }
    } 
    
    var spatialComp = new SpatialComponent()
    spatialComp.position = position
    spatialComp.position.y = 0.8f

    cof.addComponent(spatialComp)
    
    val physicsComp = new PhysicsComponent(Vec3(direction.x, 0.2f, direction.z), Vec3(0.0f, -0.0981f, 0.0f))
    cof.addComponent(physicsComp)

    var renderComp = new RenderComponent("coffee")

    var collisionComponent = new CollisionComponent(0.1f, Buffer[Int]())
    collisionComponent.isActive = false
    cof.addComponent(collisionComponent)
    
    var damageComp = new DamageComponent(1)
    cof.addComponent(damageComp)

    cof.addComponent(renderComp)
    cof
  }

  def createPlate() = {
    var monkey = new Entity(Vector())
    var spatialComp = new SpatialComponent()

    monkey.addComponent(spatialComp)

    var renderComp = new RenderComponent("plate")

    monkey.addComponent(renderComp)
    monkey
  }

  def createLevel() = {
    var monkey = new Entity(Vector())
    var spatialComp = new SpatialComponent()

    monkey.addComponent(spatialComp)

    var renderComp = new RenderComponent("testMap")

    monkey.addComponent(renderComp)
    monkey
  }

  def createFloor() = {
    var floor = new Entity(Vector())
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

    val entity = new Entity(Vector())

    val spatialComp = new SpatialComponent()
    spatialComp.position = Vec3((loc.x * 2) / 16, 0.5f, loc.y * 2 / 16)
    entity.addComponent(spatialComp)

    val invComponent = new InventoryItemComponent(Coffee())
    entity.addComponent(invComponent)

    var renderComp = new RenderComponent("coffee")
    entity.addComponent(renderComp)

    var collisionComponent = new CollisionComponent(size / 16, Buffer[Int]())
    collisionComponent.isActive = false
    entity.addComponent(collisionComponent)
    entity
  }

  def createPage(node: Node) = {
    // TODO: Fix magic size and location conversion
    val name = "Pages"//(node \ "@name").text
    val typeName = (node \ "@name").text
    val loc = Vec2((node \ "@x").text.toFloat, (node \ "@y").text.toFloat)
    val size = (node \ "@width").text.toFloat

    val entity = new Entity(Vector())

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
    var collisionComponent = new CollisionComponent(size / 16, Buffer[Int]())
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

    val entity = new Entity(Vector())

    val spatialComp = new SpatialComponent()
    spatialComp.position = Vec3(loc.x * 2 / 16, 0.5f, loc.y * 2 / 16)
    entity.addComponent(spatialComp)

    var renderComp = new RenderComponent("monkey")
    entity.addComponent(renderComp)
    
    var faceCameraComp = new FaceCameraComponent()
    entity.addComponent(faceCameraComp)
    

    var collisionComponent = new CollisionComponent(size / 16, Buffer[Int]())
    entity.addComponent(collisionComponent)
    entity
  }
  
  def createTestEnemy(node: Node) = {
    // TODO: Fix magic size and location conversion
    val name = (node \ "@name").text
    val typeName = (node \ "@name").text
    val loc = Vec2((node \ "@x").text.toFloat, (node \ "@y").text.toFloat)
    val size = (node \ "@width").text.toFloat

    val entity: Entity = new Entity(Vector(EventType.E_COLLISION))
    
    entity.eventHandle = (event, delta) => {
      val entityA = event.args(0).asInstanceOf[Entity]
      val entityB = event.args(1).asInstanceOf[Entity]
      if (entityA == entity) {
        if (entityB.getComponent(DamageComponent.id).isDefined) {
          println("Took damage")
        }
      }
    } 

    val spatialComp = new SpatialComponent()
    spatialComp.position = Vec3(loc.x * 2 / 16, 1.0f, loc.y * 2 / 16)
    entity.addComponent(spatialComp)

    val renderComp = new RenderComponent("test_enemy", Some("test_enemy_tex"))
    entity.addComponent(renderComp)
    
    val faceCameraComp = new FaceCameraComponent()
    entity.addComponent(faceCameraComp)
    
    val AIComponent = new AIComponent("lovebot")
    entity.addComponent(AIComponent)
    
    val damageComp = new DamageComponent(2)
    entity.addComponent(damageComp)
    
//    val listenerComp = new ListenerComponent(
//        Vector(EventType.E_COLLISION), (event, delta) => {
//      val entityA = event.args(0).asInstanceOf[Entity]
//      val entityB = event.args(1).asInstanceOf[Entity]
//      
//      if (entityB.getComponent(DamageComponent.id).isDefined) {
//        println("Took damage")
//      }
//    })
//    entity.addComponent(listenerComp)

    var collisionComponent = new CollisionComponent(size / 16, Buffer[Int]())
    entity.addComponent(collisionComponent)
    entity
  }
  
  def createPlayer(node: Node) = {
    val name = (node \ "@name").text
    val typeName = (node \ "@name").text
    val loc = Vec2((node \ "@x").text.toFloat, (node \ "@y").text.toFloat)
    val size = (node \ "@width").text.toFloat
    
    val player = new Entity(Vector())

    val spatialComp = new SpatialComponent()
    spatialComp.position = Vec3(loc.x * 2 / 16, 1.2f, loc.y * 2 / 16)
    player.addComponent(spatialComp)

    val collisionComponent = new CollisionComponent(0.3f, Buffer[Int]())
    player.addComponent(collisionComponent)

    val inputComponent = new InputComponent()
    player.addComponent(inputComponent)
    player
  }
}