package o1.inventory

import scala.collection.mutable.Buffer
import o1.adventure.render2D.Image2D

object ItemContainer {
  val MAX_ITEM_COUNT = 99
}

class ItemContainer[T <: Item] {
  private val items = Buffer[T]()
  
  var hiddenContainer = false // if true doesn't show in inventory
  
  var icon:Option[String] = None
  var name:Option[String] = None
    
  def addItem(item: AnyRef): Boolean = {
    if (items.size < ItemContainer.MAX_ITEM_COUNT) {
      val it: T = item.asInstanceOf[T]
      items += it
      true
    } else false
  }
  
  def removeItem(item: AnyRef): Boolean = {
    if (items.size > 0) {
      val i = items.indexOf(item)
      if (i >= 0) {
        items.remove(i)
        true
      } else false
    } else false
  }

  def removeOne: Boolean = {
    if (items.size > 0) {
      items.remove(items.size - 1)
      true
    } else false
  }

  def size = items.length

  override def toString = "Container with " + items.length + " items. Type: " + items.last.getClass()
}