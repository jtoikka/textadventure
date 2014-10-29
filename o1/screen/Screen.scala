package o1.screen

import scala.collection.mutable.Map
import o1.adventure._
import o1.adventure.render._
import scala.math._ 
import o1.event.Listener
/**
 *  Abstract Screen class. To be used with all screens. 
 * @param parent parent Adventure class
 **/

abstract class Screen(private val parent: Adventure, private val rend: Renderer) extends Listener{
  var display = rend.display;
  
	/**
	 * Update method. Used to update screen state
	 */
	def update(delta: Double)
	
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
	
	/**
	 * Pause method. Called when screen is not longer active
	 */
	def pause()
}