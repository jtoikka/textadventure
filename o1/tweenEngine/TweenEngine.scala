package o1.tweenEngine

import scala.collection.mutable.Buffer
import scala.collection.mutable.Map
import scala.reflect.api._
import scala.reflect.ClassTag

object TweenEngine {
  private val accessors = Map[ClassTag[_], TweenAccessor[_]]()
  //  private val timelines = Buffer[]
  private val tweens = Buffer[TweenObject[_]]()
  private val destroyTemp = Buffer[TweenObject[_]]()

  /*
   * Adds accessor for current type
   */
  def addAccessor[T](accessor: TweenAccessor[T])(implicit m: ClassTag[T]) = {
    accessors(m) = accessor
  }

  /*
   * Removes accessor for current type
   */
  def removeAccessor[T](implicit m: ClassTag[T]) = {
    accessors.remove(m)
  }

  /*
   * Gets accessor for current type
   */
  def getAccessor[T](implicit m: ClassTag[T]): TweenAccessor[T] = {
    accessors(m).asInstanceOf[TweenAccessor[T]]
  }

  /*
   * Adds tween
   */
  def addTween(t: TweenObject[_]) = {
    tweens += t
  }

  /*
   * Removes tween
   */
  def removeTween(t: TweenObject[_]) = {
    destroyTemp += t
  }

  /*
   * Updates all tweens
   */
  def update(delta: Int) = {
    if (!tweens.isEmpty) tweens.foreach(_.update(delta))
    destroyTemp.foreach((i: TweenObject[_]) => if(tweens.indexOf(i) >= 0)tweens.remove(tweens.indexOf(i)))
  }

}
