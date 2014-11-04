package o1.tweenEngine.accessors

import o1.tweenEngine.TestClass
import o1.tweenEngine.TweenAccessor
import scala.Vector

object TestClassTween{
  val XYZ = 1
}

class TestClassTween extends TweenAccessor[TestClass] {

  override def getValues(item: TestClass, tweenType: Int): Vector[Double] = {
    tweenType match {
      case TestClassTween.XYZ => Vector[Double](item.x, item.y, item.z)
    }
  }
  
  override def setValues(item: TestClass, tweenType: Int, values: Vector[Double]) = {
    tweenType match {
      case TestClassTween.XYZ => {
        item.x = values(0).round.toInt
        item.y = values(1).round.toInt
        item.z = values(2).round.toInt
      }
    }
  }
}