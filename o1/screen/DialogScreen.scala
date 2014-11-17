package o1.screen

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.swing.event.Key
import scala.collection.mutable.Map
import o1.adventure._
import o1.adventure.render.Renderer
import o1.adventure.render.ResourceManager
import scala.math._
import o1.math._
import o1.scene._
import o1.adventure.render2D._
import o1.event.Listener
import o1.event.Event
import o1.event.EventType._
import o1.event.EventManager
import o1.event.Input
import scala.collection.mutable.Buffer
import o1.inventory.Inventory
import o1.inventory.Page
import o1.inventory.Coffee
import o1.inventory.ItemContainer

class DialogScreen(parent: Adventure, rend: Renderer)
    extends Screen(parent, rend) {

  var currentDialog: Option[Dialog] = None

  eventHandlers = scala.collection.immutable.Map(
    (E_INPUT, (event, delta) => {
      val eventKey =
        event.args(0).asInstanceOf[Tuple2[scala.swing.event.Key.Value, Int]]
      if (inputMap.contains(eventKey)) {
        inputMap(eventKey)(delta)
      }
    }),
    (E_THROW_DIALOG, (event, delta) => {
//      println("Threw Dialog with hash " + event.args(1).toString)

      val dialog = event.args(0).asInstanceOf[Dialog]
      var rectEnt = Factory2D.createTextRectangle(dialog)
      var testRectSpatial = rectEnt.getComponent(SpatialComponent.id)
      testRectSpatial.get.position = Vec3(rend.w / 2 - dialog.w / 2, rend.h / 2 - dialog.h / 2, 0.0f)
      scene.addEntity(rectEnt)

      currentDialog = Some(dialog)

//      EventManager.activeInputListener = Some(dialog)
      EventManager.setActiveInputListener(dialog)

    }), (E_ANSWER_DIALOG, (event, delta) => {
      clearScene()
    }))

  var dialogOptions = Vector[Tuple2[String, Option[Event]]]("" -> None)

  var paused = false
  var player: Option[Entity] = None

  val inputMap =
    Map[Tuple2[scala.swing.event.Key.Value, Int], (Float) => Unit](
      ((Key.Q, Input.KEYRELEASED), (delta) => {
      }),
      ((Key.Escape, Input.KEYRELEASED), (delta) => {

      }),
      ((Key.I, Input.KEYRELEASED), (delta) => {
        //        EventManager.addEvent(new Event(Vector("gameScreen"), E_CHANGE_SCREEN))
      }),
      ((Key.N, Input.KEYRELEASED), (delta) => {
        //        if (Inventory.addItem(Page())) println("Added Page to inventory")
      }),
      ((Key.M, Input.KEYRELEASED), (delta) => {
        //        if (Inventory.addItem(Coffee())) println("Added Coffee to inventory")
      }),
      ((Key.W, Input.KEYDOWN), (delta) => {
      }),
      ((Key.S, Input.KEYDOWN), (delta) => {
      }),
      ((Key.A, Input.KEYDOWN), (delta) => {
      }),
      ((Key.D, Input.KEYDOWN), (delta) => {
      }),
      ((Key.Left, Input.KEYDOWN), (delta) => {
      }),
      ((Key.Right, Input.KEYDOWN), (delta) => {
      }))

  def this(parent: Adventure, x: Int, y: Int) = this(parent, new Renderer2D(x, y))

  val scene = new Scene()

  def init(): Unit = {

    var mainInfoBox = Factory2D.createTextRectangle(
      new TextRect2D(
        new Rectangle2D(50, 3, true)))
    var infoSpatial = mainInfoBox.getComponent(SpatialComponent.id)
    infoSpatial.get.position = Vec3(10f, 10f, 0f)
    //    scene.addEntity(mainInfoBox)
  }
  init()

  /**
   * Update method. Used to update game's state
   */

  def update(delta: Double): Unit = {
    // update invetory icons
    if (currentDialog.isDefined) {
      currentDialog.get.handleEvents(delta.toFloat)
      if (!currentDialog.get.active) {
        currentDialog = None
      }
    } else {
      // clearScene()
    }

    handleEvents(delta.toFloat)
    scene.entities.foreach(_.handleEvents(delta.toFloat))
  }

  def clearScene() = {
    scene.clear()
  }

  /**
   * Draw method. Is used to draw screen to display etc
   */
  def draw(): String = {
    rend.clear()
    if (currentDialog.isDefined && currentDialog.get.active) {
      rend.renderScene(scene)
    }
    rend.display
  }

  def resume(): Unit = {
    paused = false
//    EventManager.setActiveInputListener(this)

  }

  def pause() {
    paused = true
  }

  def drawOverlay(s: String): String = {
    rend.clear()
    if (currentDialog.isDefined && currentDialog.get.active) {
      rend.renderScene(scene)
    }
    rend.displayOverlay(s)
  }

  def dispose() = {

  }
}