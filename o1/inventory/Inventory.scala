package o1.inventory
import scala.collection.mutable.Map
import o1.adventure.render.ResourceManager
import scala.reflect.ClassTag

object Inventory {
  val MAX_INVENTORY_ITEM_COUNT = 6

  val containers = Map[Class[_], ItemContainer[_]]()
  
  def addItem(item: Item): Boolean = {
//    println("Adding item")
    if (containers.contains(item.getClass())) {
      val bool = containers(item.getClass()).addItem(item)
      bool
    } else {
      containers(item.getClass()) = new ItemContainer
      
      val bool = containers(item.getClass()).addItem(item)
      
      containers(item.getClass()).icon = Some(item.icon)
      containers(item.getClass()).name = Some(item.name)
      
      if (nonHiddenCount > MAX_INVENTORY_ITEM_COUNT){
//        println("More inventory items than slots in screen. This is a problem!")
        }
      bool
    }
  }
  
  def removeItem(item: Item): Boolean  = {
    if (containers.contains(item.getClass())) {
//      println("Container exists. Removing item")
      containers(item.getClass()).removeItem(item)
      if (containers(item.getClass()).size <= 0) { // remove if no items
        containers.remove(item.getClass()) 
      }
      true
    } else {
//      println("Container not found. Can't remove")
      false
    }
  }

  def removeOneOfType(item: Item): Boolean = {
    if (containers.contains(item.getClass())) {
//      println("Container exists. Removing one of type")
      containers(item.getClass()).removeOne
      true
    } else false
  }
  
  override def toString: String = {
    var contString = ""
    for (i <- containers)
      contString += i.toString() + "\n"

    val string = "The Inventory has " + containers.size + " categories:\n" + contString +
      "\nNon hidden category count: " + nonHiddenCount +
      "\nTotal item count: " + totalItemCount
    string
  }

  def nonHiddenCount: Int = {
    var c = 0
    for (i <- containers) {
      if (!i._2.hiddenContainer)
        c += 1
    }
    c
  }
  def totalItemCount: Int = {
    var c = 0
    for (i <- containers) {
      c += i._2.size
    }
    c
  }
}