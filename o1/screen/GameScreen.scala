package o1.screen

import scala.concurrent.impl.Future
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.util.Success
import scala.util.Failure

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
    extends Screen(parent, rend) {
  def this(parent: Adventure, x: Int, y: Int) = this(parent, new Renderer3D(x, y))

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
            val rotY = Utility.rotateY(rotateComponent.get.rateForward * delta.toFloat)
            val rotZ = Utility.rotateZ(rotateComponent.get.rateUp * delta.toFloat)
            spatial.get.forward = (
              rotY *
              Vec4(spatial.get.forward, 0.0f)).xyz.normalize()

            spatial.get.up = (
              rotY *
              Vec4(spatial.get.up, 0.0f)).xyz.normalize()

            spatial.get.up = (
              rotZ *
              Vec4(spatial.get.up, 0.0f)).xyz.normalize()

            spatial.get.forward = (
              rotZ *
              Vec4(spatial.get.forward, 0.0f)).xyz.normalize()
          }
        }
        entity.handleEvents(delta.toFloat)

        if (entity.destroy) {
          destroyedEntities += entity
        }
      }

      for (e <- destroyedEntities) {
        scene.removeEntity(e)
      }

      traceFront()

      updateCamera()
      movementMap.clear()
    } else {
      events.clear()
    }
  }

  var prevFrontEntity: Option[Entity] = None
  
  def traceFront() = {
    val camSpatial = scene.camera.get.getComponent(SpatialComponent.id).get
    val forward = camSpatial.forward.neg
    val traced = RayTrace.trace(
      camSpatial.position.neg,
      forward.normalize(),
      scene, 2.0f, 40,
      (entity) => entity.getComponent(InputComponent.id).isEmpty)
    val inFront = traced._1
    if (inFront != prevFrontEntity) {
      prevFrontEntity = inFront
      EventManager.addEvent(new Event(Vector(inFront, traced._2), E_LOOKING_AT))
    }
  }

  def handleAI(entity: Entity, delta: Double) = {
    val aiComponent = entity.getComponent(AIComponent.id).get
    AI.functionMap(aiComponent.botType)(entity, delta, scene)
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

  eventHandlers = scala.collection.immutable.Map(
    (E_INPUT, (event, delta) => {
      val eventKey =
        event.args(0).asInstanceOf[Tuple2[scala.swing.event.Key.Value, Int]]
      if (inputMap.contains(eventKey)) {
        inputMap(eventKey)(delta)
      }
    }),
    (E_ANSWER_DIALOG, (event, delta) => {
      //      if (event.args(0) == this) {
      println("HashCode match: " + event.args(1) + ":" + this.hashCode())
      println("dialog says: \"" + event.args(0) + "\"")
      //      }
    }))

  val SPEED = 0.30f

  val inputMap =
    Map[Tuple2[scala.swing.event.Key.Value, Int], (Float) => Unit](
      ((Key.Q, Input.KEYRELEASED), (delta) => {

      }),
      ((Key.Escape, Input.KEYRELEASED), (delta) => {
//        EventManager.addEvent(new Event(Vector("menuScreen"), E_CHANGE_SCREEN))
        val d = Factory.createDialog(Vector(
          ("Yes", new Event(Vector("menuScreen"), E_CHANGE_SCREEN)),
          ("No", new Event(Vector("TokaValinta", this.hashCode()), E_ANSWER_DIALOG))),
          "Are you sure?", this, 30, 10)
        EventManager.addEvent(new Event(Vector(d, this.hashCode()), E_THROW_DIALOG))
      }),
      ((Key.I, Input.KEYRELEASED), (delta) => {
        EventManager.addEvent(new Event(Vector("inventoryScreen"), E_CHANGE_SCREEN))
      }),
      ((Key.N, Input.KEYRELEASED), (delta) => {
        //        EventManager.addEvent(new Event(Vector("helpMenuScreen"), E_CHANGE_SCREEN))
        EventManager.addEvent(new Event(Vector("helpMenuScreen"), E_TEST))
      }),
      ((Key.M, Input.KEYRELEASED), (delta) => {
        val d = Factory.createDialog(Vector(
          ("First Choice", new Event(Vector("EkaValinta", this.hashCode()), E_ANSWER_DIALOG)),
          ("Second Choice", new Event(Vector("TokaValinta", this.hashCode()), E_ANSWER_DIALOG))),
          "sdsdfsdf", this, 40, 10)
        EventManager.addEvent(new Event(Vector(d, this.hashCode()), E_THROW_DIALOG))

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
        val coffee = Factory.createCoffeeBullet(
          cameraSpatial.position.neg() +
            cameraSpatial.forward.neg * 0.5f,
          cameraSpatial.forward.neg * 0.8f)
        val spatial = coffee.getComponent(SpatialComponent.id).get
        spatial.forward = cameraSpatial.forward
        spatial.forward.x *= -1
        scene.addEntity(coffee)
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