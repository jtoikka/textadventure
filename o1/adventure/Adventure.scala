package o1.adventure

import scala.collection.mutable.Map
import scala.math
import o1.math._
import o1.adventure.render.Renderer3D
import o1.screen._
import o1.adventure.ui._
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
class Adventure() {

  /** The title of the adventure game. */
  
  var title = "A Forest Adventure"
 
  val screenWidth = 200
  val screenHeight = 70
  
  val _renderer = new Renderer3D(screenWidth, screenHeight) // We draw the world here!
  var display = _renderer.display // A String displaying the world
  
  // Screen stuff
  
  val menuScreen: Screen = new MainMenuScreen(this, screenWidth, screenHeight)
  val gameScreen: Screen = new GameScreen(this, screenWidth, screenHeight)
  val testScreen2D: Screen = new TestScreen2D(this,screenWidth, screenHeight) 
  var currentScreen: Screen = testScreen2D
  
  var totalTime = 0.0 // Keep track of how much time has passed
  
  var previousInput = Map[scala.swing.event.Key.Value, Boolean]()
  
  /**
 	* Updates current screen and renders it.
 	*/
  def update(delta: Double, keyMap: Map[scala.swing.event.Key.Value, Boolean]) = {
    currentScreen.update(delta)
    totalTime += delta
    val period = math.Pi * 2.0f / 8.0f
    currentScreen.draw()
    handleInput(keyMap, delta)
  }
  
  def handleInput(
    keyMap: Map[scala.swing.event.Key.Value, Boolean], delta: Double) = {
    
    val keyMapDelta = Map[scala.swing.event.Key.Value, Int]()
    if (previousInput.size > 0) {
      for (keyValue <- keyMap) {
        val key = keyValue._1
        val value = keyValue._2
        val previousValue = previousInput(key)
        var state = 0
        if (previousValue == value) {
          if (value) state = 1 // else 0
        } else {
          if (value) 
            state = 2
          else 
            state = 3
        }
        keyMapDelta(key) = state
      }
      currentScreen.input(keyMapDelta, delta)
    }
    previousInput = keyMap.clone()
  }
  
  /**
 	* Used to change current screen
 	*/
  def changeScreen(scr: Screen): Unit = {
    this.currentScreen = scr
    currentScreen.resume()
    println("Screen Changed")
  }
  
  
}

