package o1.screen

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

class HudScreen(parent: Adventure, rend: Renderer)
    extends Screen(parent, rend) {
  eventTypes = Vector[EventType](E_INPUT, E_DIALOG, E_CHANGE_HUD_INFO)
  
  var paused = true
  var hudInfo: Option[Vector[Any]] = None
  
  val inputMap =
    Map[Tuple2[scala.swing.event.Key.Value, Int], (Float) => Unit](
      ((Key.Q, Input.KEYRELEASED), (delta) => {
      }),
      ((Key.Escape, Input.KEYRELEASED), (delta) => {
        //        EventManager.addEvent(new Event(Vector("gameScreen"), E_CHANGE_SCREEN))
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
    var border = Factory2D.createRectangle(rend.w - 3, rend.h - 3, false)
    var bSpatial = border.getComponent(SpatialComponent.id)
    bSpatial.get.position = Vec3(1f, 1f, 0f)
    scene.addEntity(border)
  }
  init()

  /**
   * Update method. Used to update game's state
   */

  def update(delta: Double): Unit = {
    // update invetory icons
    if (!paused) {

    }
    handleEvents(delta.toFloat)
  }

  def clearScene() = {
    scene.clear()
  }
  /**
   * Draw method. Is used to draw screen to display etc
   */
  def draw(): String = {
    rend.clear()
    rend.renderScene(scene)
    rend.display
  }

  def resume(): Unit = {
    paused = false
    EventManager.setActiveInputListener(this)

  }

  def pause() {
    paused = true
  }

  def handleEvent(event: Event, delta: Float) = {
    if (event.eventType == E_INPUT) {
      val eventKey =
        event.args(0).asInstanceOf[Tuple2[scala.swing.event.Key.Value, Int]]
      if (inputMap.contains(eventKey)) {
        inputMap(eventKey)(delta)
      }
    }
    if (event.eventType == E_CHANGE_HUD_INFO) {
      hudInfo = Some(event.args)
    }
  }

  def dispose() = {

  }
}