package o1.adventure.render2D

import o1.event.Listener
import o1.event.Event
import o1.event.EventType
import scala.swing.event.Key
import o1.event.Input
import o1.event.EventManager

class Dialog(parent: Listener, rect: Rectangle2D, val dialogText: String, val options: Vector[String])
  extends TextRect2D(rect) with Listener {
  eventTypes = Vector[Int](EventType.E_INPUT, EventType.E_DIALOG)

  val indent = "\u2001"
  val indentCount: Int = 6
  var activeOption: Int = 0
  updateText()
  def updateOption(next: Boolean) = {

    var newOption = activeOption
    if (next) {
      newOption += 1
    } else {
      newOption -= 1
      if (newOption < 0)
        newOption = options.length - 1
    }
    activeOption = newOption % options.length
    updateText()
  }

  def updateOption(option: Int) = {
    activeOption = activeOption % options.length
    updateText()
  }

  def updateText() = {
    var t = dialogText + "\n"
    for (option <- options.indices) {
      if (activeOption == option)
        t += (indent.*(indentCount)) + "@" + options(option) + "\n"
      else
        t += (indent * indentCount) + options(option) + "\n"
    }
    text = t
  }

  override def handleEvents(delta: Float) = {
    for (event <- events) {
      handleEvent(event, delta)
    }
    events.clear()
  }
  def handleEvent(event: Event, delta: Float) = {
    if (event.eventType == EventType.E_INPUT) {
      val eventKey = event.args(0).asInstanceOf[Tuple2[scala.swing.event.Key.Value, Int]]
      if (inputMap.contains(eventKey)) {
        inputMap(eventKey)(delta)
      }
    }
  }

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
      ((Key.M, Input.KEYRELEASED), (delta) => {
        EventManager.addEvent(new Event(null,EventType.E_DIALOG))
      }))

}