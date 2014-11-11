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
import java.awt.image.BufferedImage
import o1.event.EmptyTile

class MapScreen(parent: Adventure, rend: Renderer)
    extends Screen(parent, rend) {
  eventTypes = Vector[EventType](E_INPUT, E_DIALOG, E_CHANGE_SCENE, E_CHANGE_MAP)

  val iconBoxSize = Vec2(19, 11)
  val scene = new Scene()

  var world:Option[World] = None

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

  var scenes = Map[String, SceneUI]()
  var activeScene: Option[SceneUI] = None

  def init(): Unit = {
    clearScene()
  }
  init()

  /**
   * Update method. Used to update game's state
   */

  def update(delta: Double): Unit = {
    handleEvents(delta.toFloat)
    if (activeScene.isDefined)
      activeScene.get.handleEvents(delta.toFloat)
  }

  /**
   * Draw method. Is used to draw screen to display etc
   */
  def draw(): String = {
    rend.clear()
    rend.renderScene(scene)
//    display = rend.display
    display
  }

  def resume(): Unit = {
    println("mapscreen resume!")
    EventManager.setActiveInputListener(this)
    updateMap()
  }

  def pause() {

  }

  def updateMap() = {
    if (world.isDefined) {
      val tileMap = world.get.tileMap
      println("UpdateMap")
      val bImg = new BufferedImage(tileMap.width, tileMap.height, BufferedImage.TYPE_BYTE_GRAY)
      for (x <- 0 until tileMap.width; y <- 0 until tileMap.height) {
        bImg.setRGB(x, y, tileMap.getCollisionTile(x, y).color)
      }
      var mapImage = new Image2D(bImg, false, true)

      var mapEnt = Factory2D.createImage(mapImage)
      var mapImageSpat = mapEnt.getComponent(SpatialComponent.id)
      mapImageSpat.get.position = Vec3(rend.w/2 - bImg.getWidth(), rend.h/2 - bImg.getHeight()/2, 0.0f)
      scene.addEntity(mapEnt)
    }
  }

  def clearScene() = {
    scene.clear()
    var textRect = new TextRect2D(new Rectangle2D(iconBoxSize.x.toInt, 2, true),
      ResourceManager.strings("mapTitle"))
    textRect.offX = 1
    textRect.offY = 1
    textRect.offMinusX = 1
    textRect.offMinusY = 0
    textRect.textWrap = true
    textRect.centerText = true

    var text = Factory2D.createTextRectangle(textRect)
    var textSpat = text.getComponent(SpatialComponent.id)
    textSpat.get.position = Vec3(rend.w / 2 - iconBoxSize.x / 2 - 1, 0, 0.0f)

    var border = Factory2D.createRectangle(rend.w - 3, rend.h - 3, false)
    var bSpatial = border.getComponent(SpatialComponent.id)
    bSpatial.get.position = Vec3(1f, 1f, 0f)
    scene.addEntity(border)

    scene.addEntity(text)

  }
  def handleEvent(event: Event, delta: Float) = {
    if (event.eventType == E_INPUT) {
      val eventKey =
        event.args(0).asInstanceOf[Tuple2[scala.swing.event.Key.Value, Int]]
      if (inputMap.contains(eventKey)) {
        inputMap(eventKey)(delta)
      }
    } else if (event.eventType == E_CHANGE_MAP) {
      world = event.args(0).asInstanceOf[Option[World]]
      
    }
  }

  def dispose() = {

  }
}