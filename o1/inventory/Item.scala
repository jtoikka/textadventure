package o1.inventory

import o1.adventure.render2D.Image2D


abstract class Item(val name: String, val desc: String) {
  var icon: String
}