package o1.inventory

import o1.adventure.render.ResourceManager

object InventoryTestApp extends App{
  val inv = Inventory
  println("## " + inv.toString())
  
  val page1 = Page("Page1","This is a page1")
  val page2 = Page("Page2","This is a page2")
  val page3 = Page("Page3","This is a page3")
  val page4 = Page("Page4","This is a page4")
  val page5 = Page()
  
  val c1 = Coffee()
  val c2 = Page("Page2","This is a page2")
  val c3 = Page("Page3","This is a page3")
  val c4 = Page("Page4","This is a page4")
  val c5 = Page()
  
  inv.addItem(page1)
  inv.addItem(page2)
  inv.addItem(page3)
  inv.addItem(page4)
  println("## " + inv.toString())
  inv.removeItem(page1)
  inv.removeItem(page2)
  println("## " + inv.toString())
  inv.removeItem(page5)
  println("## " + inv.toString())
  inv.removeOneOfType(Page())
  println("## " + inv.toString())
}