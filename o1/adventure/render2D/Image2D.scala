package o1.adventure.render2D

import java.awt.image.BufferedImage
import o1.adventure.render.ResourceManager
object Image2D {
  def apply(name: String) =
    new Image2D(ResourceManager.images(name), true)
  def apply(name: String, defFill: Boolean) =
    new Image2D(ResourceManager.images(name), defFill)
  def apply(name: String, defFill: Boolean, fixWidth: Boolean) =
    new Image2D(ResourceManager.images(name), defFill, fixWidth)
}
class Image2D(var img: BufferedImage, var defFill: Boolean, val fixWidth: Boolean)
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
          //print(gray + ", ")
          grayArray(calcImageArrayIndex(x, y)) = gray
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
          grayArray(calcImageArrayIndex(x * 2 + 1, y)) = gray
          grayArray(calcImageArrayIndex(x * 2, y)) = gray
        }
      }
    }
  }
  
  /**
   * Duplicates every pixel horizontally.
   * Use with extreme caution!
   */
  def fixPixelShape() = {

  }

  def getWidth(): Int = if (fixWidth) img.getWidth() * 2 else img.getWidth()
  def getHeight(): Int = img.getHeight()

  def getArray(img: BufferedImage): Array[Int] = grayArray

  def calcImageArrayIndex(x: Int, y: Int): Int = y * getWidth() + x
}