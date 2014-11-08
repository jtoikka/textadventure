package o1.adventure.render

import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.File

class Texture(filePath: String) {
  val image = ImageIO.read(new File(filePath))
  
  private val _width = image.getWidth()
  private val _height = image.getHeight()
  
  private val _data = Vector.tabulate(width, height)((x, y) => {
    // Use the red channel as the value for grey
    (image.getRGB(x, y) >> 16) & 0xFF
  })
  
  def width = _width
  def height = _height
  
  private def clamp(minimum: Float, maximum: Float, value: Float): Float = {
    Math.max(Math.min(maximum, value), minimum)
  }
    
  def getPixel(u: Float, v: Float) = {
    var x = u
    while (x < 0) {
      x += 1
    }
    var y = v
    while (y < 0) {
      y += 1
    }
    x *= width
    y *= height
    x = clamp(0, width - 1, x)
    y = clamp(0, height - 1, y)
    
    _data(x.toInt)(y.toInt)
  }
}