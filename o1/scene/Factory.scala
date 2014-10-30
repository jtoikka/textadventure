package o1.scene

import scala.collection.mutable.Buffer
import o1.inventory.Coffee

/**
 * The Factory object is a collection of functions that can be used to create
 * entities with various characteristics.
 */

object Factory {

  def createPlayer() = {
    var player = new Entity()

    var spatialComp = new SpatialComponent()
    player.addComponent(spatialComp)
    
    var collisionComponent = new CollisionComponent(0.6f, Buffer[Int]())
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

    var collisionComponent = new CollisionComponent(1.0f, Buffer[Int]())
    monkey.addComponent(collisionComponent)

    monkey.addComponent(renderComp)
    monkey
  }

  def createCoffee() = {
    var cof = new Entity()
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
}