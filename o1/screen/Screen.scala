package o1.screen

import scala.collection.mutable.Map
import o1.adventure._
import o1.adventure.render._
import scala.math._ 
/**
 *  Abstract Screen class. To be used with all screens. 
 * @param parent parent Adventure class
 **/

abstract class Screen(private val parent: Adventure, private val rend: Renderer) {
  var display = rend.display;
	/**
	 * Update method. Used to update screen state
	 */
	def update(delta: Double)
	
	/**
	 * Handles input. Each key corresponds to a value of:
	 * 0: Up
	 * 1: Down
	 * 2: Pressed
	 * 3: Released
	 * 
	 * Note that pressed and released are single-frame states, they describe if
	 * a key was pushed down or released between frames. Delta is the time since
	 * the last frame update.
	 */
	def input(keyMap: Map[scala.swing.event.Key.Value, Int], delta: Double)
	
	/**
	 * Draw method. Is used to draw screen to display etc
	 */
	def draw()
	
	/**
	 * Initializing method. Should be used when screen is created
	 */
	def init()
	
	/**
	 * Resume method. Should be used when screen is changed to active
	 */
	def resume()
}