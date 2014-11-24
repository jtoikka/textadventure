package o1.screen

import scala.swing.event.Key
//import scala.collection.mutable.Map
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
//import scala.collection.mutable.Buffer
import o1.inventory.Inventory
import o1.inventory.Page
import o1.inventory.Coffee
import java.awt.image.BufferedImage
import o1.event.EmptyTile

class GameOverScreen(parent: Adventure, rend: Renderer)
    extends Screen(parent, rend) {

  val scene = new Scene()
  var paused = true

  var timer = 0.0

  val inputMap =
    Map[Tuple2[scala.swing.event.Key.Value, Int], (Float) => Unit](
      ((Key.Q, Input.KEYRELEASED), (delta) => {
      }),
      ((Key.Escape, Input.KEYRELEASED), (delta) => {
        EventManager.addEvent(new Event(Vector("menuScreen"), E_CHANGE_SCREEN))
      }),
      ((Key.I, Input.KEYRELEASED), (delta) => {
      }),
      ((Key.N, Input.KEYRELEASED), (delta) => {
      }),
      ((Key.M, Input.KEYRELEASED), (delta) => {
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

  def init(): Unit = {
    var border = Factory2D.createRectangle(rend.w - 3, rend.h - 3, false)
    var bSpatial = border.getComponent(SpatialComponent.id)
    bSpatial.get.position = Vec3(1f, 1f, 0f)
//    scene.addEntity(border)
    
    var name = "logo_gameOver"
    var img = Factory2D.createImage(ResourceManager.images(name))
    var spat = img.getComponent(SpatialComponent.id)

    val imgName = img.getComponent(RenderComponent2D.id).get.shape
    val width = ResourceManager.shapes(imgName).getWidth
    val height = ResourceManager.shapes(imgName).getHeight

    spat.get.position = Vec3(rend.w / 2 - width / 2, 4.0f, 0.0f)
    scene.addEntity(img)
    
    var textRect = new TextRect2D(new Rectangle2D(55, 15, false), ResourceManager.strings("helpMenu"))
    textRect.offX = 3
    textRect.offY = 2
    textRect.offMinusX = 2
    textRect.offMinusY = 2
    textRect.textWrap = true
    textRect.centerText = true
    val helpEnt = Factory2D.createTextRectangle(textRect)

    var helpSpatial = helpEnt.getComponent(SpatialComponent.id)
    helpSpatial.get.position = Vec3(rend.w / 2 - textRect.w / 2, 20, 0.0f)
//    scene.addEntity(helpEnt)
  }
  init()

  /**
   * Update method. Used to update game's state
   */

  def update(delta: Double): Unit = {
    if (!paused) {
    }
    timer += delta
    handleEvents(delta.toFloat)
    scene.entities.foreach(_.handleEvents(delta.toFloat))
  }

  /**
   * Draw method. Is used to draw screen to display etc
   */
  def draw(): String = {

    rend.clear()
    rend.renderScene(scene)

    parent.screens("gameScreen").draw()
    var tmpDisplay: String = parent.screens("gameScreen").rend.display
    
    rend.displayOverlay(tmpDisplay)

  }

  def resume(): Unit = {
    EventManager.setActiveInputListener(this)
    paused = false
  }

  def pause() {
    paused = true
  }

  eventHandlers = scala.collection.immutable.Map(
    (E_INPUT, (event, delta) => {
      val eventKey =
        event.args(0).asInstanceOf[Tuple2[scala.swing.event.Key.Value, Int]]
      if (inputMap.contains(eventKey)) {
        inputMap(eventKey)(delta)
      }
    }),
    (E_CHANGE_MAP, (event, delta) => {
    }))

  def dispose() = {

  }
}