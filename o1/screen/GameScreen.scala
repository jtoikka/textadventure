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


  /* HUD -------------------------------------*/
  var rendHUD = new Renderer2D(rend.w, rend.h)
  var hudTextRect: Option[TextRect2D] = None
  var lastDrawTime: Long = 0
  var sceneHUD = new Scene()
  var showHUD = true;
  /* -----------------------------------------*/

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
        if (entity.getComponent(SpatialComponent.id).isDefined) {
          if (entity.getComponent(InputComponent.id).isDefined) {
            handleMovement(entity)
          }
          CollisionCheck.checkCollisions(entity, entitiesAsVector, scene.world)
        }
        
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

    updateHUD(camSpatial.position)
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

  val inputMap =
    Map[Tuple2[scala.swing.event.Key.Value, Int], (Float) => Unit](
      ((Key.Q, Input.KEYRELEASED), (delta) => {
        showHUD = !showHUD
      }),
      ((Key.Escape, Input.KEYRELEASED), (delta) => {
        EventManager.addEvent(new Event(Vector("menuScreen"), E_CHANGE_SCREEN))
      }),
      ((Key.I, Input.KEYRELEASED), (delta) => {
        EventManager.addEvent(new Event(Vector("inventoryScreen"), E_CHANGE_SCREEN))
      }),
      ((Key.N, Input.KEYRELEASED), (delta) => {
        if (Inventory.addItem(Page())) println("Added Page to inventory")

      }),
      ((Key.M, Input.KEYRELEASED), (delta) => {
        if (Inventory.addItem(Coffee())) println("Added Coffee to inventory")
      }),
      ((Key.W, Input.KEYDOWN), (delta) => {
        movementMap(FORWARD) = 0.15f * delta
      }),
      ((Key.S, Input.KEYDOWN), (delta) => {
        movementMap(FORWARD) = -0.15f * delta
      }),
      ((Key.A, Input.KEYDOWN), (delta) => {
        movementMap(RIGHT) = -0.15f * delta
      }),
      ((Key.D, Input.KEYDOWN), (delta) => {
        movementMap(RIGHT) = 0.15f * delta
      }),
      ((Key.Left, Input.KEYDOWN), (delta) => {
        movementMap(ROTATERIGHT) = -0.2f * delta
      }),
      ((Key.Right, Input.KEYDOWN), (delta) => {
        movementMap(ROTATERIGHT) = 0.2f * delta
      }))

  /**
   * Draw method. Is used to draw screen to display etc
   */
  def draw(): Unit = {
    var drawStartTime: Long = System.currentTimeMillis()
    var tmpDisplay: String = ""

    rend.clear()
    rend.renderScene(scene)

    /* HUD -------------------------------------*/
    tmpDisplay = rend.display

    if (showHUD) {
      rendHUD.clear()
      rendHUD.renderScene(sceneHUD)
      display = rendHUD.displayOverlay(tmpDisplay)
    } else {
      display = tmpDisplay
    }
    /* HUD -------------------------------------*/

    lastDrawTime = System.currentTimeMillis() - drawStartTime
  }

  private def updateHUD(playerLoc: Vec3) {
    var c = " ♥♥♥♥♥♥♥♥"
    if (hudTextRect.isDefined)
      hudTextRect.get.text = "HP:" + c + "\n" +
        "X: " + playerLoc.x + "\n" +
        "Y: " + playerLoc.z + "\n" +
        "drawTime: " + lastDrawTime + "\n"
  }

  def init(): Unit = {
    scene.loadMap("00_testmap")

    /* HUD ----------------------------------------------------------*/
    hudTextRect = Some(new TextRect2D(new Rectangle2D(32, 5, true),
      "Caffeine: 10\nX: 0\n" + "Y: 0\nDeltaTime: 0"))

    hudTextRect.get.offX = 2
    hudTextRect.get.offMinusX = 1
    hudTextRect.get.offMinusY = 1

    var rectEnt = Factory2D.createTextRectangle(hudTextRect.get)
    var testRectSpatial = rectEnt.getComponent(SpatialComponent.id)
    testRectSpatial.get.position = Vec3(0.0f, rendHUD.h - 6, 0.0f)

    sceneHUD.addEntity(rectEnt)
    /* --------------------------------------------------------------*/

    var dialogOptions: Array[Tuple2[String, Event]] = Array[Tuple2[String, Event]](
      ("Continue", new Event(Vector(this, "cont"), E_DIALOG)),
      ("Ok", new Event(Vector(this, "ok"), E_DIALOG)))

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