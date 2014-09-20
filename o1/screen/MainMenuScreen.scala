package o1.screen

import scala.swing.event.Key
import scala.collection.mutable.Map
import o1.adventure._
import o1.adventure.render._
import scala.math._ 
import o1.math._
import o1.scene._

/**
 *  MainMenuScreen class.
 *  MainMenuScreen can have its own renderer or use common renderer.
 * @param parent parent Adventure class
 **/
class MainMenuScreen(parent: Adventure, rend: Renderer) 
      extends Screen(parent, rend) {
  
  def this(parent: Adventure, x: Int, y: Int) = this(parent, new Renderer(x,y))
  
  parent.title = "A 3D Adventure - Menu"
  
  var scene = new Scene()
  
//  var meshes = Array[Object](new Object(Mesh("data/monkey.obj"), 
//                                        new Vec4(0.0f,0.0f,-3.0f,0.0f)))
  
	/**
	 * Update method. Used to update screen state.
	 */
	def update(delta: Double, keyMap: Map[scala.swing.event.Key.Value, Boolean]): Unit = {
	  if(keyMap(Key.N)){
	    parent.changeScreen(parent.gameScreen)
	  }
	}
	
	/**
	 * Draw method. Is used to draw screen to display
	 */
	def draw(): Unit = {
	  rend.clear()
	  rend.renderScene(scene)
	  display = rend.display
	}
	
	def init(): Unit = {
	  parent.title = "A 3D Adventure - Menu"
	  
	  var monkey = Factory.createMonkey()
	  var monkeySpatial = monkey.getComponent(SpatialComponent.id)
	  monkeySpatial.get.position = Vec3(0.0f, 0.0f, -3.0f)
	  scene.addEntity(monkey)
	}
	init
}