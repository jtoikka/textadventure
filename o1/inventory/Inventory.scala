package o1.inventory
import scala.collection.mutable.Map
import o1.adventure.render.ResourceManager
import scala.reflect.ClassTag
import scala.collection.mutable.Buffer

object Inventory {
    val MAX_INVENTORY_ITEM_COUNT = 6
}
class Inventory {
  val containers = Map[Class[_], ItemContainer[_]]()
  
  def getAllItems() : Vector[Item] = {
    val buf = Buffer[Item]()
    containers.values.foreach(f => f.getAllItems().foreach(a => buf += a))
    buf.toVector
  }
  
  def addItem(item: Item): Boolean = {
    if (containers.contains(item.getClass())) {
      val bool = containers(item.getClass()).addItem(item)
      bool
    } else {
      containers(item.getClass()) = new ItemContainer
      
      val bool = containers(item.getClass()).addItem(item)
      
      containers(item.getClass()).icon = Some(item.icon)
      containers(item.getClass()).name = Some(item.name)
      
      if (nonHiddenCount > Inventory.MAX_INVENTORY_ITEM_COUNT){
        }
      bool
    }
  }
  
  def removeItem(item: Item): Boolean  = {
    if (containers.contains(item.getClass())) {
      containers(item.getClass()).removeItem(item)
      if (containers(item.getClass()).size <= 0) {
        containers.remove(item.getClass()) 
      }
      true
    } else {
      false
    }
  }

  def removeOneOfType(item: Item): Boolean = {
    if (containers.contains(item.getClass())) {
      containers(item.getClass()).removeOne
      if (containers(item.getClass()).size <= 0) {
        containers.remove(item.getClass()) 
      }
      true
    } else false
  }
  def removeOfType(item: Item,count:Int): Boolean = {
    if (containers.contains(item.getClass()) && containers(item.getClass()).size >= count) {
      containers(item.getClass()).remove(count)
      if (containers(item.getClass()).size <= 0) {
        containers.remove(item.getClass()) 
      }
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
    var count = 0
    for (i <- containers) {
      if (!i._2.hiddenContainer)
        count += 1
    }
    count
  }
  def totalItemCount: Int = {
    var count = 0
    for (i <- containers) {
      count += i._2.size
    }
    count
  }
}