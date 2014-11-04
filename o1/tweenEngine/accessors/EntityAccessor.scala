package o1.tweenEngine.accessors

import o1.tweenEngine.TweenAccessor
import o1.adventure.render.Mesh
import o1.scene.SpatialComponent
import o1.scene.Entity

object EntityAccessor {
  val ROTATION = 1
}
class EntityAccessor extends TweenAccessor[Entity] {

  override def getValues(item: Entity, tweenType: Int): Vector[Float] = {
    tweenType match {
      case EntityAccessor.ROTATION => {
        val spat = item.getComponent(SpatialComponent.id).get
        val rotation = spat.forward
      }
    }
    Vector[Float]()
  }

  override def setValues(item: Entity, tweenType: Int, values: Vector[Float]) = {
    tweenType match {
      case TestClassTween.XYZ => {

      }
    }
  }

}