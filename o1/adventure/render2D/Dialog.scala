package o1.adventure.render2D

import o1.event.Listener
import o1.event.Event
import o1.event.EventType._
import scala.swing.event.Key
import o1.event.Input
import o1.event.EventManager

class Dialog(
    parent: Option[Listener], 
    rect: Rectangle2D, 
    val dialogText: String, 
    val options: Vector[Tuple2[String, Event]])
  extends TextRect2D(rect) with Listener {
  
  this.centerText = true
  var seperatorRows = 0
  val marker = "---> "
  val endMarker = " <---"
  val indent = "\u2001"
  val indentCount: Int = 0
  var activeOption: Int = 0
  var active = true
  updateText()
  
  def updateOption(next: Boolean) = {
    if(options.length > 0){
      var newOption = activeOption
      if (next) {
        newOption += 1
      } else {
        newOption -= 1
        if (newOption < 0)
          newOption = options.length - 1
      }
      activeOption = newOption % options.length
    }
    updateText()
    // println(activeOption)
  }

  // Changes current option
  def updateOption(option: Int) = {
    activeOption = activeOption % options.length
    updateText()
  }

  def updateText() = {
    var t = dialogText + "\n"
    for(i <- 1 to seperatorRows){
      t +="\n"
    }
    
    for (option <- options.indices) {
      if (activeOption == option)
        t += (indent.*(indentCount - 
            marker.length() - 
            endMarker.length())) + 
            marker + 
            options(option)._1 + endMarker + "\n"
      else
        t += (indent * indentCount) + options(option)._1 + "\n"
    }
    text = t
  }
  
  eventHandlers = scala.collection.immutable.Map(
    (E_INPUT, (event, delta) => {
      val eventKey = event.args(0).asInstanceOf[Tuple2[scala.swing.event.Key.Value, Int]]
      if (inputMap.contains(eventKey)) {
        inputMap(eventKey)(delta)
      }
    }))

  val inputMap =
    Map[Tuple2[scala.swing.event.Key.Value, Int], (Float) => Unit](
      ((Key.W, Input.KEYRELEASED), (delta) => {
        updateOption(false)
      }),
      ((Key.S, Input.KEYRELEASED), (delta) => {
        updateOption(true)
      }),
      ((Key.A, Input.KEYRELEASED), (delta) => {
        
      }),
      ((Key.D, Input.KEYRELEASED), (delta) => {

      }),
      ((Key.Enter, Input.KEYRELEASED), (delta) => {
        active = false
        EventManager.addEvent(options(activeOption)._2)
        EventManager.addEvent(new Event(Vector("",-1),E_ANSWER_DIALOG))
        dispose()
      }),
      ((Key.M, Input.KEYRELEASED), (delta) => {
      }))
      
  val optionsMap = 
    Array[Tuple2[String, Int] => Unit](
        
    )
  
  def dispose() = {
    if(parent.isDefined)
      EventManager.setActiveInputListener(parent.get)
    else
      EventManager.returnToLastInputListener()
  }
  
  def setActive() = {
    EventManager.setActiveInputListener(this)
  }
}