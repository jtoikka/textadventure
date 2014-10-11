package o1.screen

import scala.swing.event.Key
import scala.collection.mutable.Map
import o1.adventure._
import o1.adventure.render._
import scala.math._
import o1.math._
import o1.scene._
import o1.adventure.render2D._


/**
 * GameScreen class.
 * GameScreen can have its own renderer or use common renderer.
 * Game logic would go in this class.
 * @param parent parent Adventure class
 **/
class GameScreen(parent: Adventure, rend: Renderer) 
      extends Screen(parent, rend) {
  
  def this(parent: Adventure, x: Int, y: Int) = this(parent, new Renderer3D(x,y))

  /* HUD -------------------------------------*/
  var rendHUD = new Renderer2D(rend.w,rend.h)
  var hudTextRect: Option[TextRect2D] = None
  var lastDrawTime: Long = 0
  var sceneHUD = new Scene()
  var showHUD = true;
  /* -----------------------------------------*/
  
  var scene = new Scene()
  init()

  /**
	* Update method. Used to update game's state
	*/
                                        
	def update(delta: Double): Unit = {
    val camSpatial = scene.camera.getComponent(SpatialComponent.id).get
    val entitiesAsVector = scene.entities.toVector
    for (entity <- scene.entities) {
      if ((entity.getComponent(FollowCameraComponent.id)).isDefined) {
        var spatial = entity.getComponent(SpatialComponent.id).get
        spatial.position = Vec3(-camSpatial.position.x, 0.2f, -camSpatial.position.z)
        spatial.forward = Vec3(-camSpatial.forward.x, camSpatial.forward.y, camSpatial.forward.z)
      }
      CollisionCheck.checkCollisions(entity, entitiesAsVector)
    }
	}
  
  def input(keyMap: Map[scala.swing.event.Key.Value, Int], delta: Double) = {
    var deltaFloat = delta.toFloat
    var camSpatial = scene.camera.getComponent(SpatialComponent.id).get
    var camRight = camSpatial.up.cross(camSpatial.forward)
    
    if (keyMap(Key.Q) == 2) {
	    showHUD = !showHUD
	  }
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
	  updateHUD(camSpatial.position)
  }
	
	/**
	 * Draw method. Is used to draw screen to display etc
	 */
	def draw(): Unit = {
	  var drawStartTime: Long = System.currentTimeMillis()
	  var tmpDisplay: String = ""
	  
	  rend.clear()
	  rend.renderScene(scene)
	  
	  /* HUD -------------------------------------*/
	  tmpDisplay = rend.display
	  
	  if (showHUD) {
  	  rendHUD.clear()
  	  rendHUD.renderScene(sceneHUD)
  	  display = rendHUD.displayOverlay(tmpDisplay)
	  } else {
	    display = tmpDisplay
	  }
	  /* HUD -------------------------------------*/
	  
	  lastDrawTime = System.currentTimeMillis() - drawStartTime
	}

	private def updateHUD(playerLoc: Vec3){
	  var c = " ♥♥♥♥♥♥♥♥"
	  if(hudTextRect.isDefined)
  	  hudTextRect.get.text =  "HP:" +  c + "\n" + 
  	                      "X: " + playerLoc.x + "\n" + 
                          "Y: " + playerLoc.z + "\n" +
                          "drawTime: " + lastDrawTime + "\n"
	}

	def init(): Unit = {
	  
	  scene.camera.getComponent(SpatialComponent.id).get.position = 
	    Vec3(0.0f, -1.2f, 0.0f)
	    
	  var player = Factory.createPlayer()
	  var playerSpatial = player.getComponent(SpatialComponent.id)
	  playerSpatial.get.position = Vec3(0.0f, 1.2f, 0.0f)
	  scene.addEntity(player)
	  
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
//	  scene.addEntity(floor)
	  
	  var level = Factory.createLevel()
	  scene.addEntity(level)
	  
	  /* HUD ----------------------------------------------------------*/
	  hudTextRect = Some(new TextRect2D(new Rectangle2D(32, 5, true),
	                    "Caffeine: 10\nX: 0\n" + "Y: 0\nDeltaTime: 0"))
	                    
	  hudTextRect.get.offX = 2
	  hudTextRect.get.offMinusX = 1
	  hudTextRect.get.offMinusY = 1
	  
	  var rectEnt = Factory2D.createTextRectangle(hudTextRect.get)
	  var testRectSpatial = rectEnt.getComponent(SpatialComponent.id)
	  testRectSpatial.get.position = Vec3(0.0f, rendHUD.h-6, 0.0f)
	  
	  sceneHUD.addEntity(rectEnt)
	  /* --------------------------------------------------------------*/
	}
	
	def resume(){
	  
	}
	
	def pause(){
	  
	}
}