package o1.screen

import scala.swing.event.Key
import scala.collection.mutable.Map
import o1.adventure._
import o1.adventure.render.Renderer
import o1.adventure.render.ResourceManager
import scala.math._
import o1.math._
import o1.scene._
import o1.adventure.render2D._
import scala.swing.Font
import o1.mapGenerator.MapGenerator
import o1.mapGenerator.CornerMap

class TestScreen2D(parent: Adventure, rend: Renderer)
  extends Screen(parent, rend) {

  def this(parent: Adventure, x: Int, y: Int) = this(parent, new Renderer2D(x, y))

  var scene = new Scene()

  /**
   * Initializing test screen entities.
   */
  def init(): Unit = {
    var hudTextRect = new TextRect2D(new Rectangle2D(32, 10, true),
      " " * 6 + "3D Text Adventure!")

    hudTextRect.offX = 2
    hudTextRect.offMinusX = 1
    hudTextRect.offMinusY = 1

    var rectEnt = Factory2D.createTextRectangle(hudTextRect)
    var testRectSpatial = rectEnt.getComponent(SpatialComponent.id)
    testRectSpatial.get.position = Vec3(rend.w / 2 - hudTextRect.w / 2, 25, 0.0f)
    scene.addEntity(rectEnt)

    var border = Factory2D.createRectangle(rend.w - 3, rend.h - 3, false)
    var bSpatial = border.getComponent(SpatialComponent.id)
    bSpatial.get.position = Vec3(1f, 1f, 0f)
    scene.addEntity(border)

    var name = "cross"
    var img = Factory2D.createImage(name)
    var spat = img.getComponent(SpatialComponent.id)

    var width = ResourceManager.images(name).getWidth()
    var heigth = ResourceManager.images(name).getHeight()

    spat.get.position = Vec3(rend.w / 2 - width / 2 + 1, 4.0f, 0.0f)
    scene.addEntity(img)
  }
  init()

  /**
   * Update method. Used to update game's state
   */

  def update(delta: Double): Unit = {
  }

  def input(keyMap: Map[scala.swing.event.Key.Value, Int], delta: Double) = {
    if (keyMap(Key.M) == 2) {
      parent.changeScreen(parent.gameScreen)
    }
  }

  /**
   * Draw method. Is used to draw screen to display etc
   */
  def draw(): Unit = {
    rend.clear()
    rend.renderScene(scene)
    display = rend.display
  }

  def resume(): Unit = {
    println("TestScreen2D resumed")
  }

  def pause() {
  }

}