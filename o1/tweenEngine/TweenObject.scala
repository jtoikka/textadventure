package o1.tweenEngine

import scala.collection.mutable.Buffer
import scala.reflect.ClassTag

class TweenObject[T](val obj: T,
                     val tweenType: Int,
                     val target: Vector[Float],
                     val duration: Int)(implicit m: ClassTag[T]) {
                     
  private val valueCount = target.length
  private val startValues = accessor.getValues(obj, tweenType)
  private var currentValues = startValues
  private var lastTimeError = Vector.tabulate(valueCount)(_ => 0.0f)
  private var currentTime = 0
  private val steps = getSteps()
  
  val selfDestruct = true
  var ready = false

  def update(delta: Int) = {
    if (!ready) {
      val time = delta.min(duration - currentTime)

      val oldValues = accessor.getValues(obj, tweenType)

      val calcNew = (i: Int) => oldValues(i) + (steps(i) * time) - lastTimeError(i)
      val newValues = Vector.tabulate(valueCount)(calcNew)
      
      accessor.setValues(obj, tweenType, newValues)
      val newFromObject = accessor.getValues(obj, tweenType)
      
      lastTimeError = Vector.tabulate(valueCount)((i: Int) => newFromObject(i) - newValues(i))
      
      currentTime += delta

      if (currentTime >= duration){
        ready = true
        if(selfDestruct) TweenEngine.removeTween(this)
      }
    }
  }

  private def accessor: TweenAccessor[T] = TweenEngine.getAccessor(m)

  private def getSteps() = {
    val arr = Buffer[Float]()
    for (i <- target.indices) {
      arr += (target(i) - startValues(i)) / duration
    }
    arr.toVector
  }
}