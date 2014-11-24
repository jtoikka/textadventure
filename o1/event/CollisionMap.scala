package o1.event

import o1.scene._
import o1.math.Vec2

abstract class CollisionTile() {
  var color = -1
  var id = -1
  def checkIntersection(tilePosition: Vec2, entity: Entity): Vec2 = {
    val spatial = entity.getComponent(SpatialComponent.id)
    val collisionComp = entity.getComponent(CollisionComponent.id)
    if (spatial.isDefined && collisionComp.isDefined)
      checkIntersection(
          tilePosition, spatial.get.position.xz, collisionComp.get.radius)
    else
      Vec2(0.0f, 0.0f)
  }
  
  def checkIntersection(tilePosition: Vec2, position: Vec2, radius: Float): Vec2 
}

class EmptyTile extends CollisionTile {
  color = 0xFFFFF0
  id = 0
  
  val halfWidth = 1.0
  
  def checkIntersection(tilePosition: Vec2, pos: Vec2, radius: Float): Vec2 = {
    Vec2(0.0f, 0.0f)
  }
}

class FakeSolidTile extends CollisionTile {
  color = 0
  id = 2
  
  val halfWidth = 1.0
  
  def checkIntersection(tilePosition: Vec2, pos: Vec2, radius: Float): Vec2 = {
    Vec2(0.0f, 0.0f)
  }
}

class SolidTile extends CollisionTile {
  color = 0
  id = 1
  
  val halfWidth = 1.0
  
  def checkIntersection(tilePosition: Vec2, pos: Vec2, radius: Float): Vec2 = {
    var intersection = Vec2(0.0f, 0.0f)
    
    val relativePosition = pos - tilePosition
    
    val sumAxes = radius + halfWidth
    
    val intersectionX = relativePosition.x.abs - sumAxes
    val intersectionY = relativePosition.y.abs - sumAxes
        
    if (intersectionX < 0.0f && intersectionY < 0.0f) {
     if ((relativePosition.x < sumAxes && relativePosition.x > -sumAxes) ||
         (relativePosition.y < sumAxes && relativePosition.y > -sumAxes)) { 
          intersection = 
            Vec2((intersectionX * relativePosition.x.signum).toFloat, (intersectionY * relativePosition.y.signum).toFloat)
      } else {
        val cornerX = relativePosition.x.signum * halfWidth
        val cornerY = relativePosition.y.signum * halfWidth
        
        val colliderToCorner = pos - Vec2(cornerX.toFloat, cornerY.toFloat)
        
        val colliderToConerLength = colliderToCorner.length
        
        if (colliderToConerLength < radius) {
          intersection = colliderToCorner * (radius - colliderToConerLength)
        }
      }
    }
    
    if (intersection.x.abs > intersection.y.abs) {
      new Vec2(0.0f, intersection.y)
    } else {
      new Vec2(intersection.x, 0.0f)
    }
  }
}

class CollisionMap(width: Int, height: Int) {
  val tiles = Array.fill[CollisionTile](width * height)(new EmptyTile())
  
  def getIndex(x: Int, y: Int): Int= {
    if (x >= 0 && x < width && y >= 0 && y < height) {
      y * width + x
    } else {
      -1
    }
  }
  
  def addTile[T <: CollisionTile](x: Int, y: Int, tileType: T) = {
    val index = getIndex(x, y)
    if (index != -1) {
      tiles(index) = tileType
    }
  }
  
  def checkCollision(pos: Vec2, radius: Float) = {
    
  }
}