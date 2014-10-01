package o1.adventure.render2D

import java.awt.image.BufferedImage

class Image2D(var img: BufferedImage, var defFill: Boolean) 
  extends Shape(Shape.defColor, Shape.defColor) {
  
  var grayArray: Array[Int] = new Array[Int](img.getWidth() * img.getHeight())
  
  makeArray()
  
  def makeArray() = {
    for (x <- 0 until img.getWidth()) {
      for (y <- 0 until img.getHeight()) {
        grayArray(calcImageArrayIndex(x,y))
        var rgb = img.getRGB(x, y);
        var r = (rgb >> 16) & 0xFF;
        var g = (rgb >> 8) & 0xFF;
        var b = (rgb & 0xFF);
        var gray = (r+g+b) / 3
        //print(gray + ", ")
        grayArray(calcImageArrayIndex(x,y)) = gray
      }
    }
  }
  /**
   * Duplicates every pixel horizontally. 
   * Use with extreme caution!
   */
  def fixPixelShape() = {
  }
  
  def getArray(img: BufferedImage): Array[Int] = grayArray
  
  def calcImageArrayIndex(x: Int, y: Int): Int = y * img.getWidth() + x
}