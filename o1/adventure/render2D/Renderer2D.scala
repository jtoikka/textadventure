package o1.adventure.render2D

import scala.swing.TextArea
import scala.collection.mutable.Buffer
import scala.math._
import o1.scene._
import o1.adventure.render2D._
import o1.adventure.render._
import o1.math._

object Renderer2D {
  //  val chars = "MWNQBHKR#EDFXOAPGUSVZYCLTJ$I*:."
  val chars = "\u2001\u2591\u2592\u2593\u2588".reverse
  val empty = '\u0000'
}

class Renderer2D(w: Int, h: Int) extends Renderer(w, h) {

  val newLineCharWidth = 1
  var frameBuffer: Array[Char] = new Array[Char]((w + newLineCharWidth) * h)

  /**
   * Used to clear buffer
   */
  def clear(): Unit = {
    for (y <- 0 until h) {
      for (x <- 0 until w) {
        setPixel(x, y, Renderer2D.empty)
      }
    }
  }

  def calcIndex(x: Int, y: Int): Int = y * w + x

  def initialize() = {
    this.clear()
    for (i <- 0 until frameBuffer.size) {
      frameBuffer(i) = '\n'
    }
  }
  initialize()

  def setPixel(p: Point2D, char: Char): Unit = setPixel(p.x, p.y, char)

  def setPixel(x: Int, y: Int, char: Char): Unit = {
    try {
      if (x >= 0 && y >= 0 && x < w - 1 && y < h)
        frameBuffer(calcIndex(x, y)) = char
    } catch {
      case e: ArrayIndexOutOfBoundsException => println("Invalid array index at setPixel. x:" + x +
        ", y:" + y + ", char:" + char)
    }
  }

  /**
   * Draws line between two points
   */
  def drawLine(p1: Point2D, p2: Point2D, color: Int): Unit =
    drawLine(p1.x, p1.y, p2.x, p2.y, color)

  def drawLine(x1: Int, y1: Int, x2: Int, y2: Int, color: Int): Unit = {
    // Using Brensenham's line drawing algorithm
    if ((x1 - x2) > 0) { drawLine(x2, y2, x1, y1, color); return ; }

    if (Math.abs(y2 - y1) > Math.abs(x2 - x1)) {
      // flip
      var x3 = min(y1, y2)
      var y3 = if (x3 == y1) x1 else x2 //x1
      var x4 = if (x3 == y1) y2 else y1 //y2
      var y4 = if (x3 == y1) x2 else x1 //x2

      var x = x3
      var y = y3
      var sum = x4 - x3
      var Dx = 2 * (x4 - x3)
      var Dy = Math.abs(2 * (y4 - y3))
      var a = if ((y4 - y3) > 0) 1 else -1

      for (i <- 0 to (x4 - x3)) {
        setPixel(y, x, Renderer2D.chars(color))
        x += 1
        sum -= Dy
        if (sum < 0) { y = y + a; sum += Dx }
      }
    } else {

      var x = x1
      var y = y1
      var sum = x2 - x1
      var Dx = 2 * (x2 - x1)
      var Dy = Math.abs(2 * (y2 - y1))
      var a = if ((y2 - y1) > 0) 1 else -1

      for (i <- 0 to (x2 - x1)) {
        setPixel(x, y, Renderer2D.chars(color))
        x += 1
        sum -= Dy
        if (sum < 0) y = y + a; sum += Dx
      }
    }
  }

  /**
   * Renders scene to framebuffer
   */
  def renderScene(scene: Scene): Unit = {
    for (entity <- scene.entities) {
      var rendComp = entity.getComponent(RenderComponent2D.id)
      if (rendComp.isDefined && rendComp.get.isActive) {
        var spatialComp = entity.getComponent(SpatialComponent.id)

        renderShape(ResourceManager.shapes(rendComp.get.shape),
          spatialComp.get.position)
      }
    }
  }

  /**
   * Switch method used to forward shape to right drawing method
   */
  def renderShape(shape: Shape, loc: Vec3) {
    var a = shape match {
      case tri: Triangle2D => renderTriangle(tri, loc)
      case img: Image2D => renderImage(img, loc)
      case textR: TextRect2D => renderTextRectangle(textR, loc)
      case rect: Rectangle2D => renderRectangle(rect, loc)
    }
  }

  def renderTriangle(tri: Triangle2D, loc: Vec3) {
    // TODO: Filling the triangle
    var p1 = tri.p1.move(loc)
    var p2 = tri.p2.move(loc)
    var p3 = tri.p3.move(loc)

    drawLine(p1, p2, tri.color1)
    drawLine(p2, p3, tri.color1)
    drawLine(p1, p3, tri.color1)
  }

  def renderRectangle(rect: Rectangle2D, loc: Vec3) {
    // TODO: Drawing filled rectangle doesn't work with colors as intended
    val p1 = new Point2D(loc.x.toInt, loc.y.toInt)
    val p2 = new Point2D(loc.x.toInt + rect.w, loc.y.toInt + rect.h)

    if (rect.defFill) {
      for (x <- p1.x to p2.x) {
        for (y <- p1.y to p2.y) {
          setPixel(x, y, Renderer2D.chars(rect.color2))
        }
      }

    } else {
      var p3 = new Point2D(p1.x, p2.y)
      var p4 = new Point2D(p2.x, p1.y)
      drawLine(p1, p3, rect.color1)
      drawLine(p3, p2, rect.color1)
      drawLine(p2, p4, rect.color1)
      drawLine(p1, p4, rect.color1)
    }
  }

  def renderImage(image: Image2D, loc: Vec3) {
    val fill = image.defFill
    val locX = loc.x.toInt
    val locY = loc.y.toInt

    for (x <- 0 until image.img.getWidth()) {
      for (y <- 0 until image.img.getHeight()) {
        var value = image.grayArray(image.calcImageArrayIndex(x, y))
        var char = getCharFrom8Bit(value)
        if (!(!fill && value != 0))
          setPixel(locX + x, locY + y, char)
      }
    }
  }

  private def getCharFrom8Bit(i: Int): Char = {
    val charCount = Renderer2D.chars.length()
    val step = 256 / charCount

    var count = (i - 1) / step

    var char: Char = Renderer2D.chars.charAt(count)
    char
  }

  def renderTextRectangle(tRect: TextRect2D, loc: Vec3) {
    // TODO: Fix offset and implement text wrapping
    val text = tRect.text
    val p1 = new Point2D(loc.x.toInt, loc.y.toInt)
    val p2 = new Point2D(loc.x.toInt + tRect.rect.w,
      loc.y.toInt + tRect.rect.h)

    // Text offsets
    val offX = tRect.offX
    val offY = tRect.offY
    val offMinusX = tRect.offMinusX
    val offMinusY = tRect.offMinusY

    var linesOrig = text.split('\n')
    var lines = Buffer[String]()
    val lineWidth = tRect.w - offMinusX - offX
    //    var lines = text.split("\n",lineWidth)

    // Wrap text if needed
    if (tRect.textWrap) {
      for (i <- linesOrig.indices) {
        var a = linesOrig(i)
        if (a.length > lineWidth) { // if line is longer than max line width
          var words = a.split(' ')
          while (!words.isEmpty) {
            var line = ""
            while (!words.isEmpty && // add as many words as possible to line
                line.length() < lineWidth - words(0).length()) {
              line += words(0) + " "
              words = words.tail
            }
            lines += line
          }
        } else {
          lines += linesOrig(i)
        }
      }
    } else {
      lines = linesOrig.toBuffer
    }

    // center text if needed
    if (tRect.centerText) {
      for (i <- lines.indices) {
        while (lines(i).length < lineWidth) {
          lines(i) = " " + lines(i) + " "
        }
      }
    }

    var lineCounter = 0

    // empty area
    for (x <- p1.x to p2.x) {
      for (y <- p1.y to p2.y) {
        setPixel(x, y, Renderer2D.chars.last)
      }
    }

    if (tRect.defFill) {
      var r = tRect.rect
      r.defFill = false
      renderRectangle(r, loc)
    }

    for (
      y <- p1.y + offY to
        Math.min(p2.y - offMinusY, (p1.y + lines.length) - 1 + offY)
    ) {
      var charCounter = 0

      for (
        x <- p1.x + offX to
          Math.min(p2.x - offMinusX, (p1.x + lines(lineCounter).length() - 1 + offX))
      ) {
        setPixel(x, y, lines(lineCounter).charAt(charCounter))
        charCounter += 1
      }
      lineCounter += 1
    }
  }
  /**
   * Used to draw 2d scene.
   */
  def display: String = {
    var disp = frameBuffer.mkString("")
    disp.replace(Renderer2D.empty, Renderer2D.chars.last)
  }

  /**
   * Used to draw 2d scene on top of another scene.
   * Takes empty char as transparent character.
   * For example, can draw 2d hud on top of 3d game.
   * Should only be used when needed.
   */

  def displayOverlay(disp: String): String = {
    var str = disp.toCharArray()
    for (i <- 0 to str.length - 1) {
      if (frameBuffer(i) != Renderer2D.empty &&
        frameBuffer(i) != '\n' && str(i) != '\n')
        str(i) = frameBuffer(i)
    }
    str.mkString("")
  }
}