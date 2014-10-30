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

class InventoryScreen(parent: Adventure, rend: Renderer)
    extends Screen(parent, rend) {
  eventTypes = Vector[EventType](E_INPUT, E_DIALOG, E_CHANGE_SCENE)

  val iconCoords = Array[Vec2]( Vec2(10, 5), Vec2(40, 5), Vec2(70, 5),
                                Vec2(10, 25), Vec2(40, 25), Vec2(70, 25))
                                
  val iconPlaceFix = Vec2(2,2)
  var paused = true

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

  val scene = new Scene()

  val textRect = new TextRect2D(new Rectangle2D(100, 30, true),
    Inventory.toString())

  var selected: Int = 0

  def init(): Unit = {
    textRect.offX = 2
    textRect.offMinusX = 1
    textRect.offMinusY = 1
    textRect.textWrap = false
    textRect.centerText = false

    var rectEnt = Factory2D.createTextRectangle(textRect)
    var testRectSpatial = rectEnt.getComponent(SpatialComponent.id)
    testRectSpatial.get.position = Vec3(rend.w / 2 - textRect.w / 2, rend.h / 2 - textRect.h / 2, 0.0f)
    scene.addEntity(rectEnt)

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
//      println("Inventory Screen update")
      val invArray = Inventory.containers.toArray
      clearScene()

      // Add evety item to invArray
      for (i <- invArray.indices) {
//        println("Found inventory container: " + invArray(i).toString())
        if (!invArray(i)._2.hiddenContainer) {
          // Not hidden. Add to the list
          val icon = invArray(i)._2.icon.get
          val count = invArray(i)._2.size
          val name = invArray(i)._2.name
          
          var img = Factory2D.createImage(icon)
          var imgSpat = img.getComponent(SpatialComponent.id)
          imgSpat.get.position = Vec3(iconCoords(i).x+iconPlaceFix.x, 
                                 iconCoords(i).y +iconPlaceFix.y, 0.0f)
          scene.addEntity(img)
        }
      }


      textRect.text = Inventory.toString()
    }
    handleEvents(delta.toFloat)
  }
  def clearScene() = {
    scene.clear()
    
    var border = Factory2D.createRectangle(rend.w - 3, rend.h - 3, false)
    var bSpatial = border.getComponent(SpatialComponent.id)
    bSpatial.get.position = Vec3(1f, 1f, 0f)
    scene.addEntity(border)
    
    for(i <- iconCoords){
      var border = Factory2D.createRectangle(19, 11, false)
      var bSpatial = border.getComponent(SpatialComponent.id)
      bSpatial.get.position = Vec3(i.x, i.y, 0f)
      scene.addEntity(border)
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
  }

  def dispose() = {

  }
}