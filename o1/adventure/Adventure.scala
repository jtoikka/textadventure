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
  eventTypes = Vector(EventType.E_INPUT)
  /** The title of the adventure game. */
  var title = "A Forest Adventure"
 
  val screenWidth = 130
  val screenHeight = 40
  
  private val renderer = new Renderer3D(screenWidth, screenHeight) // We draw the world here!
  var display = renderer.display // A String displaying the world
  
  
//  var map = new MapGenerator(64, 64, 4, 123123).map
//  for (y <- 62 to 0 by -1) {
//    for (x <- 0 until 65) {
//      print(map(y * (64 + 1) + x))
//    }
//    print('\n')
//  }
//  var edgeMap = CornerMap.generateMap(map, 65)
//  ResourceManager.meshes("map") = CornerMap.createWallMesh(edgeMap, 2.0f)
  
  // Screen stuff
  val screens = Map[String, Screen](
      "menuScreen" -> new MainMenuScreen(this, screenWidth, screenHeight),
      "gameScreen" -> new GameScreen(this, screenWidth, screenHeight),
      "testScreen2D" -> new TestScreen2D(this, screenWidth, screenHeight))
//  val menuScreen: Screen = new MainMenuScreen(this, screenWidth, screenHeight)
//  val gameScreen: Screen = new GameScreen(this, screenWidth, screenHeight)
//  val testScreen2D: Screen = new TestScreen2D(this,screenWidth, screenHeight) 
  var currentScreen: Option[Screen] = None
  changeScreen(screens("testScreen2D"))
  
  var totalTime = 0.0 // Keep track of how much time has passed
  
  var previousInput = Map[scala.swing.event.Key.Value, Boolean]()
  
  /**
 	* Updates current screen and renders it.
 	*/
  def update(delta: Double, keyMap: Map[scala.swing.event.Key.Value, Boolean]) = {
    for (screen <- screens.values) {
      screen.update(delta)
    }
//    currentScreen.get.update(delta)
    totalTime += delta
    val period = math.Pi * 2.0f / 8.0f
    currentScreen.get.draw()
    handleInput(keyMap, delta)
    handleEvents(delta.toFloat)
    EventManager.delegateEvents()
  }
  
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
//      currentScreen.get.input(keyMapDelta, delta)
    }
    previousInput = keyMap.clone()
  }
  
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
  
  def handleEvent(event: Event, delta: Float) {
    if (event.eventType == EventType.E_INPUT) {
      val eventKey = 
        event.args(0).asInstanceOf[Tuple2[scala.swing.event.Key.Value, Int]]
      if (inputMap.contains(eventKey)) {
        inputMap(eventKey)(delta)
      }
    }
  }
  
  /**
 	* Used to change current screen
 	*/
  def changeScreen(screen: Screen): Unit = {
    if(currentScreen != None)
      currentScreen.get.pause()
      
    this.currentScreen = Some(screen)
    currentScreen.get.resume()
    println("Screen Changed")
  }
  
  
}

