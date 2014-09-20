package o1.adventure

import scala.collection.mutable.Map

  
/**
 * A `Player` object represents a player character controlled by the real-life user of the program. 
 * 
 * A player object's state is mutable: the player's location and possessions can change, for instance.
 * 
 * @param startingArea  the initial location of the player
 */
class Player(startingArea: Area) {

  private var currentLocation = startingArea        // gatherer: changes in relation to the previous location
  private var quitCommandGiven = false              // one-way flag
   
  
  /**
   * Determines if the player has indicated a desire to quit the game.
   */
  def hasQuit = this.quitCommandGiven

  
  /**
   * Returns the current location of the player.
   */
  def location = this.currentLocation
  

  /**
   * Attempts to move the player in the given direction. This is successful if there 
   * is an exit from the player's current location towards the given direction.
   * 
   * @param direction  a direction name (may be a nonexistent direction)
   * @return a description of the results of the attempt 
   */
  def go(direction: String) = {
    val destination = this.location.neighbor(direction)
    this.currentLocation = destination.getOrElse(this.currentLocation) 
    if (destination.isDefined) "You go " + direction + "." else "You can't go " + direction + "."
  }

  
  /**
   * Causes the player to rest for a short while (this has no substantial effect in game terms).
   * 
   * @return a description of what happened
   */
  def rest() = {
    "You rest for a while. Better get a move on, though." 
  }
  
  
  /**
   * Signals that the player wants to quit the game.
   * 
   * @return a description of what happened within the game as a result (which is the empty string, in this case) 
   */
  def quit() = {
    this.quitCommandGiven = true
    ""
  }

  
  /**
   * Returns a brief description of the player's state, for debugging purposes.
   */
  override def toString = "Now at: " + this.location.name   


  
}


