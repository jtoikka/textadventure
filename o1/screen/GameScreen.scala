package o1.screen

import scala.concurrent.impl.Future
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.util.Success
import scala.util.Failure
import scala.swing.event.Key
import scala.collection.mutable.Map
import o1.adventure._
import o1.adventure.render._
import scala.math._
import o1.math._
import o1.scene._
import o1.adventure.render2D._
import o1.event.CollisionCheck
import o1.event._
import o1.event.EventType._
import o1.inventory.Inventory
import o1.inventory.Page
import o1.inventory.Coffee
import scala.collection.mutable.Buffer
import scala.util.Random
import o1.inventory.Pellet
import o1.inventory.KillAll

/**
 * GameScreen class.
 * GameScreen can have its own renderer or use common renderer.
 * Game logic would go in this class.
 * @param parent parent Adventure class
 */
class GameScreen(parent: Adventure, rend: Renderer)
    extends Screen(parent, rend) {
  def this(parent: Adventure, x: Int, y: Int) = this(parent, new Renderer3D(x, y))

  var levels = ResourceManager.maps.map(x => (x._1, loadLevel(x._1))).toMap
  
  var scene = new Scene()

  var paused = true

  val FORWARD = 0
  val RIGHT = 1
  val ROTATERIGHT = 2
  val ROTATEUP = 3
  val TOSSCOFFEE = 4
  val DRINKCOFFEE = 5
  val KILLALL = 6
  // Grabs movement inputs
  val movementMap = Map[Int, Float]()

  var player: Option[Entity] = None
  
  var ghostKillCount = 0

  eventHandlers = scala.collection.immutable.Map(
    (E_INPUT, (event, delta) => {
      val eventKey =
        event.args(0).asInstanceOf[Tuple2[scala.swing.event.Key.Value, Int]]
      if (inputMap.contains(eventKey)) {
        inputMap(eventKey)(delta)
      }
    }),
    (E_LOAD_NEW_MAP, (event, delta) => {
      val newMapName = event.args(0).asInstanceOf[String]
      val newSpawnName = event.args(1).asInstanceOf[String]
      println("Load " + newMapName  + " with spawn " + newSpawnName)
      changeLevel(newMapName, newSpawnName)
      
    }),
    (E_PLAYER_CREATION, (event, delta) => {
      player = Some(event.args(0).asInstanceOf[Entity])
    }),
    (E_PLAYER_DAMAGE, (event, delta) => {
      println("Damage")
    }),
    (E_EXPLOSION, (event, delta) => {
      val position = event.args(0).asInstanceOf[Vec3]
      val explosion = Factory.createExplosion(position)
      scene.addEntity(explosion)
    }),
    (E_GHOST_KILLED, (event, delta) => {
      ghostKillCount += 1
      if (ghostKillCount == 5) {
        EventManager.addEvent(new Event(Vector(), EventType.E_OPEN_LAST_DOOR))
        scene.entities.foreach(e => {
          val collisionComponent = e.getComponent(CollisionComponent.id)
          if (collisionComponent.isDefined && collisionComponent.get.collisionType == CollisionComponent.GHOST) {
            e.destroy = true
          }
        })
      }
    }),
    (E_RESET_GAME, (event, delta) => {
      reset()
    }))
    
  def tossCoffee() = {
    val cameraSpatial = scene.camera.get.getComponent(SpatialComponent.id).get
    val coffee = Factory.createCoffeeBullet(
      cameraSpatial.position.neg() +
        cameraSpatial.forward.neg * 0.5f,
      cameraSpatial.forward.neg * 0.8f)
    val spatial = coffee.getComponent(SpatialComponent.id).get
    spatial.forward = cameraSpatial.forward.neg.neg
    spatial.forward.x *= -1
    scene.addEntity(coffee)
  }
  
  def tossPellet() = {
    val cameraSpatial = scene.camera.get.getComponent(SpatialComponent.id).get
    val pellet = Factory.createPelletBullet(
      cameraSpatial.position.neg() +
        cameraSpatial.forward.neg * 0.5f,
      cameraSpatial.forward.neg * 0.8f)
    val spatial = pellet.getComponent(SpatialComponent.id).get
    spatial.forward = cameraSpatial.forward.neg.neg
    spatial.forward.x *= -1
    scene.addEntity(pellet)
  }
    
  /**
   * Update method. Used to update game's state
   */
    
  val rng = new Random()
  
  

  def update(delta: Double): Unit = {
    handleEvents(delta.toFloat)
    scene.entities.foreach(_.handleEvents(delta.toFloat))
    
    levels.filter(_._2 != scene).foreach(_._2.destroyEvents())

    if (!paused) {
      val entitiesAsVector = scene.entities.toVector
      val destroyedEntities = Buffer[Entity]()

      for (entity <- scene.entities) {
        val spatial = entity.getComponent(SpatialComponent.id)
        if (spatial.isDefined) {
          if (entity.getComponent(InputComponent.id).isDefined) {
            handleMovement(entity)
          }
          if (entity.getComponent(FaceCameraComponent.id).isDefined) {
            faceCamera(entity, scene.camera.get)
          }
          if (entity.getComponent(AIComponent.id).isDefined) {
            handleAI(entity, delta)
          }
          if (entity.getComponent(PhysicsComponent.id).isDefined) {
            Physics.applyPhysics(entity, delta)
          }
          CollisionCheck.checkCollisions(entity, entitiesAsVector, scene.world)
          if (entity.getComponent(SpatialComponent.id).get.position.y < 0) {
            entity.destroy = true
            EventManager.addEvent(
                new Event(Vector(spatial.get.position), 
                EventType.E_EXPLOSION))
          }
          val healthComponent = entity.getComponent(HealthComponent.id)
          if (healthComponent.isDefined) {
            if (healthComponent.get.invulnerabilityTimer > 0.0) {
              healthComponent.get.invulnerabilityTimer -= delta
              println(healthComponent.get.invulnerabilityTimer)
            }
            if (healthComponent.get.hp <= 0) {
              if (entity.getComponent(PlayerComponent.id).isDefined) {
                entity.removeComponent(InputComponent.id)
                if (spatial.get.position.y > 0.2) {
                  spatial.get.position.y -= 0.05f * delta.toFloat
                }
                else {
                  EventManager.addEvent(new Event(Vector("gameOverScreen"), EventType.E_CHANGE_SCREEN))
                }
              }
            }
          }
          val animationComponent = entity.getComponent(AnimationComponent.id)
          if (animationComponent.isDefined) {
            val renderComponent = entity.getComponent(RenderComponent.id)
            if (renderComponent.isDefined) {
              renderComponent.get.texture = Some(animationComponent.get.frame)
              animationComponent.get.timer += delta
            }
          }
          val rotateComponent = entity.getComponent(RotateComponent.id)
          if (rotateComponent.isDefined) {
            val rotY = Utility.rotateY(rotateComponent.get.rateForward * delta.toFloat)
            val rotZ = Utility.rotateZ(rotateComponent.get.rateUp * delta.toFloat)
            spatial.get.forward = (
              rotY *
              Vec4(spatial.get.forward, 0.0f)).xyz.normalize()

            spatial.get.up = (
              rotY *
              Vec4(spatial.get.up, 0.0f)).xyz.normalize()

            spatial.get.up = (
              rotZ *
              Vec4(spatial.get.up, 0.0f)).xyz.normalize()

            spatial.get.forward = (
              rotZ *
              Vec4(spatial.get.forward, 0.0f)).xyz.normalize()
          }
          val deathTimerComponent = entity.getComponent(DeathTimerComponent.id)
          if (deathTimerComponent.isDefined) {
            if (deathTimerComponent.get.timer <= 0) {
              entity.destroy = true
            }
            deathTimerComponent.get.timer -= delta
          }
          val spawnComponent = entity.getComponent(SpawnComponent.id)
          if (spawnComponent.isDefined) {
            if (spawnComponent.get.entity.isEmpty || spawnComponent.get.entity.get.destroy) {
              spawnComponent.get.timer += delta
              if (spawnComponent.get.timer > spawnComponent.get.step) {
                spawnComponent.get.timer = 0
                if (spawnComponent.get.spawnType == "pellet") {
                  val pelletSpawn = Factory.createPellet(spatial.get.position)
                  spawnComponent.get.entity = Some(pelletSpawn)
                  scene.addEntity(pelletSpawn)
                } else {
                  val rupeeSpawn = Factory.createRupee(spatial.get.position)
                  spawnComponent.get.entity = Some(rupeeSpawn)
                  scene.addEntity(rupeeSpawn)
                }
              }
            }
          }
        }
        if (entity.destroy) {
          destroyedEntities += entity
        }
      }

      for (e <- destroyedEntities) {
        scene.removeEntity(e)
      }

      traceFront()

      updateCamera()
      movementMap.clear()
    } else {
      events.clear()
    }
  }

  var prevFrontEntity: Option[Entity] = None

  def traceFront() = {
    val camSpatial = scene.camera.get.getComponent(SpatialComponent.id).get
    val forward = camSpatial.forward.neg
    val traced = RayTrace.trace(
      camSpatial.position.neg,
      forward.normalize(),
      scene, 2.0f, 40,
      (entity) => entity.getComponent(InputComponent.id).isEmpty)
    val inFront = traced._1
    if (inFront != prevFrontEntity) {
      prevFrontEntity = inFront
      EventManager.addEvent(new Event(Vector(inFront, traced._2), E_LOOKING_AT))
    }
  }

  def handleAI(entity: Entity, delta: Double) = {
    val aiComponent = entity.getComponent(AIComponent.id).get
    AI.functionMap(aiComponent.botType)(entity, delta, scene)
  }

  def faceCamera(entity: Entity, camera: Entity) = {
    val spatial = entity.getComponent(SpatialComponent.id)
    val cameraPos = camera.getComponent(SpatialComponent.id).get.position
    val setY = spatial.get.forward.y != 0
    spatial.get.forward = (spatial.get.position + cameraPos)
    spatial.get.forward.y = 0
    spatial.get.forward = spatial.get.forward.neg.normalize
    spatial.get.forward.x *= -1
  }

  def handleMovement(entity: Entity) = {
    val spatial = entity.getComponent(SpatialComponent.id).get
    var flippedForward = Vec3()
    flippedForward.x = -spatial.forward.x
    flippedForward.z = spatial.forward.z
    var right = Vec3(0.0f, 1.0f, 0.0f).cross(flippedForward).normalize()
    var up = flippedForward.cross(right).normalize
    if (movementMap.contains(FORWARD)) {
      spatial.position += flippedForward * movementMap(FORWARD)
    }
    if (movementMap.contains(RIGHT)) {
      spatial.position -= right * movementMap(RIGHT)
    }
    if (movementMap.contains(ROTATERIGHT)) {
      spatial.forward = (
        Utility.rotateAxis(movementMap(ROTATERIGHT), up) *
        Vec4(spatial.forward, 0.0f)).xyz.normalize()
//      spatial.up = (
//        Utility.rotateY(movementMap(ROTATERIGHT)) *
//        Vec4(spatial.up, 0.0f)).xyz
    }
    if (movementMap.contains(ROTATEUP)) {
      val matrix = Utility.rotateAxis(-movementMap(ROTATEUP), Vec3(-right.x, right.y, right.z))
      val newForward = (matrix * Vec4(spatial.forward, 0.0f)).xyz.normalize()
      if (newForward.y.abs < 0.5f)
        spatial.forward = newForward
    }
    if (movementMap.contains(TOSSCOFFEE)) {
      val inventory = entity.getComponent(InventoryComponent.id).get
      if (inventory.inv.removeOneOfType(new Pellet("",""))) {
        tossPellet()
      } else if (inventory.inv.removeOneOfType(new Coffee("", ""))) {
        tossCoffee()
      }
    }
    if (movementMap.contains(DRINKCOFFEE)) {
      val inventory = entity.getComponent(InventoryComponent.id).get
      if (inventory.inv.removeOneOfType(new Coffee("", ""))) {
        val health = entity.getComponent(HealthComponent.id).get
        health.hp += 1
        if (health.hp > health.maxHP) {
          health.hp = health.maxHP
        }
      }
    }
    if (movementMap.contains(KILLALL)) {
      val inventory = entity.getComponent(InventoryComponent.id).get
      if (inventory.inv.removeOneOfType(new KillAll("", ""))) {
        var killedAGhost = false
        val assari = scene.entities.find(_.description == "assari")
        if (assari.isDefined) {
          if (assari.get.getComponent(AIComponent.id).isDefined) {
            EventManager.addEvent(new Event(Vector(), E_VICTORY))
          }
        }
      }
    }
  }

  private def updateCamera() = {
    var camSpatial = scene.camera.get.getComponent(SpatialComponent.id).get
    
    val camFollow = scene.camera.get.getComponent(FollowComponent.id).get
    val followSpatial = camFollow.entity.getComponent(SpatialComponent.id)
    
    camSpatial.forward.x = followSpatial.get.forward.x
    camSpatial.forward.y = followSpatial.get.forward.y
    camSpatial.forward.z = -followSpatial.get.forward.z

    camSpatial.position = followSpatial.get.position.neg()

  }

  val SPEED = 0.30f

  val inputMap =
    Map[Tuple2[scala.swing.event.Key.Value, Int], (Float) => Unit](
      ((Key.Q, Input.KEYRELEASED), (delta) => {

      }),
      ((Key.Escape, Input.KEYRELEASED), (delta) => {
        //        EventManager.addEvent(new Event(Vector("menuScreen"), E_CHANGE_SCREEN))
        val d = Factory.createDialog(Vector(
          ("Yes", new Event(Vector("menuScreen"), E_CHANGE_SCREEN)),
          ("No", new Event(Vector("TokaValinta", this.hashCode()), E_ANSWER_DIALOG))),
          "Are you sure?", None, 30, 10)
        EventManager.addEvent(new Event(Vector(d, this.hashCode()), E_THROW_DIALOG))
      }),
      ((Key.I, Input.KEYRELEASED), (delta) => {
        EventManager.addEvent(new Event(Vector("inventoryScreen"), E_CHANGE_SCREEN))
      }),
      ((Key.N, Input.KEYRELEASED), (delta) => {
        //        EventManager.addEvent(new Event(Vector("helpMenuScreen"), E_CHANGE_SCREEN))
        EventManager.addEvent(new Event(Vector("helpMenuScreen"), E_TEST))
      }),
      ((Key.M, Input.KEYRELEASED), (delta) => {
        EventManager.addEvent(new Event(Vector("mapScreen"), E_CHANGE_SCREEN))
      }),
      ((Key.L, Input.KEYRELEASED), (delta) => {
//        loadLevel("00_testmap","startSpawn")
      }),
      ((Key.W, Input.KEYDOWN), (delta) => {
        movementMap(FORWARD) = SPEED * delta
      }),
      ((Key.S, Input.KEYDOWN), (delta) => {
        movementMap(FORWARD) = -SPEED * delta
      }),
      ((Key.E, Input.KEYPRESSED), (delta) => {
        EventManager.addEvent(new Event(Vector(player, prevFrontEntity), E_INTERACTION))
      }),
      ((Key.A, Input.KEYDOWN), (delta) => {
        movementMap(RIGHT) = -SPEED * delta
      }),
      ((Key.D, Input.KEYDOWN), (delta) => {
        movementMap(RIGHT) = SPEED * delta
      }),
      ((Key.Left, Input.KEYDOWN), (delta) => {
        movementMap(ROTATERIGHT) = -0.2f * delta
      }),
      ((Key.Right, Input.KEYDOWN), (delta) => {
        movementMap(ROTATERIGHT) = 0.2f * delta
      }),
      ((Key.Up, Input.KEYDOWN), (delta) => {
        movementMap(ROTATEUP) = 0.2f * delta
      }),
      ((Key.Down, Input.KEYDOWN), (delta) => {
        movementMap(ROTATEUP) = -0.2f * delta
      }),
      ((Key.Space, Input.KEYPRESSED), (delta) => {
        movementMap(TOSSCOFFEE) = 1
      }),
      ((Key.R, Input.KEYRELEASED), (delta) => {
        movementMap(DRINKCOFFEE) = 1
      }),
      ((Key.K, Input.KEYRELEASED), (delta) => {
        movementMap(KILLALL) = 1
      }))

  /**
   * Draw method. Is used to draw screen to display etc
   */
  def draw(): String = {
    var drawStartTime: Long = System.currentTimeMillis()
    var tmpDisplay: String = ""

    rend.clear()
    rend.renderScene(scene)

    tmpDisplay = rend.display

    parent.screens("hudScreen").draw()
    parent.screens("hudScreen").rend.displayOverlay(tmpDisplay)
    
  }
  
  init()
  def init(): Unit = {
//    changeLevel("00_startlevel", "startSpawn")
    changeLevel("07_temple", "templeSpawn")
//    changeLevel("06_panic", "01_entrance")
  }
  
  def loadLevel(level: String): Scene = {
    val newScene = new Scene()
    Level.loadMap(newScene, level)
    newScene
  }
  
  
  def changeLevel(level: String, spawn: String) = {
    var player = scene.entities.find(_.getComponent(PlayerComponent.id).isDefined)
    if (player.isDefined) {
      scene.entities.remove(scene.entities.indexOf(player.get))
    }
    var camera = scene.camera
    scene = levels(level)
    
    scene.entities.filter(_.getComponent(PlayerComponent.id).isDefined).foreach(scene.removeEntity(_))
    val xml = ResourceManager.maps(level)
    val objectGroups = collection.mutable.Map[String, scala.xml.Node]()
    for (layer <- xml \ "objectgroup") {
      val name = layer \ "@name"
      objectGroups(name.text) = layer
    }
    val playerSpawn = (objectGroups("player") \ "object")
    val node = playerSpawn.find(a => ((a \ "properties" \ "property").find(q => (q \ "@name").text == "name").get \ "@value").text == spawn)
    
    if (player.isEmpty) {
      player = Some(Factory.createPlayer(node.get))
    }
    
    val loc = Vec2((node.get \ "@x").text.toFloat, (node.get \ "@y").text.toFloat)
    player.get.getComponent(SpatialComponent.id).get.position = 
      Vec3(loc.x * 2 / 16 + 0.0001f, 1.4f, loc.y * 2 / 16 + 0.0001f)
    if (camera.isEmpty) {
      camera = Some(Factory.createCamera(player.get))
    }
    
    scene.addEntity(player.get)
    scene.camera = camera

    EventManager.addEvent(new Event(Vector(scene.world), E_CHANGE_MAP))
  }

  var firstTime = true
  
  def resume() {
    paused = false
    if (firstTime) {
      val d = Factory.createDialog(Vector(
          ("OK", new Event(Vector("", this.hashCode()), E_ANSWER_DIALOG))),
          ResourceManager.strings("introDialog"), Some(this), 80, 10)
      EventManager.addEvent(new Event(Vector(d, this.hashCode()), E_THROW_DIALOG))
      firstTime = false
    }
  }
  
  def pause() = {
    paused = true
  }

  def dispose() = {

  }
  
  def reset() = {
    for (scene <- levels.values) {
      scene.clear()
    }
    levels = ResourceManager.maps.map(x => (x._1, loadLevel(x._1))).toMap
    scene = new Scene()
    player = None
    prevFrontEntity = None
    init()
  }
}