package o1.adventure.render2D

import java.awt.image.BufferedImage
import o1.adventure.render.ResourceManager

class Image2D(var img: BufferedImage, var defFill: Boolean, var fixWidth: Boolean)
    extends Shape(Shape.defColor, Shape.defColor) {
  def this(img: BufferedImage, defFill: Boolean) = this(img, defFill, true)

  private val arrLenght = if (fixWidth) img.getWidth() * img.getHeight() * 2
  else img.getWidth() * img.getHeight()

  var grayArray: Array[Int] = new Array[Int](arrLenght)

  makeArray()

  def makeArray() = {
    if (!fixWidth) {
      for (x <- 0 until img.getWidth()) {
        for (y <- 0 until img.getHeight()) {
          //          grayArray(calcImageArrayIndex(x, y))
          var rgb = img.getRGB(x, y);
          var r = (rgb >> 16) & 0xFF;
          var g = (rgb >> 8) & 0xFF;
          var b = (rgb & 0xFF);
          var gray = (r + g + b) / 3
          var a = (rgb >> 25) & 0xFF;
          if (a <= 0)
            grayArray(calcImageArrayIndex(x, y)) = -1
          else
            grayArray(calcImageArrayIndex(x, y)) = gray
          //print(gray + ", ")

        }
      }
    } else {
      for (x <- 0 until img.getWidth()) {
        for (y <- 0 until img.getHeight()) {
          //          grayArray(calcImageArrayIndex(x, y))
          var rgb = img.getRGB(x, y);
          var r = (rgb >> 16) & 0xFF;
          var g = (rgb >> 8) & 0xFF;
          var b = (rgb & 0xFF);
          var gray = (r + g + b) / 3
          //print(gray + ", ")
          var a = (rgb >> 25) & 0xFF;
          if (a <= 0) {
            grayArray(calcImageArrayIndex(x * 2 + 1, y)) = -1
            grayArray(calcImageArrayIndex(x * 2, y)) = -1
          } else {
            grayArray(calcImageArrayIndex(x * 2 + 1, y)) = gray
            grayArray(calcImageArrayIndex(x * 2, y)) = gray
          }
          //          grayArray(calcImageArrayIndex(x * 2 + 1, y)) = gray
          //          grayArray(calcImageArrayIndex(x * 2, y)) = gray
        }
      }
    }
  }

  def getWidth(): Int = if (fixWidth) img.getWidth() * 2 else img.getWidth()

  def getHeight(): Int = img.getHeight()

  def getArray(img: BufferedImage): Array[Int] = grayArray

  def calcImageArrayIndex(x: Int, y: Int): Int = y * getWidth() + x
}