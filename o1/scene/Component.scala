package o1.scene

import o1.math._
import scala.collection.mutable.Buffer

/**
 * Components make up the data of an entity.
 */

abstract class Component {
}

/**
 * Contains the spatial information of an entity. Specifically its [position] in
 * the scene, the direction [forward] it is pointing in, and the [up] direction
 * for its orientation (by default 'up' is equal to the world's y-axis).
 */
class SpatialComponent extends Component {
  var position = Vec3(0.0f, 0.0f, 0.0f)
  var forward = Vec3(0.0f, 0.0f, 1.0f) // The direction the entity is facing
  var up = Vec3(0.0f, 1.0f, 0.0f) // The up direction for the entity
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

class RenderComponent(val mesh: String) extends Component {
}

object RenderComponent2D {
  val id = classOf[RenderComponent2D]
}

class RenderComponent2D(val shape: String) extends Component {
  
}

object FollowCameraComponent {
  val id = classOf[FollowCameraComponent]
}

class FollowCameraComponent extends Component {
  
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
