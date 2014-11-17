package o1.scene

import o1.math.Vec3
import o1.event.RayTrace

object AI {
  val functionMap = Map[String, (Entity, Double, Scene) => Unit](
      "lovebot" -> updateLovebot,
      "nosebot" -> updateNosebot,
      "psychobot" -> updatePsychobot)
  
  val loveBotSpeed = 0.25f
      
  def updateLovebot(bot: Entity, delta: Double, scene: Scene) = {
    val spatial = bot.getComponent(SpatialComponent.id).get
    spatial.position += Vec3(-spatial.forward.x, 0.0f, spatial.forward.z) * loveBotSpeed * delta.toFloat
  }
  
  def updateNosebot(bot: Entity, delta: Double, scene: Scene) = {
    
  }
  
  def updatePsychobot(bot: Entity, delta: Double, scene: Scene) = {
    val spatial = bot.getComponent(SpatialComponent.id).get
    val forward = Vec3(-spatial.forward.x, spatial.forward.y, spatial.forward.z)
    val trace = RayTrace.trace(spatial.position, forward, scene, 20.0f, 100, (entity) => entity.getComponent(PlayerComponent.id).isDefined)
    if (trace._1.isDefined) {
      val entityInFront = trace._1.get
//      println("PSYCHO: " + entityInFront)
      if (entityInFront.getComponent(PlayerComponent.id).isDefined) {
        spatial.position += Vec3(forward.x, 0.0f, forward.z) * 0.3f * delta.toFloat
//        println(spatial.position)
      }
    }
  }
}
