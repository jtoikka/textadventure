package o1.scene

import o1.math._

import scala.collection.mutable.Buffer
import o1.inventory.Item
import o1.event._

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
    var up: Vec3 = Vec3(0.0f, 1.0f, 0.0f)) extends Component {
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
    val texture: Option[String] = None) extends Component {
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
}

class CollisionComponent(
    val radius: Float, val shape: Int,
    val attributes: Buffer[Int]) extends Component {
  var isActive = true
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
}

case class AIComponent(val botType: String) extends Component {
  
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

case class HealthComponent(var hp: Int) extends Component {
  
}