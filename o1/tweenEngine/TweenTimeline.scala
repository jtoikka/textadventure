package o1.tweenEngine

import scala.collection.mutable.Buffer
import scala.reflect.ClassTag

class TweenTimeline[T](implicit m: ClassTag[_]) {
  private val tweens = Buffer[T]()
}