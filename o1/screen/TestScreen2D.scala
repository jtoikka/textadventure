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
import scala.swing.Font
import o1.mapGenerator.MapGenerator
import o1.mapGenerator.CornerMap
import o1.event.Listener
import o1.event.Event
import o1.event.EventType
import o1.event.EventManager
import o1.event.Input
import scala.collection.mutable.Buffer

class TestScreen2D(parent: Adventure, rend: Renderer)
  extends Screen(parent, rend) with Listener {
  eventTypes = Vector[Int](EventType.E_INPUT, EventType.E_DIALOG)

  def this(parent: Adventure, x: Int, y: Int) = this(parent, new Renderer2D(x, y))

  var childListeners = Buffer[Listener]()

  var scene = new Scene()
  var dialog = new Dialog(this, new Rectangle2D(32, 10, true), "3D Text Adventure\nMain Menu",
    Vector[String]("FirstOption", "SecondOption", "ThirdOption"))
  /**
   * Initializing test screen entities.
   */
  def init(): Unit = {
    dialog.offX = 2
    dialog.offMinusX = 1
    dialog.offMinusY = 1

    childListeners += dialog

    var rectEnt = Factory2D.createTextRectangle(dialog)
    var testRectSpatial = rectEnt.getComponent(SpatialComponent.id)
    testRectSpatial.get.position = Vec3(rend.w / 2 - dialog.w / 2, 25, 0.0f)
    scene.addEntity(rectEnt)

    var border = Factory2D.createRectangle(rend.w - 3, rend.h - 3, false)
    var bSpatial = border.getComponent(SpatialComponent.id)
    bSpatial.get.position = Vec3(1f, 1f, 0f)
    scene.addEntity(border)

    var name = "cross"
    var img = Factory2D.createImage(name)
    var spat = img.getComponent(SpatialComponent.id)

    var width = ResourceManager.images(name).getWidth()
    var heigth = ResourceManager.images(name).getHeight()

    spat.get.position = Vec3(rend.w / 2 - width / 2 + 1, 4.0f, 0.0f)
    scene.addEntity(img)

  }
  init()

  /**
   * Update method. Used to update game's state
   */

  def update(delta: Double): Unit = {
    handleEvents(delta.toFloat)
    for (i <- childListeners) {
      i.handleEvents(delta.toFloat)
    }

  }

  /**
   * Draw method. Is used to draw screen to display etc
   */
  def draw(): Unit = {
    rend.clear()
    rend.renderScene(scene)
    display = rend.display
  }

  def resume(): Unit = {
    println("TestScreen2D resumed")
    EventManager.setActiveInputListener(dialog)
  }

  def pause() {

  }

  def handleEvent(event: Event, delta: Float) = {
    if (event.eventType == EventType.E_INPUT) {
      val eventKey = event.args(0).asInstanceOf[Tuple2[scala.swing.event.Key.Value, Int]]
      if (inputMap.contains(eventKey)) {
        inputMap(eventKey)(delta)
      }
    }
    if (event.eventType == EventType.E_DIALOG) {
      parent.changeScreen(parent.screens("gameScreen"))
    }
  }

  val inputMap =
    Map[Tuple2[scala.swing.event.Key.Value, Int], (Float) => Unit](
      ((Key.W, Input.KEYRELEASED), (delta) => {

      }),
      ((Key.S, Input.KEYRELEASED), (delta) => {

      }),
      ((Key.A, Input.KEYRELEASED), (delta) => {

      }),
      ((Key.D, Input.KEYRELEASED), (delta) => {

      }))
}