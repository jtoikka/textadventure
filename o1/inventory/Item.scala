package o1.inventory


abstract class Item(val name: String, val desc: String) {
  def getDescription: String
}