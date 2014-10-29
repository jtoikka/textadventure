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

class InventoryScreen(parent: Adventure, rend: Renderer)
    extends Screen(parent, rend){
  eventTypes = Vector[EventType](E_INPUT, E_DIALOG, E_CHANGE_SCENE)

  val inputMap =
    Map[Tuple2[scala.swing.event.Key.Value, Int], (Float) => Unit](
      ((Key.Q, Input.KEYRELEASED), (delta) => {
      }),
      ((Key.Escape, Input.KEYRELEASED), (delta) => {
        EventManager.addEvent(new Event(Vector("gameScreen"), E_CHANGE_SCREEN))
      }),
      ((Key.I, Input.KEYRELEASED), (delta) => {
        EventManager.addEvent(new Event(Vector("gameScreen"), E_CHANGE_SCREEN))
      }),
      ((Key.N, Input.KEYRELEASED), (delta) => {
        if (Inventory.addItem(Page())) println("Added Page to inventory")

      }),
      ((Key.M, Input.KEYRELEASED), (delta) => {
        if (Inventory.addItem(Coffee())) println("Added Coffee to inventory")
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

  var scenes = Map[String, SceneUI]()
  var activeScene: Option[SceneUI] = None

  val textRect = new TextRect2D(new Rectangle2D(100, 30, true),
    Inventory.toString())

  def init(): Unit = {
    var invScene = new SceneUI(inputMap)

    textRect.offX = 2
    textRect.offMinusX = 1
    textRect.offMinusY = 1
    textRect.textWrap = false
    textRect.centerText = false

    var rectEnt = Factory2D.createTextRectangle(textRect)
    var testRectSpatial = rectEnt.getComponent(SpatialComponent.id)
    testRectSpatial.get.position = Vec3(rend.w / 2 - textRect.w / 2, rend.h / 2 - textRect.h / 2, 0.0f)

    invScene.addEntity(rectEnt)
    changeScene(invScene)
  }
  init()

  /**
   * Update method. Used to update game's state
   */

  def update(delta: Double): Unit = {
    // update text
    textRect.text = Inventory.toString()
    handleEvents(delta.toFloat)
    if (activeScene.isDefined)
      activeScene.get.handleEvents(delta.toFloat)
  }

  /**
   * Draw method. Is used to draw screen to display etc
   */
  def draw(): Unit = {
    rend.clear()
    if (activeScene.isDefined)
      rend.renderScene(activeScene.get)
    display = rend.display
  }

  def resume(): Unit = {

    if (activeScene.isDefined)
      EventManager.setActiveInputListener(activeScene.get.defaultListener)
    else
      EventManager.setActiveInputListener(this)
  }

  def pause() {

  }

  def handleEvent(event: Event, delta: Float) = {
    if (event.eventType == E_INPUT) {
      val eventKey =
        event.args(0).asInstanceOf[Tuple2[scala.swing.event.Key.Value, Int]]
      if (inputMap.contains(eventKey)) {
        inputMap(eventKey)(delta)
      }
    }
  }

  def changeScene(scene: SceneUI) = {
    activeScene = Some(scene)
    EventManager.setActiveInputListener(activeScene.get.defaultListener)
  }

  def dispose() = {

  }
}