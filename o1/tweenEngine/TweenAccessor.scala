package o1.tweenEngine

abstract class TweenAccessor[T] {

  def getValues(item: T, tweenType: Int): Vector[Float]
  def setValues(item: T, tweenType: Int, values: Vector[Float])
  
}