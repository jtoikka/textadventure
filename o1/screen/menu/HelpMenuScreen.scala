package o1.screen.menu

import scala.swing.event.Key
import scala.collection.mutable.Map
import o1.adventure._
import o1.adventure.render.Renderer
import o1.adventure.render.ResourceManager
import scala.math._
import o1.math._
import o1.scene._
import o1.adventure.render2D._
import o1.event.Event
import o1.event.EventType._
import o1.event.EventManager
import o1.event.Input
import o1.screen.Screen
import scala.Vector

class HelpMenuScreen(parent: Adventure, rend: Renderer)
    extends Screen(parent, rend) {
  def this(parent: Adventure, x: Int, y: Int) = this(parent, new Renderer2D(x, y))

  var paused = false
  val scene = new Scene()

  def init(): Unit = {
    var border = Factory2D.createRectangle(rend.w - 3, rend.h - 3, false)
    var bSpatial = border.getComponent(SpatialComponent.id)
    bSpatial.get.position = Vec3(1f, 1f, 0f)
    scene.addEntity(border)

    var helpTextRect = new TextRect2D(new Rectangle2D(30, 18, true), ResourceManager.strings("helpMenu")) {
      offX = 3
      offY = 0
      offMinusX = 2
      offMinusY = 2
      textWrap = true
      centerText = false
    }
    
    var objectiveTextRect = new TextRect2D(new Rectangle2D(60, 18, true), ResourceManager.strings("helpObjective")) {
      offX = 3
      offY = 0
      offMinusX = 2
      offMinusY = 2
      textWrap = true
      centerText = false
    }
    val helpEnt = Factory2D.createTextRectangle(helpTextRect)
    val objectiveEnt = Factory2D.createTextRectangle(objectiveTextRect)

    val helpSpatial = helpEnt.getComponent(SpatialComponent.id)
    helpSpatial.get.position = Vec3(3, 3, 0.0f)
    
    val objectiveSpatial = objectiveEnt.getComponent(SpatialComponent.id)
    objectiveSpatial.get.position = Vec3(40, 3, 0.0f)
    
    scene.addEntity(helpEnt)
    scene.addEntity(objectiveEnt)
  }
  init()

  /**
   * Update method. Used to update game's state
   */

  def update(delta: Double): Unit = {
    if (!paused) {

    }
    handleEvents(delta.toFloat)
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
    EventManager.setActiveInputListener(this)
  }

  def pause() {

  }
  
  eventHandlers = scala.collection.immutable.Map(
    (E_INPUT, (event, delta) => {
      val eventKey = event.args(0).asInstanceOf[Tuple2[scala.swing.event.Key.Value, Int]]
      if (inputMap.contains(eventKey)) {
        inputMap(eventKey)(delta)
      }
    }))

  val inputMap =
    Map[Tuple2[scala.swing.event.Key.Value, Int], (Float) => Unit](
      ((Key.W, Input.KEYRELEASED), (delta) => {

      }),
      ((Key.S, Input.KEYRELEASED), (delta) => {

      }),
      ((Key.A, Input.KEYRELEASED), (delta) => {

      }),
      ((Key.D, Input.KEYRELEASED), (delta) => {

      }),
      ((Key.Escape, Input.KEYRELEASED), (delta) => {
        parent.changeToPreviousScreen()
      }))

  def dispose() = {

  }
}