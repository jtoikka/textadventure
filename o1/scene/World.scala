package o1.scene

import o1.event._
import o1.math.Vec2
import o1.adventure.render.ResourceManager
import scala.collection.mutable.Map
import scala.collection.mutable.Buffer

class TileMap(val width: Int, val height: Int) {
  val TILEWIDTH = 2

  val collisionMap = Array.fill[CollisionTile](width * height)(new EmptyTile())
  val visualMap = Array.fill[Int](width * height)(0)

  def getIndex(x: Int, y: Int): Int = {
    if (x >= 0 && x < width && y >= 0 && y < height) {
      y * width + x
    } else {
      -1
    }
  }

  def addCollisionTile[T <: CollisionTile](x: Int, y: Int, tileType: T) = {
    val index = getIndex(x, y)
    if (index != -1) {
      collisionMap(index) = tileType
    }
  }

  def getCollisionTile(x: Int, y: Int): CollisionTile = {
    val index = getIndex(x, y)
    if (index >= 0) {
      collisionMap(index)
    } else {
      new EmptyTile()
    }
  }
  
  def checkCollisions(pos: Vec2, radius: Float): Vector[Vec2] = {
    val centerTileX = (pos.x / TILEWIDTH).toInt
    val centerTileY = (pos.y / TILEWIDTH).toInt
    
    val intersections = Buffer[Vec2]()
    
    println(radius)
    
    for (x <- -1 to 1) {
      for (y <- -1 to 1) {
        val tilePos = Vec2(centerTileX + x, centerTileY + y)
        val tileIndex = getIndex(centerTileX + x, centerTileY + y)
        
        if (tileIndex >= 0) {
          val intersection = 
            collisionMap(tileIndex).checkIntersection(
                tilePos * TILEWIDTH, pos, radius)
                
          if (intersection.x.abs + intersection.y.abs != 0) {
            intersections += intersection
          }
        }
      }
    }    
    intersections.toVector
  }
}
class World(val map: String) {

  val xml = ResourceManager.maps(map)

  val xmlDepth = (xml \ "@height")
  val xmlWidth = (xml \ "@width")

  val depth = xmlDepth.text.toInt
  val width = xmlWidth.text.toInt

  val tileMap = new TileMap(depth, width)

  val layers = Map[String, scala.xml.Node]()

  for (layer <- xml \ "layer") {
    val name = layer \ "@name"
    layers(name.text) = layer
  }
  
  val collisionTiles = (layers("collision") \ "data" \ "tile").toArray
  val meshTiles = (layers("mesh") \ "data" \ "tile").toArray
  for (y <- 0 until depth) {
    for (x <- 0 until width) {
      val g = collisionTiles(y * width + x) \ "@gid"
      if(g.text.toInt == 1){ // firstGid => Solid Tile
        tileMap.addCollisionTile(x, y, new SolidTile())

      }else{
      }
      
    }
  }

}