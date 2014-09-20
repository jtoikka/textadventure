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
  
  def this(parent: Adventure, x: Int, y: Int) = this(parent, new Renderer(x,y))
  
  var scene = new Scene()
  
//  var meshes = Array[Object](new Object(Mesh("data/sphere.obj"), 
//                                        new Vec4(-1.1f,0.0f,-3.0f,0.0f)),
//                             new Object(Mesh("data/monkey.obj"), 
//                                        new Vec4(1.1f,0.0f,-3.0f,0.0f)))
  /**
	* Update method. Used to update game's state
	*/
                                        
	def update(delta: Double, keyMap: Map[scala.swing.event.Key.Value, Boolean]): Unit = {
    var camSpatial = scene.camera.getComponent(SpatialComponent.id).get
    var camRight = camSpatial.up.cross(camSpatial.forward)
	  if (keyMap(Key.M)) {
	    parent.changeScreen(parent.menuScreen)
	  }
	  if (keyMap(Key.W)) {
	    camSpatial.position += camSpatial.forward * 0.05f
	  }
	  if (keyMap(Key.S)) {
	    camSpatial.position -= camSpatial.forward * 0.05f
	  }
	  if (keyMap(Key.A)) {
	    camSpatial.position += camRight * 0.05f
	  }
	  if (keyMap(Key.D)) {
	    camSpatial.position -= camRight * 0.05f
	  }
	  if (keyMap(Key.Left)) {
	    camSpatial.forward = 
	      (Utility.rotateY(0.1f) * Vec4(camSpatial.forward, 0.0f)).xyz
	  }
	  if (keyMap(Key.Right)) {
	    camSpatial.forward = 
	      (Utility.rotateY(-0.1f) * Vec4(camSpatial.forward, 0.0f)).xyz
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
	  
	  scene.camera.getComponent(SpatialComponent.id).get.position = Vec3(2.0f, -0.6f, -5.0f)
	  
//	  var sphere = Factory.createSphere()
//	  var spatialComp = sphere.getComponent(SpatialComponent.id)
//	  spatialComp.get.position = Vec3(-1.1f, 0.0f, -3.0f)
//	  scene.addEntity(sphere)
	  
//	  var cube = Factory.createCube()
//	  var cubeSpatialComp = cube.getComponent(SpatialComponent.id)
//	  cubeSpatialComp.get.position = Vec3(-1.1f, 0.0f, -3.0f)
//	  scene.addEntity(cube)

	  for (x <- 0 to 3) {
	    for (y <- 0 to 3) {
	  	  var monkey = Factory.createMonkey()
	      var monkeySpatial = monkey.getComponent(SpatialComponent.id)
	      monkeySpatial.get.position = Vec3((x - 1) * 3, 0.0f, (y - 1) * 3)
	      scene.addEntity(monkey)
	    }
	  }
	}
}