package o1.event

import o1.math._
import o1.scene._
import o1.event._

object RayTrace {
  
  def createBullet(position: Vec3): Entity = {
    var entity = new Entity()

    var spatialComp = new SpatialComponent()
    spatialComp.position = position
    entity.addComponent(spatialComp)

    var collisionComponent = new CollisionComponent(0.15f, CollisionComponent.CIRCLE)
    collisionComponent.isActive = true
    entity.addComponent(collisionComponent)

    entity
  }
  
  def trace(
      start: Vec3, direction: Vec3,
      scene: Scene, 
      distance: Float, maxSteps: Int, filter: (Entity) => Boolean): (Option[Entity], Float) = {
    val step = distance / maxSteps
    val bullet = createBullet(start)
    val spatial = bullet.getComponent(SpatialComponent.id).get
        
    var steps = 0
    while (steps < maxSteps) {
      if (CollisionCheck.findWorldCollision(bullet, 0.15f, scene.world.get)) {
        bullet.dispose()
        return (None, (start - spatial.position).length())
      }
      val entity = CollisionCheck.findEntityCollision(bullet, scene.entities.toVector, filter)
      if (entity.isDefined) {
        bullet.dispose()
        return (entity, (start - spatial.position).length())
      }
      spatial.position += direction * step
      steps += 1
    }
    bullet.dispose()
    (None, 0)
  }
}