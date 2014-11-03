package o1.scene

import o1.event._
import o1.math.Vec2
import o1.adventure.render.ResourceManager
import scala.collection.mutable.Map

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

  def checkCollision(pos: Vec2, radius: Float): Vec2 = {
    val upperTileX = (pos.x / TILEWIDTH + 0.5).toInt
    val upperTileY = (pos.y / TILEWIDTH + 0.5).toInt

    // Check a 3 x 3 grid
    val tileAPos = Vec2(upperTileX, upperTileY)
    val tileBPos = tileAPos - Vec2(0.0f, 1.0f)
    val tileCPos = tileAPos - Vec2(1.0f, 0.0f)
    val tileDPos = tileAPos - Vec2(0.0f, -1.0f)
    val tileEPos = tileAPos - Vec2(-1.0f, 0.0f)
    val tileFPos = tileAPos - Vec2(1.0f, 1.0f)
    val tileGPos = tileAPos - Vec2(1.0f, -1.0f)
    val tileHPos = tileAPos - Vec2(-1.0f, -1.0f)
    val tileIPos = tileAPos - Vec2(-1.0f, 1.0f)

    val tileAIndex = getIndex(tileAPos.x.toInt, tileAPos.y.toInt)
    val tileBIndex = getIndex(tileBPos.x.toInt, tileBPos.y.toInt)
    val tileCIndex = getIndex(tileCPos.x.toInt, tileCPos.y.toInt)
    val tileDIndex = getIndex(tileDPos.x.toInt, tileDPos.y.toInt)
    val tileEIndex = getIndex(tileEPos.x.toInt, tileEPos.y.toInt)
    val tileFIndex = getIndex(tileFPos.x.toInt, tileFPos.y.toInt)
    val tileGIndex = getIndex(tileGPos.x.toInt, tileGPos.y.toInt)
    val tileHIndex = getIndex(tileHPos.x.toInt, tileHPos.y.toInt)
    val tileIIndex = getIndex(tileIPos.x.toInt, tileIPos.y.toInt)

    var intersection = new Vec2(0.0f, 0.0f)

    def combineIntersection(intersectionA: Vec2, intersectionB: Vec2): Vec2 = {
      val newIntersection = new Vec2(intersectionA.x, intersectionA.y)
      if (intersectionB.x != 0.0f) {
        if (intersectionA.x != 0.0f) {
          if (intersectionA.x.abs > intersectionB.x.abs) {
            newIntersection.x = intersectionB.x
          }
        } else {
          newIntersection.x = intersectionB.x
        }
      }
      if (intersectionB.y != 0.0f) {
        if (intersectionA.y != 0.0f) {
          if (intersectionA.y.abs < intersectionB.y.abs) {
            newIntersection.y = intersectionB.y
          }
        } else {
          newIntersection.y = intersectionB.y
        }
      }
      newIntersection
    }

    if (tileAIndex >= 0) {
      intersection = collisionMap(tileAIndex).checkIntersection(tileAPos * 2, pos, radius)
    }
    if (tileBIndex >= 0 && intersection.x == 0.0f && intersection.y == 0.0f) {
      val localIntersection =
        collisionMap(tileBIndex).checkIntersection(tileBPos * 2, pos, radius)
      intersection = combineIntersection(intersection, localIntersection)
    }
    if (tileCIndex >= 0 && intersection.x == 0.0f && intersection.y == 0.0f) {
      val localIntersection =
        collisionMap(tileCIndex).checkIntersection(tileCPos * 2, pos, radius)
      intersection = combineIntersection(intersection, localIntersection)
    }
    if (tileDIndex >= 0 && intersection.x == 0.0f && intersection.y == 0.0f) {
      val localIntersection =
        collisionMap(tileDIndex).checkIntersection(tileDPos * 2, pos, radius)
      intersection = combineIntersection(intersection, localIntersection)
    }
    if (tileEIndex >= 0 && intersection.x == 0.0f && intersection.y == 0.0f) {
      val localIntersection =
        collisionMap(tileEIndex).checkIntersection(tileEPos * 2, pos, radius)
      intersection = combineIntersection(intersection, localIntersection)
    }
    if (tileFIndex >= 0 && intersection.x == 0.0f && intersection.y == 0.0f) {
      val localIntersection =
        collisionMap(tileFIndex).checkIntersection(tileFPos * 2, pos, radius)
      intersection = combineIntersection(intersection, localIntersection)
    }
    if (tileGIndex >= 0 && intersection.x == 0.0f && intersection.y == 0.0f) {
      val localIntersection =
        collisionMap(tileGIndex).checkIntersection(tileGPos * 2, pos, radius)
      intersection = combineIntersection(intersection, localIntersection)
    }
    if (tileHIndex >= 0 && intersection.x == 0.0f && intersection.y == 0.0f) {
      val localIntersection =
        collisionMap(tileHIndex).checkIntersection(tileHPos * 2, pos, radius)
      intersection = combineIntersection(intersection, localIntersection)
    }
    if (tileIIndex >= 0 && intersection.x == 0.0f && intersection.y == 0.0f) {
      val localIntersection =
        collisionMap(tileIIndex).checkIntersection(tileIPos * 2, pos, radius)
      intersection = combineIntersection(intersection, localIntersection)
    }

    intersection
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