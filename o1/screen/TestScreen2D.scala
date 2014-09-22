package o1.screen

import scala.swing.event.Key
import scala.collection.mutable.Map
import o1.adventure._
import o1.adventure.render.Renderer
import scala.math._
import o1.math._
import o1.scene._
import o1.adventure.render2D._

class TestScreen2D(parent: Adventure, rend: Renderer) 
      extends Screen(parent, rend) {
  def this(parent: Adventure, x: Int, y: Int) = this(parent, new Renderer2D(x,y))
  
  /**
  * Init works as constructor method
  */
  
  var scene = new Scene() 
  init()
  
  /**
	* Update method. Used to update game's state
	*/
  
  var speeds = Array.ofDim[Int](2, 2)
  for(x <- 0 until 2){
    for(y <- 0 until 2){
      speeds(x)(y) = 2
    }
  }
  
	def update(delta: Double, keyMap: Map[scala.swing.event.Key.Value, Boolean]): Unit = {
    // code is only for testing
    
	  for(i <- 0 to 1){
	    
      var spatialComp = scene.entities(i).getComponent(SpatialComponent.id)
      var rendComp = scene.entities(i).getComponent(RenderComponent2D.id)
      var rect = ResourceManager2D.shapes(rendComp.get.shape).asInstanceOf[Rectangle2D]
      
      spatialComp.get.position.x += speeds(i)(0)
      spatialComp.get.position.y += speeds(i)(1)
      
      if(spatialComp.get.position.x <= 0)
        speeds(i)(0) = 2
      if(spatialComp.get.position.x + rect.w >= rend.w)
        speeds(i)(0) = -2
      if(spatialComp.get.position.y <= 0)
        speeds(i)(1) = 2
      if(spatialComp.get.position.y + rect.h >= rend.h)
        speeds(i)(1) = -2
        
	  }
	  if(keyMap(Key.M)){
	    parent.changeScreen(parent.gameScreen)
	  }
	}
	
	/**
	 * Draw method. Is used to draw screen to display etc
	 */
	def draw(): Unit = {
	  rend.clear()
//	  println("rendering scene: " + scene.entities.toString())
	  rend.renderScene(scene)
	  display = rend.display
	}
	
	/**
	 * Initializing method. Should be used when screen is made
	 */
	def init(): Unit = {
	  var testRect2 = Factory2D.createTestRect2()
	  var testRect2Spatial = testRect2.getComponent(SpatialComponent.id)
	  testRect2Spatial.get.position = Vec3(100.0f, 43.0f, 0.0f)
	  scene.addEntity(testRect2)
	  
	  var testRect = Factory2D.createTestRect()
	  var testRectSpatial = testRect.getComponent(SpatialComponent.id)
	  testRectSpatial.get.position = Vec3(10.0f, 25.0f, 0.0f)
	  scene.addEntity(testRect)
	  
	  var testTri = Factory2D.createTestTri()
	  var testTriSpatial = testRect.getComponent(SpatialComponent.id)
	  testTriSpatial.get.position = Vec3(20.0f, 50.0f, 0.0f)
	  scene.addEntity(testTri)
	}
	
	def resume(): Unit = {
	  println("TestScreen2D resumed")
	}
	
}