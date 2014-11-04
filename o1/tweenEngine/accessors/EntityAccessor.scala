package o1.tweenEngine.accessors

import o1.tweenEngine.TweenAccessor
import o1.adventure.render.Mesh
import o1.scene.SpatialComponent
import o1.scene.Entity

object EntityAccessor {
  val ROTATION = 1
  val X = 2
}
class EntityAccessor extends TweenAccessor[Entity] {

  override def getValues(item: Entity, tweenType: Int): Vector[Double] = {
    tweenType match {
      case EntityAccessor.X => {
        val spatial = item.getComponent(SpatialComponent.id)
        Vector[Double](spatial.get.position.x)
      }
    }
  }

  override def setValues(item: Entity, tweenType: Int, values: Vector[Double]) = {
    tweenType match {
      case EntityAccessor.X => {
        val spatial = item.getComponent(SpatialComponent.id)
        spatial.get.position.x = values(0).toFloat
      }
    }
  }

}