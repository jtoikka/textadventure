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

class MainMenuScreen(parent: Adventure, rend: Renderer)
    extends Screen(parent, rend) {
  def this(parent: Adventure, x: Int, y: Int) = this(parent, new Renderer2D(x, y))

  var scenes = Map[String, SceneUI]()
  var activeScene: Option[SceneUI] = None

  var dialogOptions: Vector[Tuple2[String, Event]] = Vector[Tuple2[String, Event]](
    ("Play Game", new Event(Vector("gameScreen"), E_CHANGE_SCREEN)),
    ("Help", new Event(Vector("helpMenuScreen"), E_CHANGE_SCREEN)),
    ("Credits", new Event(Vector("creditsScreen", this), E_CHANGE_SCREEN)),
    ("Exit Game", new Event(null, E_SYSTEM_EXIT)))

  var dialog = new Dialog(Some(this),
    new Rectangle2D(26, 10, true),
    "-" * 10 + "\nMain Menu\n" + "-" * 10,
    dialogOptions)

  def init(): Unit = {
    // MainMenu
    val mainMenuScene = new SceneUI(null)
    dialog.offX = 1
    dialog.offMinusX = 1
    dialog.offMinusY = 1
    dialog.textWrap = false
    dialog.centerText = true

    val rectEnt = Factory2D.createTextRectangle(dialog)
    val testRectSpatial = rectEnt.getComponent(SpatialComponent.id)
    testRectSpatial.get.position = Vec3(rend.w / 2 - dialog.w / 2, 25, 0.0f)
    mainMenuScene.addEntity(rectEnt)

    val textR =  new TextRect2D(
      new Rectangle2D(23, 3, false), ResourceManager.version){
      offX = 1
      offY = 1
      color1 = Renderer.empty
      color2 = Renderer.empty
      offMinusX = 2
      offMinusY = 2
      textWrap = true
      centerText = true
    }
    textR.color1 = Renderer.empty
    textR.color2 = Renderer.empty
    val versionRect = Factory2D.createTextRectangle(textR)

    val versionRectSpat = versionRect.getComponent(SpatialComponent.id)
    versionRectSpat.get.position = Vec3(110f, 35f, 0f)
    mainMenuScene.addEntity(versionRect)

    val border = Factory2D.createRectangle(rend.w - 3, rend.h - 3, false)
    val bSpatial = border.getComponent(SpatialComponent.id)
    bSpatial.get.position = Vec3(1f, 1f, 0f)
    mainMenuScene.addEntity(border)

    val name = "logo_main"
    val img = Factory2D.createImage(ResourceManager.images(name))
    val spat = img.getComponent(SpatialComponent.id)

    val imgName = img.getComponent(RenderComponent2D.id).get.shape
    val width = ResourceManager.shapes(imgName).getWidth
    val height = ResourceManager.shapes(imgName).getHeight

    spat.get.position = Vec3(rend.w / 2 - width / 2, 4.0f, 0.0f)
    mainMenuScene.addEntity(img)

    mainMenuScene.childListeners += dialog
    mainMenuScene.defaultListener = dialog

    scenes("mainMenu") = mainMenuScene

    // HelpMenu
    val helpInputMap =
      Map[Tuple2[scala.swing.event.Key.Value, Int], (Float) => Unit](
        ((Key.Enter, Input.KEYRELEASED), (delta) => {
          EventManager.addEvent(new Event(Vector("mainMenu"), E_CHANGE_SCENE))
        }),
        ((Key.Escape, Input.KEYRELEASED), (delta) => {
          EventManager.addEvent(new Event(Vector("mainMenu"), E_CHANGE_SCENE))
        }))

    val helpMenuScene = new SceneUI(helpInputMap)

    helpMenuScene.addEntity(border)
    helpMenuScene.addEntity(img)

    val helpTextRect = new TextRect2D(new Rectangle2D(50, 10, true), ResourceManager.strings("helpMenu"))
    helpTextRect.offX = 3
    helpTextRect.offY = 2
    helpTextRect.offMinusX = 2
    helpTextRect.offMinusY = 2
    helpTextRect.textWrap = true
    helpTextRect.centerText = true
    val helpEnt = Factory2D.createTextRectangle(helpTextRect)

    val helpSpatial = helpEnt.getComponent(SpatialComponent.id)
    helpSpatial.get.position = Vec3(rend.w / 2 - helpTextRect.w / 2, 25, 0.0f)
    helpMenuScene.addEntity(helpEnt)

    scenes("helpMenu") = helpMenuScene

    changeScene(scenes("mainMenu"))
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
    if (activeScene.isDefined) {
      rend.renderScene(activeScene.get)
    }
//    parent.screens("gameScreen").draw()
//    var tmpDisplay: String = parent.screens("gameScreen").rend.display
//    rend.displayOverlay(tmpDisplay)
    rend.display
  }

  def resume(): Unit = {
    if (activeScene.isDefined)
      EventManager.setActiveInputListener(activeScene.get.defaultListener)
    else
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

  def changeScene(scene: SceneUI) = {
    activeScene = Some(scene)
    EventManager.setActiveInputListener(activeScene.get.defaultListener)
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

  def dispose() = {

  }
  
  def reset() = {
    
  }
}