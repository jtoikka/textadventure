package o1.scene

import o1.math._
import o1.inventory.Item
import o1.event._
import o1.inventory.Inventory
import scala.collection.mutable.ArrayBuffer

/**
 * Components make up the data of an entity.
 */

abstract class Component {}

/**
 * Contains the spatial information of an entity. Specifically its [position] in
 * the scene, the direction [forward] it is pointing in, and the [up] direction
 * for its orientation (by default 'up' is equal to the world's y-axis).
 */
case class SpatialComponent(
    var position: Vec3 = Vec3(0.0f, 0.0f, 0.0f),
    var forward: Vec3 = Vec3(0.0f, 0.0f, 1.0f), // The direction the entity is facing
    var up: Vec3 = Vec3(0.0f, 1.0f, 0.0f),
    var scale: Vec3 = Vec3(1.0f, 1.0f, 1.0f)) extends Component {
}
object SpatialComponent {
  val id = classOf[SpatialComponent]
}

/**
 * Contains an identifier to a mesh to use for rendering the entity.
 */
object RenderComponent {
  val id = classOf[RenderComponent]
}

case class RenderComponent(
    val mesh: String, 
    var texture: Option[String] = None) extends Component {
}

object RenderComponent2D {
  val id = classOf[RenderComponent2D]
}

case class RenderComponent2D(val shape: String) extends Component {
  var isActive = true
}

object FollowComponent {
  val id = classOf[FollowComponent]
}

case class FollowComponent(val entity: Entity) extends Component {
  
}

object FaceCameraComponent {
  val id = classOf[FaceCameraComponent]
}
case class FaceCameraComponent(var on: Boolean = true) extends Component {
  
}

class CollisionMapComponent(val tileMap: Array[Int]) extends Component {}

object CollisionComponent {
  val id = classOf[CollisionComponent]
  
  val CIRCLE = 1
  val SQUARE = 2
  
  val ALL = 3
  val DEFAULT = 4
  val COFFEE = 5
  val PLAYER = 6
  val ENEMY = 7
  val GHOST = 8
}

class CollisionComponent(
    val radius: Float, val shape: Int, 
    val halfWidth: Float = 0.0f, val halfHeight: Float = 0.0f, 
    val collisionType: Int = CollisionComponent.DEFAULT) extends Component {
  var isActive = true
  var isStatic = false
  val collidesWith = ArrayBuffer[Int](CollisionComponent.ALL)
}

object InputComponent {
  val id = classOf[InputComponent]
}

class InputComponent extends Component {
  
}

object InventoryItemComponent {
  val id = classOf[InventoryItemComponent]
}

case class InventoryItemComponent(
    val invItem: Item,
    val count: Int = 1) extends Component {

}

object AIComponent {
  val id = classOf[AIComponent]

  val NORTH = 0
  val EAST = 1
  val SOUTH = 2
  val WEST = 3
}

case class AIComponent(val botType: String) extends Component {
  var direction = AIComponent.NORTH
  var distance = 0.0
}

object PhysicsComponent {
  val id = classOf[PhysicsComponent]
}

case class PhysicsComponent(var velocity: Vec3, var acceleration: Vec3) extends Component {
  
}

object DamageComponent {
  val id = classOf[DamageComponent]
  
  val NONE = 0
  val PLAYER = 1
  val ENEMY = 2
  val GHOST = 3
}

case class DamageComponent(val amount: Int, val canDamage: Int) extends Component {
}

object BreakableComponent {
  val id = classOf[BreakableComponent]
}

case class BreakableComponent() extends Component {
  
}

object TakeDamageComponent {
  val id = classOf[TakeDamageComponent]
}

case class TakeDamageComponent(val invinsibility: Float) {
  var timer = 0.0f
}

object HealthComponent {
  val id = classOf[HealthComponent]
}

case class HealthComponent(var hp: Int, val maxHP: Int = 5) extends Component {
  var invulnerabilityTimer = 0.0
}

object RotateComponent {
  val id = classOf[RotateComponent]
}

case class RotateComponent(val rateForward: Float = 0.0f, val rateUp: Float = 0.0f) extends Component {
  
}

object InventoryComponent {
  val id = classOf[InventoryComponent]
}

case class InventoryComponent() extends Component {
  val inv = new Inventory()
}

object PlayerComponent {
  val id = classOf[PlayerComponent]
}

case class PlayerComponent() extends Component {
  
}

object LootComponent {
  val id = classOf[LootComponent]
}

case class LootComponent(loot: Entity) extends Component {}

object AnimationComponent {
  val id = classOf[AnimationComponent]
}

case class AnimationComponent(val frames: Vector[String], val step: Double = 1.0) extends Component {
  var timer = 0.0
  def frame = frames((timer / step).toInt % frames.size)
}

object DeathTimerComponent {
  val id = classOf[DeathTimerComponent]
}

case class DeathTimerComponent(var timer: Double) extends Component {
  
}

object SpawnComponent {
  val id = classOf[SpawnComponent]
}

case class SpawnComponent(var entity: Option[Entity], val step: Double) extends Component {
  var timer = step + 1
}