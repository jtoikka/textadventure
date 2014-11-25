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

  var paused = false
  var player: Option[Entity] = None
  var infoItem: Option[Entity] = None

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

    var mainInfoBox = Factory2D.createTextRectangle(
      new TextRect2D(
        new Rectangle2D(23, 3, true)))
    var infoSpatial = mainInfoBox.getComponent(SpatialComponent.id)
    infoSpatial.get.position = Vec3(0f, 0f, 0f)

    mainInfoBox.eventHandlers = scala.collection.immutable.Map(
      (E_CHANGE_HUD_INFO, (event, delta) => {
        val playerHP = event.args(0).asInstanceOf[Int]
        val playerMana = event.args(1).asInstanceOf[Int]
        val playerLoc = event.args(2).asInstanceOf[Vec3]
        val playerHeading = event.args(3).asInstanceOf[Vec3]
        val time = event.args(4).asInstanceOf[String]
        val hp = event.args(5).asInstanceOf[Int]
        val maxHP = event.args(6).asInstanceOf[Int]
        val box = ResourceManager.shapes(mainInfoBox.getComponent(RenderComponent2D.id).get.shape)
        box.asInstanceOf[TextRect2D].text =
          "Health: " + "\u2665" * hp + "\u2661" * (maxHP - hp) +
          "\nTime: " + time + " AM"
          "\nHeading: " + playerHeading
      }))

    scene.addEntity(mainInfoBox)
  }
  init()

  /**
   * Update method. Used to update game's state
   */
  
  def gameTime(realTime: Double) = {
    val gTime = (6 - exp(3/pow(realTime + 3, 0.46915))) * 72
    "%02d".format((gTime).toInt / 60) + ":" + "%02d".format((gTime).toInt % 60)
  }
  
  var realTime = 0.0

  def update(delta: Double): Unit = {
    // update invetory icons
    if (!paused && player.isDefined) {
      realTime += delta / 1000.0
      EventManager.addEvent(new Event(Vector(69, 420,
        player.get.getComponent(SpatialComponent.id).get.position,
        player.get.getComponent(SpatialComponent.id).get.forward,
        gameTime(realTime),
        player.get.getComponent(HealthComponent.id).get.hp,
        player.get.getComponent(HealthComponent.id).get.maxHP),
        E_CHANGE_HUD_INFO))
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

  eventHandlers = scala.collection.immutable.Map(
    (E_INPUT, (event, delta) => {
      val eventKey =
        event.args(0).asInstanceOf[Tuple2[scala.swing.event.Key.Value, Int]]
      if (inputMap.contains(eventKey)) {
        inputMap(eventKey)(delta)
      }
    }),
    (E_PLAYER_CREATION, (event, delta) => {
      player = Some(event.args(0).asInstanceOf[Entity])
    }))

  def dispose() = {

  }
  
  def reset() = {
    
  }
}