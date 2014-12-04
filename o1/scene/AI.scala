package o1.scene

import o1.math.Vec3
import o1.event.RayTrace
import o1.math.Vec2
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

object AI {
  val functionMap = Map[String, (Entity, Double, Scene) => Unit](
      "lovebot" -> updateLovebot,
      "nosebot" -> updateNosebot,
      "psychobot" -> updatePsychobot,
      "ghost" -> updateGhost)
  
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
      if (entityInFront.getComponent(PlayerComponent.id).isDefined) {
        spatial.position += Vec3(forward.x, 0.0f, forward.z) * 0.3f * delta.toFloat
      }
    }
  }
  
  val pacmanStage = Vector[Int](
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
      1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
      1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1,
      1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1,
      1, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 1,
      1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1,
      1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1,
      1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1,
      1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1,
      1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1,
      1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1,
      1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1,
      1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1,
      1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1,
      1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1,
      1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1,
      1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 0, 1,
      1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
      1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1,
      1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1,
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1
      )
      
  private def getIndex(coord: Vec2): Int = {
    val relative = coord - origin
    if (relative.x < 0 || relative.y < 0 || relative.y >= 21 || relative.x >= 19)
      0
    else {
      relative.x.round + relative.y.round * 19
    }
  }
  
  private def checkOpportunities(coord: Vec2): Vector[Int] = {
    val north = Vec2(0, -1)
    val east = Vec2(1, 0)
    val south = Vec2(0, 1)
    val west = Vec2(-1, 0)
    
    val buffer = ArrayBuffer[Int]()
    
    val northIndex = getIndex(coord + north)
    val eastIndex = getIndex(coord + east)
    val southIndex = getIndex(coord + south)
    val westIndex = getIndex(coord + west)
    
    if (pacmanStage(northIndex) == 0) buffer += AIComponent.NORTH
    if (pacmanStage(eastIndex) == 0) buffer += AIComponent.EAST
    if (pacmanStage(southIndex) == 0) buffer += AIComponent.SOUTH
    if (pacmanStage(westIndex) == 0) buffer += AIComponent.WEST
    
    buffer.toVector
  }
  
  def opposite(direction: Int) = {
    direction match {
      case AIComponent.NORTH => AIComponent.SOUTH
      case AIComponent.EAST => AIComponent.WEST
      case AIComponent.SOUTH => AIComponent.NORTH
      case AIComponent.WEST => AIComponent.EAST
    }
  }
  
  val origin = Vec2(4, 0)
  
  val rng = new Random()
    
  def updateGhost(ghost: Entity, delta: Double, scene: Scene) = {
    val spatial = ghost.getComponent(SpatialComponent.id).get
    val aiComp = ghost.getComponent(AIComponent.id).get
    val coord = Vec2(spatial.position.x.round / 2, spatial.position.z.round / 2)
    val opportunities = checkOpportunities(coord)
    val posXDec = spatial.position.x / 2.0f - (spatial.position.x / 2).toInt
    val posYDec = spatial.position.z / 2.0f - (spatial.position.z / 2).toInt
    
    if (aiComp.distance <= 0) {
      if ((posXDec < 0.1 || posXDec > 0.9) && (posYDec < 0.1 || posYDec > 0.9)) {
        if (opportunities.size > 2) {
          var newDirection = opportunities(rng.nextInt(opportunities.size))
          while (newDirection == opposite(aiComp.direction)) {
            newDirection = opportunities(rng.nextInt(opportunities.size))
          }
          aiComp.direction = newDirection
          aiComp.distance = 1.0
        } else if (opportunities.contains(aiComp.direction)) {
          aiComp.distance = 1.0
        } else if (opportunities.size > 1) {
          aiComp.direction = opportunities.find(opposite(_) != aiComp.direction).get
          aiComp.distance = 1.0
        } else if (opportunities.size != 0) {
          aiComp.direction = opportunities(0)
          aiComp.distance = 1.0
        } else {
          // println("Hey, I'm totally in the wrong place! " + spatial.position)
        }
      }
    }
//    if (opportunities.contains(aiComp.direction) && posXDec > 0.4 && posXDec < 0.6) {
//      
//    } else {
//      aiComp.direction = opportunities(rng.nextInt(opportunities.size))
//    }
    if (aiComp.direction == AIComponent.NORTH) {
      spatial.position += Vec3(0.0f, 0.0f, -0.22f) * delta.toFloat
      spatial.position.x = spatial.position.x.round
    } else if (aiComp.direction == AIComponent.EAST) {
      spatial.position += Vec3(0.22f, 0.0f, 0.0f) * delta.toFloat
      spatial.position.z = spatial.position.z.round
    } else if (aiComp.direction == AIComponent.SOUTH) {
      spatial.position += Vec3(0.0f, 0.0f, 0.22f) * delta.toFloat
      spatial.position.x = spatial.position.x.round
    } else if (aiComp.direction == AIComponent.WEST) {
      spatial.position += Vec3(-0.22f, 0.0f, 0.0f) * delta.toFloat
      spatial.position.z = spatial.position.z.round
    }
    aiComp.distance -= 0.22f * (delta.toFloat)
  }
}
