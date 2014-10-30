package o1.adventure

import scala.collection.mutable.Map
import scala.math
import scala.swing.event.Key
import o1.math._
import o1.adventure.render.Renderer3D
import o1.screen._
import o1.adventure.ui._
import o1.mapGenerator.MapGenerator
import o1.mapGenerator.CornerMap
import o1.adventure.render.ResourceManager
import o1.event._
import o1.event.EventType._
/**
 * The class `Adventure` represents text adventure games. An adventure consists of a player and
 * a number of areas that make up the game world. It provides methods for playing the game one
 * turn at a time and for checking the state of the game.
 *
 * N.B. This version of the class has a lot of "hard-coded" information which pertain to a very
 * specific adventure game that involves a small trip through a twisted forest. All newly created
 * instances of class `Adventure` are identical to each other. To create other kinds of adventure
 * games, you will need to modify or replace the source code of this class.
 */
class Adventure() extends Listener {
  eventTypes = Vector(E_INPUT,E_CHANGE_SCREEN)
  /** The title of the adventure game. */
  var title = "A Forest Adventure"

  val screenWidth = 140
  val screenHeight = 40

  private val renderer = new Renderer3D(screenWidth, screenHeight) // We draw the world here!
  var display = renderer.display // A String displaying the world

  // Screen stuff
  val screens = Map[String, Screen](
    "menuScreen" -> new MainMenuScreen(this, screenWidth, screenHeight),
    "gameScreen" -> new GameScreen(this, screenWidth, screenHeight),
    "inventoryScreen" -> new InventoryScreen(this, screenWidth, screenHeight))
  //  val menuScreen: Screen = new MainMenuScreen(this, screenWidth, screenHeight)
  //  val gameScreen: Screen = new GameScreen(this, screenWidth, screenHeight)
  //  val testScreen2D: Screen = new TestScreen2D(this,screenWidth, screenHeight) 
  var currentScreen: Option[Screen] = None
  changeScreen(screens("menuScreen"))

  var totalTime = 0.0 // Keep track of how much time has passed

  var previousInput = Map[scala.swing.event.Key.Value, Boolean]()

  /**
   * Updates all screens, and renders the currently active screen. Handles
   * events and user input.
   * 
   * @param delta amount of time since last update
   * @param keyMap A map of pressed/released keys
   */
  def update(delta: Double, keyMap: Map[scala.swing.event.Key.Value, Boolean]) = {
    for (screen <- screens.values) {
      screen.update(delta)
    }
    totalTime += delta
    val period = math.Pi * 2.0f / 8.0f
    currentScreen.get.draw()
    handleInput(keyMap, delta)
    handleEvents(delta.toFloat)
    EventManager.delegateEvents()
  }

  /**
   * Checks which keys are held down, which keys are up, and which keys have
   * been pressed or released since the last frame update. Forwards them as
   * constant values to the Input object.
   * 
   * @param keyMap The current state of keys (up or down)
   * @param delta Time in seconds since the previous frame update
   */
  def handleInput(
    keyMap: Map[scala.swing.event.Key.Value, Boolean],
    delta: Double) = {

    val keyMapDelta = Map[scala.swing.event.Key.Value, Int]()
    if (previousInput.size > 0) {
      for (keyValue <- keyMap) {
        val key = keyValue._1
        val value = keyValue._2
        val previousValue = previousInput(key)
        var state = 0
        if (previousValue == value) {
          if (value) state = Input.KEYDOWN // else 0
        } else {
          if (value)
            state = Input.KEYRELEASED
          else
            state = Input.KEYPRESSED
        }
        keyMapDelta(key) = state
      }
      Input.handleInput(keyMapDelta, delta)
    }
    previousInput = keyMap.clone()
  }
  

  /** Maps key actions to functions */ 
  val inputMap =
    Map[Tuple2[scala.swing.event.Key.Value, Int], (Float) => Unit](
      ((Key.M, Input.KEYRELEASED), (delta) => {
        if (currentScreen.get == screens("testScreen2D")) {
          changeScreen(screens("gameScreen"))
        } else {
          changeScreen(screens("testScreen2D"))
        }
      }),
      ((Key.N, Input.KEYRELEASED), (delta) => {
        changeScreen(screens("testScreen2D"))
      }))

  /** Handles [event]. Takes time since last update as parameter [delta]. */
  def handleEvent(event: Event, delta: Float) {
    if (event.eventType == EventType.E_INPUT) {
      val eventKey =
        event.args(0).asInstanceOf[Tuple2[scala.swing.event.Key.Value, Int]]
      if (inputMap.contains(eventKey)) {
        inputMap(eventKey)(delta)
      }
    }
    else if (event.eventType == EventType.E_CHANGE_SCREEN) {
      val a = event.args(0).asInstanceOf[String]
      changeScreen(a)
    }
  }

  /**
   * Used to change current screen
   */
  def changeScreen(screen: String): Unit = changeScreen(screens(screen))
  def changeScreen(screen: Screen): Unit = {
    if (currentScreen != None)
      currentScreen.get.pause()
    this.currentScreen = Some(screen)
    
    EventManager.setActiveInputListener(currentScreen.get)
    currentScreen.get.resume()
  }
  
  def dispose() = {

  }

}

