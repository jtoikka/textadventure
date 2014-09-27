package o1.mapGenerator

import o1.math._
import scala.collection.mutable.ArrayBuffer
import o1.adventure.render.Mesh

class Corner(var location: Vec2, var edge: Option[Corner] = None) {}

object CornerMap {
  def getCoordinate(index: Int, width: Int): Vec2 =
    Vec2(index % width, index / width)
    
  def getIndex(coordinate: Vec2, width: Int, height: Int): Int = {
    if (coordinate.y < 0 || coordinate.y >= height
     || coordinate.x < 0 || coordinate.x >= width) {
      -1
    } else {
      coordinate.y.toInt * width + coordinate.x.toInt
    }
  }
  
  val turnRight = Mat2()
    turnRight(1).x = 1.0f
    turnRight(0).y = -1.0f
    
  val turnLeft = Mat2()
    turnLeft(1).x = -1.0f
    turnLeft(0).y = 1.0f
    
  def generateMap(level: Array[Int], width: Int): ArrayBuffer[Corner] = {
    var nodes = ArrayBuffer[Corner]()
    
    var currentBlock = 0
    var i = 0
    val height = level.length / width
    
    // Find first instance of an "empty" block
    while (currentBlock == 0 && i < level.size) {
      i += 1
      currentBlock = level(i)
    }
    println("i: " + i)
    var startCoordinate = getCoordinate(i, width)
    var coordinate = startCoordinate
    var forward = Vec2(1.0f, 0.0f)
    var right = Vec2(0.0f, -1.0f)
        
    nodes += new Corner(startCoordinate)
    
    
    // Moves along the outer wall, and adds a node each time a turn is made.
    // Each node is connected to the previous one, to create edges.
    var previousWasRightTurn = false
    do {
      var nextCoordinate = coordinate + forward
      var nextIndex = getIndex(nextCoordinate, width, height)
      if (nextIndex == -1 || level(nextIndex) == 0) {
        forward = turnLeft * forward
        right = turnLeft * right
        nodes += new Corner(coordinate, Some(nodes.last))
        previousWasRightTurn = false
      } else {
        var rightIndex = getIndex(coordinate + right, width, height)
        if (previousWasRightTurn && level(nextIndex) == 1) {
          coordinate = coordinate + forward
          previousWasRightTurn = false
        } else if (level(rightIndex) == 1) {
          forward = turnRight * forward
          right = turnRight * right
          nodes += new Corner(coordinate, Some(nodes.last))
          previousWasRightTurn = true
        } else {
          coordinate = coordinate + forward
          previousWasRightTurn = false
        }
      }
    } while ((getIndex(coordinate, width, height) != i))
    nodes(0).edge = Some(nodes.last)
    nodes
  }
  
  def createWallMesh(nodes: ArrayBuffer[Corner], wallHeight: Float): Mesh = {
    var vertices = ArrayBuffer[Float]()
    var indices = ArrayBuffer[Int]()
    
    var indexOffset = 0
    
    for (i <- 0 until nodes.size) {
      var node = nodes(i)
      var location = node.location
      // Add floor vertex
      vertices += location.x
      vertices += 0.0f
      vertices += location.y
      // Add ceiling vertex
      vertices += location.x
      vertices += wallHeight
      vertices += location.y
      
      indexOffset = (i - 1) * 2
      
      // Add indices for two triangles (form a quad)
      if (i != 0) {
        indices += 1 + indexOffset
        indices += 0 + indexOffset
        indices += 2 + indexOffset
        indices += 1 + indexOffset
        indices += 2 + indexOffset
        indices += 3 + indexOffset
      }
    }
    // Connect last and first corner
    indices += indexOffset + 1
    indices += indexOffset
    indices += 0
    indices += indexOffset + 1
    indices += 0
    indices += 1
    
    new Mesh(vertices, indices)
  }
}