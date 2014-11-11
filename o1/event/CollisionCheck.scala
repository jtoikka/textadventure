package o1.event

import o1.math.Vec2
import o1.scene.CollisionComponent
import o1.scene.Entity
import o1.inventory.Inventory
import o1.scene.SpatialComponent
import scala.Vector
import o1.scene.InventoryItemComponent
import o1.scene.World
import o1.math.Vec3
import o1.scene.PhysicsComponent

object CollisionCheck {

  def checkCollisions(entity: Entity, entities: Vector[Entity], world: Option[World]) = {
    val collisionComponent = entity.getComponent(CollisionComponent.id)
    val spatialComponent = entity.getComponent(SpatialComponent.id)
    if (collisionComponent.isDefined) {
      for (
        otherEntity <- entities.filter((other) => {
          other != entity && other.getComponent(CollisionComponent.id).isDefined
        })
      ) {
        val otherCollisionComponent =
          otherEntity.getComponent(CollisionComponent.id)

        val otherSpatialComponent =
          otherEntity.getComponent(SpatialComponent.id)

        val posEntity = spatialComponent.get.position.xz
        val posOther = otherSpatialComponent.get.position.xz

        val diff = posEntity - posOther

        val totalRadius =
          collisionComponent.get.radius + otherCollisionComponent.get.radius

        // Check if collision
        if (diff.length < totalRadius) {
          // Multiply the vector 'entity to other' by the percentage
          // intersection of the two collision shapes.
          val intersection =
            diff * ((totalRadius - diff.length) / diff.length)

          def sendIntersectionEvent(entityA: Entity, entityB: Entity) = {
            val event = new Event(
              Vector(entityA, entityB, intersection),
              EventType.E_COLLISION)
            EventManager.addEvent(event)
          }

          sendIntersectionEvent(entity, otherEntity)
          sendIntersectionEvent(otherEntity, entity)

          handleCollision(entity, otherEntity, intersection)
        }
      }
      checkWorldCollisions(entity, collisionComponent.get.radius, world.get)
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
          velocity.x = -velocity.x
        }
        if (greatest.y != 0) {
          velocity.z = -velocity.z
        }
      }
      intersections = world.tileMap.checkCollisions(
        position.xz,
        collisionRadius)
      i += 1
    }
  }

  def handleCollision(entity: Entity, other: Entity, intersection: Vec2) = {
    // This function is incomplete! 
    if (entity.getComponent(CollisionComponent.id).get.isActive &&
      other.getComponent(CollisionComponent.id).get.isActive) {
      val spatial = entity.getComponent(SpatialComponent.id).get;
      spatial.position.x += intersection.x
      spatial.position.z += intersection.y
    }

    val invComp = other.getComponent(InventoryItemComponent.id)
    if (invComp.isDefined) {
      if (!other.destroy) {
        val picked = Inventory.addItem(invComp.get.invItem)
        if (picked) {
          other.destroy = true
        }
      }
    }
  }
}