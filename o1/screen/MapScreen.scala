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

class MapScreen(parent: Adventure, rend: Renderer)
    extends Screen(parent, rend) {

  val iconBoxSize = Vec2(19, 11)
  val scene = new Scene()
  val paused = true
  var world: Option[World] = None
  var playerLoc: Option[Vec3] = None
  var playerHeading: Option[Vec3] = None

  val inputMap =
    Map[Tuple2[scala.swing.event.Key.Value, Int], (Float) => Unit](
      ((Key.Q, Input.KEYRELEASED), (delta) => {
      }),
      ((Key.Escape, Input.KEYRELEASED), (delta) => {
        parent.changeToPreviousScreen()
      }),
      ((Key.I, Input.KEYRELEASED), (delta) => {

      }),
      ((Key.N, Input.KEYRELEASED), (delta) => {
      }),
      ((Key.M, Input.KEYRELEASED), (delta) => {
        parent.changeToPreviousScreen()
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
    clearScene()
  }
  init()

  /**
   * Update method. Used to update game's state
   */

  def update(delta: Double): Unit = {
    if (!paused) {

    }
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
    updateMap()
  }

  def pause() {

  }

  def updateMap() = {
    if (world.isDefined) {
//      scene.clear()
      val tileMap = world.get.tileMap
      val bImg = new BufferedImage(tileMap.width, tileMap.height, BufferedImage.TYPE_BYTE_GRAY)
      for (x <- 0 until tileMap.width; y <- 0 until tileMap.height) {
        bImg.setRGB(x, y, tileMap.getCollisionTile(x, y).color)
      }

      var mapImage = new Image2D(bImg, false, true)
      var mapEnt = Factory2D.createImage(mapImage)
      var mapImageSpat = mapEnt.getComponent(SpatialComponent.id)
      mapImageSpat.get.position = Vec3(rend.w / 2 - bImg.getWidth(), rend.h / 2 - bImg.getHeight() / 2, 0.0f)
      scene.addEntity(mapEnt)
      
      if(playerLoc.isDefined){
      var playerMark = Factory2D.createTextRectangle(
        new TextRect2D(
          new Rectangle2D(0, 0, false, 0, 0), "@") {
          this.offMinusX = 0
          this.offMinusY = 0
          this.offX = 0
          this.offY = 0
          this.centerText = false
          this.textWrap = false
          this.defFill = false
        })
        
      var infoSpatial = playerMark.getComponent(SpatialComponent.id)
      infoSpatial.get.position = Vec3(
          (mapImageSpat.get.position.x).round + ((playerLoc.get.x+0.50f)*2).round/2,
          (mapImageSpat.get.position.y).round + ((playerLoc.get.z+0.50f)*2).round/4,
          0.0f)
      scene.addEntity(playerMark)
      }
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

  eventHandlers = scala.collection.immutable.Map(
    (E_INPUT, (event, delta) => {
      val eventKey =
        event.args(0).asInstanceOf[Tuple2[scala.swing.event.Key.Value, Int]]
      if (inputMap.contains(eventKey)) {
        inputMap(eventKey)(delta)
      }
    }),
    (E_CHANGE_MAP, (event, delta) => {
      world = event.args(0).asInstanceOf[Option[World]]
    }),
    (E_CHANGE_HUD_INFO, (event, delta) => {
      playerLoc = Some(event.args(2).asInstanceOf[Vec3])
      playerHeading = Some(event.args(3).asInstanceOf[Vec3])

    }))

  def dispose() = {

  }
}