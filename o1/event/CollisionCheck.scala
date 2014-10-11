package o1.event

import o1.math.Vec2
import o1.scene.CollisionComponent
import o1.scene.Entity
import o1.scene.SpatialComponent
import scala.Vector

object CollisionCheck {

  def checkCollisions(entity: Entity, entities: Vector[Entity]) = {
    val collisionComponent = entity.getComponent(CollisionComponent.id)
    val spatialComponent = entity.getComponent(SpatialComponent.id)
    if (collisionComponent.isDefined) {
      for (otherEntity <- entities) {
        if (otherEntity != entity) {
          val otherCollisionComponent =
            otherEntity.getComponent(CollisionComponent.id)
          if (otherCollisionComponent.isDefined) {
            val otherSpatialComponent =
              otherEntity.getComponent(SpatialComponent.id)
            val posEntity = spatialComponent.get.position.xz
            val posOther = otherSpatialComponent.get.position.xz
            val diff = posEntity - posOther
  
            val entityRadius = collisionComponent.get.radius
            val otherRadius = otherCollisionComponent.get.radius
            val totalRadius = entityRadius + otherRadius
  
            // Check if collision
            if (diff.length < totalRadius) {
              val intersection = diff * ((totalRadius - diff.length) / diff.length)
              val event1 = new Event(
                Vector(entity, otherEntity, intersection),
                EventType.E_COLLISION)
              val event2 = new Event(
                Vector(otherEntity, entity, intersection),
                EventType.E_COLLISION)
              EventManager.addEvent(event1)
              EventManager.addEvent(event2)
              handleCollision(entity, otherEntity, intersection)
            }
          }
        }
      }
    }
  }

  def handleCollision(entity: Entity, other: Entity, intersection: Vec2) = {
    // This function is incomplete! 
    val spatial = entity.getComponent(SpatialComponent.id).get;
    spatial.position.x += intersection.x
    spatial.position.z += intersection.y
  }
}