package o1.scene

import o1.math.Vec3

object AI {
  val functionMap = Map[String, (Entity, Double) => Unit](
      "lovebot" -> updateLovebot,
      "nosebot" -> updateNosebot)
  
  val loveBotSpeed = 0.25f
      
  def updateLovebot(bot: Entity, delta: Double) = {
    val spatial = bot.getComponent(SpatialComponent.id).get
    spatial.position += Vec3(-spatial.forward.x, 0.0f, spatial.forward.z) * loveBotSpeed * delta.toFloat
  }
  
  def updateNosebot(bot: Entity, delta: Double) = {
    
  }
}
