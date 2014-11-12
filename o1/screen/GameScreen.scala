package o1.screen

import scala.swing.event.Key
import scala.collection.mutable.Map
import o1.adventure._
import o1.adventure.render._
import scala.math._
import o1.math._
import o1.scene._
import o1.adventure.render2D._
import o1.event.CollisionCheck
import o1.event._
import o1.event.EventType._
import o1.inventory.Inventory
import o1.inventory.Page
import o1.inventory.Coffee
import scala.collection.mutable.Buffer

/**
 * GameScreen class.
 * GameScreen can have its own renderer or use common renderer.
 * Game logic would go in this class.
 * @param parent parent Adventure class
 */
class GameScreen(parent: Adventure, rend: Renderer)
    extends Screen(parent, rend){
    def this(parent: Adventure, x: Int, y: Int) = this(parent, new Renderer3D(x, y))
  eventTypes = Vector[EventType](E_INPUT, E_DIALOG)

  var scene = new Scene()
  
  var paused = true
  
  val FORWARD = 0
  val RIGHT = 1
  val ROTATERIGHT = 2
  // Grabs movement inputs
  val movementMap = Map[Int, Float]()

  init()

  /**
   * Update method. Used to update game's state
   */

  def update(delta: Double): Unit = {
    if (!paused) {
      handleEvents(delta.toFloat)
      val entitiesAsVector = scene.entities.toVector
      val destroyedEntities = Buffer[Entity]()
      
      for (entity <- scene.entities) {
        val spatial = entity.getComponent(SpatialComponent.id)
        if (spatial.isDefined) {
          if (entity.getComponent(InputComponent.id).isDefined) {
            handleMovement(entity)
          }
          if (entity.getComponent(FaceCameraComponent.id).isDefined) {
            faceCamera(entity, scene.camera.get)
          }
          if (entity.getComponent(AIComponent.id).isDefined) {
            handleAI(entity, delta)
          }
          if (entity.getComponent(PhysicsComponent.id).isDefined) {
            Physics.applyPhysics(entity, delta)
          }
          CollisionCheck.checkCollisions(entity, entitiesAsVector, scene.world)
          if (entity.getComponent(SpatialComponent.id).get.position.y < 0) {
            entity.destroy = true
          }
          val rotateComponent = entity.getComponent(RotateComponent.id)
          if (rotateComponent.isDefined) {
            spatial.get.forward = (
                Utility.rotateY(rotateComponent.get.rate * delta.toFloat) * 
                Vec4(spatial.get.forward, 0.0f)).xyz.normalize()
          }
        }
        entity.handleEvents(delta.toFloat)
        
        if (entity.destroy) {
            destroyedEntities += entity
        }
      }
      
      for(e <- destroyedEntities){
        scene.removeEntity(e)
      }
      
      updateCamera()
      movementMap.clear()
    } else {
      events.clear()
    }
  }
  
  def handleAI(entity: Entity, delta: Double) = {
    val aiComponent = entity.getComponent(AIComponent.id).get
    AI.functionMap(aiComponent.botType)(entity, delta)
  }
  
  def faceCamera(entity: Entity, camera: Entity) = {
    val spatial = entity.getComponent(SpatialComponent.id)
    val cameraPos = camera.getComponent(SpatialComponent.id).get.position
    spatial.get.forward = (spatial.get.position + cameraPos).normalize.neg
    spatial.get.forward.x *= -1
  }
  
  def handleMovement(entity: Entity) = {
    val spatial = entity.getComponent(SpatialComponent.id).get
    var flippedForward = Vec3()
    flippedForward.x = -spatial.forward.x
    flippedForward.z = spatial.forward.z
    var right = spatial.up.cross(flippedForward)
    if (movementMap.contains(FORWARD)) {
      spatial.position += flippedForward * movementMap(FORWARD)
    }
    if (movementMap.contains(RIGHT)) {
      spatial.position -= right * movementMap(RIGHT)
    }
    if (movementMap.contains(ROTATERIGHT)) {
      spatial.forward = (
        Utility.rotateY(movementMap(ROTATERIGHT)) *
        Vec4(spatial.forward, 0.0f)).xyz
    }
  }

  private def updateCamera() = {
    var camSpatial = scene.camera.get.getComponent(SpatialComponent.id).get
    val camRight = camSpatial.up.cross(camSpatial.forward)

    val camFollow = scene.camera.get.getComponent(FollowComponent.id).get
    val followSpatial = camFollow.entity.getComponent(SpatialComponent.id)

    camSpatial.position = followSpatial.get.position.neg()

    camSpatial.forward.x = followSpatial.get.forward.x
    camSpatial.forward.y = followSpatial.get.forward.y
    camSpatial.forward.z = -followSpatial.get.forward.z

  }

  def handleEvent(event: Event, delta: Float) {
    if (event.eventType == EventType.E_INPUT) {
      val eventKey =
        event.args(0).asInstanceOf[Tuple2[scala.swing.event.Key.Value, Int]]
      if (inputMap.contains(eventKey)) {
        inputMap(eventKey)(delta)
      }
    }
    if (event.eventType == EventType.E_DIALOG && event.args(0) == this) {
      println("dialog says: \"" + event.args(1) + "\"")
    }
  }
  
  val SPEED = 0.30f

  val inputMap =
    Map[Tuple2[scala.swing.event.Key.Value, Int], (Float) => Unit](
      ((Key.Q, Input.KEYRELEASED), (delta) => {
        
      }),
      ((Key.Escape, Input.KEYRELEASED), (delta) => {
        EventManager.addEvent(new Event(Vector("menuScreen"), E_CHANGE_SCREEN))
      }),
      ((Key.I, Input.KEYRELEASED), (delta) => {
        EventManager.addEvent(new Event(Vector("inventoryScreen"), E_CHANGE_SCREEN))
      }),
      ((Key.N, Input.KEYRELEASED), (delta) => {
        EventManager.addEvent(new Event(Vector("helpMenuScreen"), E_CHANGE_SCREEN))
      }),
      ((Key.M, Input.KEYRELEASED), (delta) => {
        EventManager.addEvent(new Event(Vector("mapScreen"), E_CHANGE_SCREEN))
      }),
      ((Key.W, Input.KEYDOWN), (delta) => {
        movementMap(FORWARD) = SPEED * delta
      }),
      ((Key.S, Input.KEYDOWN), (delta) => {
        movementMap(FORWARD) = -SPEED * delta
      }),
      ((Key.A, Input.KEYDOWN), (delta) => {
        movementMap(RIGHT) = -SPEED * delta
      }),
      ((Key.D, Input.KEYDOWN), (delta) => {
        movementMap(RIGHT) = SPEED * delta
      }),
      ((Key.Left, Input.KEYDOWN), (delta) => {
        movementMap(ROTATERIGHT) = -0.2f * delta
      }),
      ((Key.Right, Input.KEYDOWN), (delta) => {
        movementMap(ROTATERIGHT) = 0.2f * delta
      }),
      ((Key.Space, Input.KEYPRESSED), (delta) => {
        println("Toss coffee")
        val cameraSpatial = scene.camera.get.getComponent(SpatialComponent.id).get
        scene.addEntity(Factory.createCoffeeBullet(cameraSpatial.position.neg() + cameraSpatial.forward.neg * 0.5f, cameraSpatial.forward.neg * 0.8f))
      }))

  /**
   * Draw method. Is used to draw screen to display etc
   */
  def draw(): String = {
    var drawStartTime: Long = System.currentTimeMillis()
    var tmpDisplay: String = ""

    rend.clear()
    rend.renderScene(scene)
    
    tmpDisplay = rend.display
    
    parent.screens("hudScreen").draw()
    parent.screens("hudScreen").rend.displayOverlay(tmpDisplay)
    
  }

  def init(): Unit = {
    scene.loadMap("00_testmap")
    EventManager.addEvent(new Event(Vector(scene.world), E_CHANGE_MAP))
  }

  def resume() {
    paused = false
  }

  def pause() {
    paused = true
  }

  def dispose() = {

  }
}