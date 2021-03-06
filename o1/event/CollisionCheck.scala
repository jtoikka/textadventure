package o1.event

import o1.math.Vec2
import o1.inventory.Inventory
import scala.Vector
import o1.math.Vec3
import o1.scene._

object CollisionCheck {
  
  def findEntityCollision(entity: Entity, entities: Vector[Entity], filter: (Entity) => Boolean): Option[Entity] = {
    val collisionComponent = entity.getComponent(CollisionComponent.id)
    val spatialComponent = entity.getComponent(SpatialComponent.id)
    if (collisionComponent.isDefined) {
      for (
        otherEntity <- entities.filter((other) => {
          other != entity && other.getComponent(CollisionComponent.id).isDefined &&
          filter(other)
        })) {
        val otherCollisionComponent =
          otherEntity.getComponent(CollisionComponent.id)

        val otherSpatialComponent =
          otherEntity.getComponent(SpatialComponent.id)

        val posEntity = spatialComponent.get.position
        val posOther = otherSpatialComponent.get.position
        
        var intersection = Vec3(0.0f, 0.0f, 0.0f)
        
        if (otherCollisionComponent.get.shape == CollisionComponent.CIRCLE &&
            collisionComponent.get.shape == CollisionComponent.CIRCLE) {
          val diff = posEntity - posOther
  
          val totalRadius =
            collisionComponent.get.radius + otherCollisionComponent.get.radius
            
          intersection = checkRoundIntersection(
              posEntity, posOther, 
              collisionComponent.get.radius, 
              otherCollisionComponent.get.radius)
        } else if (collisionComponent.get.shape == CollisionComponent.CIRCLE &&
                   otherCollisionComponent.get.shape == CollisionComponent.SQUARE) {
          intersection = checkRectangleIntersection(
              posOther, 
              otherCollisionComponent.get.halfWidth,
              otherCollisionComponent.get.halfHeight,
              posEntity, 
              collisionComponent.get.radius).neg()
        } else if (otherCollisionComponent.get.shape == CollisionComponent.CIRCLE &&
                   collisionComponent.get.shape == CollisionComponent.SQUARE) {
          intersection = checkRectangleIntersection(
              posEntity, 
              collisionComponent.get.halfWidth,
              collisionComponent.get.halfHeight,
              posOther, 
              otherCollisionComponent.get.radius).neg()
        }
        if (intersection.x != 0 || intersection.z != 0) {
          return Some(otherEntity)
        }
      }
    }
    None
  }

  def findWorldCollision(entity: Entity, collisionRadius: Float, world: World): Boolean = {
    val position = entity.getComponent(SpatialComponent.id).get.position
    def greatestIntersection(intersections: Vector[Vec2]) = {
      var greatest = 0.0f
      var greatestVec = Vec2(0.0f, 0.0f)
      for (intersection <- intersections) {
        if (intersection.x.abs > greatest || intersection.y.abs > greatest) {
          greatestVec = intersection
          greatest = Math.max(intersection.x.abs, intersection.y.abs)
        }
      }
      greatestVec
    }

    var intersections = world.tileMap.checkCollisions(
      position.xz,
      collisionRadius)
    var i = 0
    val maxChecks = 4
    !intersections.isEmpty
  }
  
  def checkCollisions(entity: Entity, entities: Vector[Entity], world: Option[World]) = {
    val collisionComponent = entity.getComponent(CollisionComponent.id)
    val spatialComponent = entity.getComponent(SpatialComponent.id)
    if (collisionComponent.isDefined) {
      for (
        otherEntity <- entities.filter((other) => {
          other != entity && other.getComponent(CollisionComponent.id).isDefined
        })) {
        val otherCollisionComponent =
          otherEntity.getComponent(CollisionComponent.id)

        val otherSpatialComponent =
          otherEntity.getComponent(SpatialComponent.id)

        val posEntity = spatialComponent.get.position
        val posOther = otherSpatialComponent.get.position
        
        var intersection = Vec3(0.0f, 0.0f, 0.0f)
        
        if (otherCollisionComponent.get.shape == CollisionComponent.CIRCLE &&
            collisionComponent.get.shape == CollisionComponent.CIRCLE) {
          intersection = checkRoundIntersection(
              posEntity, posOther, 
              collisionComponent.get.radius, 
              otherCollisionComponent.get.radius)
          if (intersection.x != 0 || intersection.z != 0) {
            handleCollision(entity, otherEntity, intersection)
          }
              
        } else if (collisionComponent.get.shape == CollisionComponent.CIRCLE &&
                   otherCollisionComponent.get.shape == CollisionComponent.SQUARE) {
          intersection = checkRectangleIntersection(
              posOther, 
              otherCollisionComponent.get.halfWidth,
              otherCollisionComponent.get.halfHeight,
              posEntity, 
              collisionComponent.get.radius).neg()
          if (intersection.x != 0 || intersection.z != 0) {
            handleCollision(entity, otherEntity, intersection)
          }
        } else if (otherCollisionComponent.get.shape == CollisionComponent.CIRCLE &&
                   collisionComponent.get.shape == CollisionComponent.SQUARE) {
          intersection = checkRectangleIntersection(
              posEntity, 
              collisionComponent.get.halfWidth,
              collisionComponent.get.halfHeight,
              posOther, 
              otherCollisionComponent.get.radius).neg()
          if (intersection.x != 0 || intersection.z != 0) {
            if (otherCollisionComponent.get.isStatic) {
              handleCollision(entity, otherEntity, intersection)
            } else {
              handleCollision(otherEntity, entity, intersection)
            }
          }
        }
        if (intersection.x != 0 || intersection.z != 0) {
//            def sendIntersectionEvent(entityA: Entity, entityB: Entity) = {
//              val event = new Event(
//                Vector(entityA, entityB, intersection),
//                EventType.E_COLLISION)
//              EventManager.addEvent(event)
//            }
//            sendIntersectionEvent(entity, otherEntity)
//            sendIntersectionEvent(otherEntity, entity)
          }
      }
      if (collisionComponent.get.isActive)
        checkWorldCollisions(entity, collisionComponent.get.radius, world.get)
    }
  }
  
  def checkRoundIntersection(posEntity: Vec3, posOther: Vec3, radiusEntity: Float, radiusOther: Float): Vec3 = {
    val diff = posEntity - posOther
    diff.y = 0.0f

    val totalRadius = radiusEntity + radiusOther
      
    var intersection = Vec3(0.0f, 0.0f, 0.0f)

    // Check if collision
    if (diff.length < totalRadius) {
      intersection = Vec3(diff.x, 0.0f, diff.z) * ((totalRadius - diff.length) / diff.length)
    } 
     intersection
  }
  
  def checkRectangleIntersection(
      rectanglePos: Vec3, 
      halfWidth: Float, halfHeight: Float, 
      circlePos: Vec3, radius: Float): Vec3 = {
    var intersection = Vec3(0.0f, 0.0f, 0.0f)
    
    val relativePosition = circlePos - rectanglePos
    
    val sumWidth = halfWidth + radius
    val sumHeight = halfHeight + radius
    
    val intersectionX = relativePosition.x.abs - sumWidth
    val intersectionY = relativePosition.z.abs - sumHeight
    
    
    if (intersectionX < 0.0 && intersectionY < 0.0) {
      if ((relativePosition.x < sumWidth && relativePosition.x > -sumWidth) ||
          (relativePosition.z < sumHeight && relativePosition.z > -sumHeight)) {
        intersection = Vec3(
            (intersectionX * relativePosition.x.signum).toFloat, 
            0.0f, 
            (intersectionY * relativePosition.z.signum).toFloat)
      } else {
        val cornerX = relativePosition.x.signum * halfWidth
        val cornerY = relativePosition.z.signum * halfHeight
        
        val colliderToCorner = circlePos - Vec3(cornerX.toFloat, 0.0f, cornerY.toFloat)
        
        val colliderToConerLength = colliderToCorner.length
        
        if (colliderToConerLength < radius) {
          intersection = colliderToCorner * (radius - colliderToConerLength)
        }
      }
    }
    if (intersection.x.abs > intersection.z.abs) {
      new Vec3(0.0f, 0.0f, intersection.z)
    } else {
      new Vec3(intersection.x, 0.0f, 0.0f)
    }
  }

  def checkWorldCollisions(entity: Entity, collisionRadius: Float, world: World) = {
    val position = entity.getComponent(SpatialComponent.id).get.position
    def greatestIntersection(intersections: Vector[Vec2]) = {
      var greatest = 0.0f
      var greatestVec = Vec2(0.0f, 0.0f)
      for (intersection <- intersections) {
        if (intersection.x.abs > greatest || intersection.y.abs > greatest) {
          greatestVec = intersection
          greatest = Math.max(intersection.x.abs, intersection.y.abs)
        }
      }
      greatestVec
    }

    var intersections = world.tileMap.checkCollisions(
      position.xz,
      collisionRadius)
    var i = 0
    val maxChecks = 4
    while (!intersections.isEmpty && i < maxChecks) {
      val greatest = greatestIntersection(intersections)
      position.x -= greatest.x
      position.z -= greatest.y
      val physComp = entity.getComponent(PhysicsComponent.id)
      if (physComp.isDefined) {
        val velocity = physComp.get.velocity
        if (greatest.x != 0) {
          if (velocity.x * greatest.x > 0) {
            velocity.x = 0.0f
            velocity.z *= 0.8f
          }
        }
        if (greatest.y != 0) {
          if (velocity.z * greatest.y > 0) {
            velocity.z = 0.0f
            velocity.x *= 0.8f
          }
        }
      }
      intersections = world.tileMap.checkCollisions(
        position.xz,
        collisionRadius)
      i += 1
    }
  }
  
  

  def handleCollision(entity: Entity, other: Entity, intersection: Vec3) = {
    val collisionEntity = entity.getComponent(CollisionComponent.id).get
    val collisionOther = other.getComponent(CollisionComponent.id).get
    
    def sendIntersectionEvent(entityA: Entity, entityB: Entity) = {
      val event = new Event(
        Vector(entityA, entityB, intersection),
        EventType.E_COLLISION)
      EventManager.addEvent(event)
    }
    if (collisionEntity.isActive && collisionOther.isActive) {
      if (collisionEntity.collidesWith.contains(CollisionComponent.ALL) ||
          collisionEntity.collidesWith.contains(collisionOther.collisionType)) {
        val spatial = entity.getComponent(SpatialComponent.id).get;
        spatial.position.x += intersection.x
        spatial.position.z += intersection.z
        
        val physComp = entity.getComponent(PhysicsComponent.id)
        if (physComp.isDefined) {
          if (intersection.x != 0 || intersection.z != 0) {
            var velocity = physComp.get.velocity
            val reflected = velocity - (intersection.normalize() * 2) * velocity.dot(intersection.normalize())
            physComp.get.velocity = reflected
            physComp.get.velocity.x *= 0.25f
            physComp.get.velocity.z *= 0.25f
          }
        }
        sendIntersectionEvent(entity, other)
        sendIntersectionEvent(other, entity)
      }
    } else {
      if (collisionEntity.collidesWith.contains(CollisionComponent.ALL) ||
          collisionEntity.collidesWith.contains(collisionOther.collisionType)) {
        sendIntersectionEvent(entity, other)
        sendIntersectionEvent(other, entity)
      }
    }
  }
}