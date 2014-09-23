package o1.screen

import scala.swing.event.Key
import scala.collection.mutable.Map
import o1.adventure._
import o1.adventure.render._
import scala.math._
import o1.math._
import o1.scene._


/**
 * GameScreen class.
 * GameScreen can have its own renderer or use common renderer.
 * Game logic would go in this class.
 * @param parent parent Adventure class
 **/
class GameScreen(parent: Adventure, rend: Renderer) 
      extends Screen(parent, rend) {
  
  def this(parent: Adventure, x: Int, y: Int) = this(parent, new Renderer3D(x,y))
  
  var scene = new Scene()
  init()

  /**
	* Update method. Used to update game's state
	*/
                                        
	def update(delta: Double): Unit = {
    val camSpatial = scene.camera.getComponent(SpatialComponent.id).get
    for (entity <- scene.entities) {
      if ((entity.getComponent(FollowCameraComponent.id)).isDefined) {
        var spatial = entity.getComponent(SpatialComponent.id).get
        spatial.position = Vec3(-camSpatial.position.x, 0.2f, -camSpatial.position.z)
        spatial.forward = Vec3(-camSpatial.forward.x, camSpatial.forward.y, camSpatial.forward.z)
      }
    }
	}
  
  def input(keyMap: Map[scala.swing.event.Key.Value, Int], delta: Double) = {
    var deltaFloat = delta.toFloat
    var camSpatial = scene.camera.getComponent(SpatialComponent.id).get
    var camRight = camSpatial.up.cross(camSpatial.forward)
	  if (keyMap(Key.M) == 2) {
	    parent.changeScreen(parent.menuScreen)
	  }
    if (keyMap(Key.N) == 2) {
	    parent.changeScreen(parent.testScreen2D)
	  }
	  if (keyMap(Key.W) == 1 || keyMap(Key.W) == 2) {
	    camSpatial.position += camSpatial.forward * 0.15f * deltaFloat
	  }
	  if (keyMap(Key.S) == 1 || keyMap(Key.S) == 2) {
	    camSpatial.position -= camSpatial.forward * 0.15f * deltaFloat
	  }
	  if (keyMap(Key.A) == 1 || keyMap(Key.A) == 2) {
	    camSpatial.position += camRight * 0.15f * deltaFloat
	  }
	  if (keyMap(Key.D) == 1 || keyMap(Key.D) == 2) {
	    camSpatial.position -= camRight * 0.15f * deltaFloat
	  }
	  if (keyMap(Key.Left) == 1 || keyMap(Key.Left) == 2) {
	    camSpatial.forward = 
	      (Utility.rotateY(0.2f * deltaFloat) * Vec4(camSpatial.forward, 0.0f)).xyz
	  }
	  if (keyMap(Key.Right) == 1 || keyMap(Key.Right) == 2) {
	    camSpatial.forward = 
	      (Utility.rotateY(-0.2f * deltaFloat) * Vec4(camSpatial.forward, 0.0f)).xyz
	  }
  }
	
	/**
	 * Draw method. Is used to draw screen to display etc
	 */
	def draw(): Unit = {
	  rend.clear()
	  rend.renderScene(scene)
	  display = rend.display
	}
	
	def init(): Unit = {
	  parent.title = "A 3D Adventure - Game"
	  
	  scene.camera.getComponent(SpatialComponent.id).get.position = Vec3(0.0f, -1.2f, -0.0f)
	  
//	  var sphere = Factory.createSphere()
//	  var spatialComp = sphere.getComponent(SpatialComponent.id)
//	  spatialComp.get.position = Vec3(-1.1f, 0.0f, -3.0f)
//	  scene.addEntity(sphere)
	  
//	  var cube = Factory.createCube()
//	  var cubeSpatialComp = cube.getComponent(SpatialComponent.id)
//	  cubeSpatialComp.get.position = Vec3(-1.1f, 0.0f, -3.0f)
//	  scene.addEntity(cube)

  	  var monkey = Factory.createMonkey()
      var monkeySpatial = monkey.getComponent(SpatialComponent.id)
      monkeySpatial.get.position = Vec3(0.0f, 1.0f, 0.5f)
      scene.addEntity(monkey)
      
//	  for (x <- 0 to 3) {
//	  	  var monkey = Factory.createPlate()
//	      var monkeySpatial = monkey.getComponent(SpatialComponent.id)
//	      monkeySpatial.get.position = Vec3(4f*x,0f,0f)
//	      scene.addEntity(monkey)
//	  }
	  var floor = Factory.createFloor()
	  var floorSpatial = floor.getComponent(SpatialComponent.id)
	  val floorFollowCam = new FollowCameraComponent()
	  floor.addComponent(floorFollowCam)
	  floorSpatial.get.position = Vec3(0.0f, 0.05f, 0.0f)
	  scene.addEntity(floor)
	  
	  var level = Factory.createLevel()
	  scene.addEntity(level)
	}
	
	def resume(){
	  
	}
	
	def pause(){
	  
	}
}