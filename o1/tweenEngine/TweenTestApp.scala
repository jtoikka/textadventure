package o1.tweenEngine

import scala.reflect._
import o1.tweenEngine.accessors._

object TweenTestApp extends App {
  val test = new TestClass(0, 0, 0)
  val test2 = new TestClass(100, 100, 100)
  TweenEngine.addAccessor[TestClass](new TestClassTween())
  
  TweenEngine.addTween(new TweenObject(test, TestClassTween.XYZ, Vector(1000, 20000, 40000), 20))
  TweenEngine.addTween(new TweenObject(test2,TestClassTween.XYZ, Vector(3975, 5682, 4432), 17))
  
  println("Setup: " + test.toString())
  var i = 0
  while(i < 25){
    TweenEngine.update(1)
    println(test.toString() + " , " + test2.toString())
    i += 1
  }
  

}