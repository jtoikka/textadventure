package o1.screen

import scala.swing.event.Key
import scala.collection.mutable.Map
import o1.adventure._
import o1.adventure.render.Renderer
import o1.adventure.render.ResourceManager
import scala.math._
import o1.math._
import o1.scene._
import o1.adventure.render2D._
import scala.swing.Font

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
  
	def update(delta: Double): Unit = {
    // code is only for testing
    
	  for(i <- 0 to 1){
	    
      var spatialComp = scene.entities(i).getComponent(SpatialComponent.id)
      var rendComp = scene.entities(i).getComponent(RenderComponent2D.id)
      var rect = ResourceManager.shapes(rendComp.get.shape).asInstanceOf[Rectangle2D]

      if(spatialComp.get.position.x <= 2)
        speeds(i)(0) = 2
      if(spatialComp.get.position.x + rect.w >= rend.w-3)
        speeds(i)(0) = -2
      if(spatialComp.get.position.y <= 2)
        speeds(i)(1) = 2
      if(spatialComp.get.position.y + rect.h >= rend.h-3)
        speeds(i)(1) = -2
        
      spatialComp.get.position.x += speeds(i)(0)
      spatialComp.get.position.y += speeds(i)(1)
	  }
	}
	
	def input(keyMap: Map[scala.swing.event.Key.Value, Int], delta: Double) = {
    if(keyMap(Key.M) == 2){
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
	  
	  var testRect = Factory2D.createRectangle(20, 10, false)
	  var testRectSpatial = testRect.getComponent(SpatialComponent.id)
	  testRectSpatial.get.position = Vec3(10.0f, 25.0f, 0.0f)
	  scene.addEntity(testRect)
	  
	  var testRect2 = Factory2D.createRectangle(30, 15, false)
	  var testRect2Spatial = testRect2.getComponent(SpatialComponent.id)
	  testRect2Spatial.get.position = Vec3(50.0f, 48.0f, 0.0f)
	  scene.addEntity(testRect2)
	  
	  var border = Factory2D.createRectangle(rend.w-3,rend.h-3,false)
	  var bSpatial = border.getComponent(SpatialComponent.id)
	  bSpatial.get.position = Vec3(1f, 1f, 0f)
	  scene.addEntity(border)
	}
	
	def resume(): Unit = {
	  println("TestScreen2D resumed")
	}
	
	def pause(){
	}
	
}