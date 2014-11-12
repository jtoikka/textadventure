package o1.scene

object Physics {
  def applyPhysics(entity: Entity, delta: Double) = {
    val spatial = entity.getComponent(SpatialComponent.id).get
    val physics = entity.getComponent(PhysicsComponent.id).get
    
    spatial.position += physics.velocity * delta.toFloat + physics.acceleration * 0.5f * delta.toFloat * delta.toFloat
    physics.velocity += physics.acceleration * delta.toFloat
  }
}