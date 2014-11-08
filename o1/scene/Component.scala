package o1.scene

import o1.math._
import scala.collection.mutable.Buffer
import o1.inventory.Item

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

class CollisionMapComponent(val tileMap: Array[Int]) extends Component {}

object CollisionComponent {
  val id = classOf[CollisionComponent]
}

class CollisionComponent(
    val radius: Float, 
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