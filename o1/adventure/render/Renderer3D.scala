package o1.adventure.render

import scala.collection.mutable.Map
import scala.swing.event.Key
import scala.math._
import o1.math._
import o1.scene._
import o1.event.SolidTile
import o1.adventure.render2D.Image2D
import o1.adventure.render2D.Renderer2D


class Renderer3D(w: Int, h: Int) extends Renderer(w,h) {
  val framebufferWidth = w * 2
  
/* Buffers -------------------------------------------------------------------*/
  val newLineCharWidth = 1
  private var _frontBuffer   = new Array[Char]((w + newLineCharWidth) * h) 
  private var _normalBuffer  = new Array[Vec3](framebufferWidth * h)
  private var _depthBuffer   = new Array[Float](framebufferWidth * h)
  private var _diffuseBuffer = new Array[Float](framebufferWidth * h)
  private var _viewRayBuffer = new Array[Vec3](framebufferWidth * h)
/* ---------------------------------------------------------------------------*/
  
  val zNear = 0.1f  // Near clipping plane
  val zFar = 30.0f // Far clipping plane
  val fov = 60.0f
  
  val _ramp = "MEIi!:,. "
  
/**
 * Matrix for ordered dithering. Used to shift a pixel's luminosity either up or
 * down, to simulate a more expansive color range.
 */
  val bayerMatrix: Array[Int] = Array( 
     0, 32,  8, 40,  2, 34, 10, 42,
    48, 16, 56, 24, 50, 18, 58, 26,
    12, 44,  4, 36, 14, 46,  6, 38,
    60, 28, 52, 20, 62, 30, 54, 22,
     3, 35, 11, 43,  1, 33,  9, 41,
    51, 19, 59, 27, 49, 17, 57, 25,
    15, 47,  7, 39, 13, 45,  5, 37,
    63, 31, 55, 23, 61, 29, 53, 21)

/*
 * Offset by 32 so that half of the pixels have their value increase, half
 * decreased.
 */
  for (i <- 0 until bayerMatrix.size) {
    bayerMatrix(i) = (bayerMatrix(i) - 32)
  }
  
  val ditherStrength = 1.0f / (64.0f * _ramp.size)
    
  val pixelRatio = 0.5f // Pixel height/width

  var cubeRotation = Mat4.identity()
  
  var cameraToClipMatrix = 
    Camera.perspectiveProjection(w * pixelRatio, h, fov, zNear, zFar)

/**
 * Sets a pixel at index [position] to [character]. Use the function calcIndex
 * to determine the index.
 */
  def setPixel(position: Int, character: Char) = {
    if (position >= 0 && position < _frontBuffer.size)
      this._frontBuffer(position) = character
  }

/**
 * Calculate the index of a pixel from given screen coordinates [x] and [y].
 * The coordinate (0, 0) is the top-left corner.
 */
  def calcDoubleIndex(x: Float, y: Float): Int = 
    (y.round * framebufferWidth + x.round).toInt

  def calcDoubleIndex(x: Int, y: Int): Int = 
    y * framebufferWidth + x
    
  def calcFrontIndex(x: Int, y: Int): Int = 
    y * w + x / 2

/**
 * Sets initial front and back buffer values, and clears depth buffer.
 */
  def initialize() = {
    this.clear()
    for (i <- 0 until _frontBuffer.size) {
      _frontBuffer(i) = '\n'
    }
    for (y <- 0 until h) {
      for (x <- 0 until (w - 1) * 2) {
        _viewRayBuffer(calcDoubleIndex(x, y)) = calcViewRay(x, y)
      }
    }
  }
  
  initialize()

/**
 * Converts clip-space coordinates to screen-space coordinates. 
 */
  def screenCoordinates(coord: Vec4): Vec4 = {
    val perspectiveCorrect = coord.xyz / coord.w
    val x = (perspectiveCorrect.x + 1.0f) * 0.5f
    val y = (perspectiveCorrect.y + 1.0f) * 0.5f
    val z = (perspectiveCorrect.z)
    Vec4(x * framebufferWidth, (1.0f - y) * h, z, 1.0f / coord.w)
  }

/**
 * Clears the depth and diffuse buffer.
 */
  def clear() = {
    for (y <- 0 until h) {
      for (x <- 0 until framebufferWidth) {
        val index = calcDoubleIndex(x, y)
        this._depthBuffer(index) = 1.0f
        this._diffuseBuffer(index) = 1.0f
        this._normalBuffer(index) = Vec3(0.0f, 0.0f, 0.0f)
      }
    }
  }
  
/**
 * Clamps a [value] to the range [minimum, maximum].
 */
  private def clamp(value: Float, minimum: Float, maximum: Float): Float = {
    min(maximum, max(minimum, value))
  }
  
  def cullObject(cameraPos: Vec2, cameraForward: Vec2, objectPos: Vec2, angleLimit: Double): Boolean = {
    val posA = objectPos + Vec2(1.0f, 1.0f)
    val posB = objectPos + Vec2(1.0f, -1.0f)
    val posC = objectPos + Vec2(-1.0f, 1.0f)
    val posD = objectPos + Vec2(-1.0f, -1.0f)
    val dirFromCameraA = (posA + cameraPos).neg().normalize()
    val dirFromCameraB = (posB + cameraPos).neg().normalize()
    val dirFromCameraC = (posC + cameraPos).neg().normalize()
    val dirFromCameraD = (posD + cameraPos).neg().normalize()
    
    val cosAngleBetweenA = dirFromCameraA.dot(cameraForward.normalize())
    val cosAngleBetweenB = dirFromCameraB.dot(cameraForward.normalize())
    val cosAngleBetweenC = dirFromCameraC.dot(cameraForward.normalize())
    val cosAngleBetweenD = dirFromCameraD.dot(cameraForward.normalize())
    
    !((cosAngleBetweenA > angleLimit) || 
      (cosAngleBetweenB > angleLimit) || 
      (cosAngleBetweenC > angleLimit) ||
      (cosAngleBetweenD > angleLimit))
  }
  
/**
 * TODO: Render actual scene. Currently uses test parameters. Uses values from
 * depth buffer and (once implemented) normal buffer to determine the final
 * value of a pixel. 
 */
  def renderScene(scene: Scene) = {
/* First pass, render geometry -----------------------------------------------*/
    var camera = scene.camera
    if (camera.isDefined) {
      var cameraSpatial = camera.get.getComponent(SpatialComponent.id).get
      
      var worldToCam = Camera.getLookMatrix(
          cameraSpatial.position, 
          cameraSpatial.forward, 
          cameraSpatial.up)
          
      if (scene.world.isDefined) {
        val world = scene.world.get
        val tileWidth = world.tileMap.TILEWIDTH
        for (x <- 0 until world.width) {
          for (y <- 0 until world.depth) {
            if (world.tileMap.getCollisionTile(x, y).isInstanceOf[SolidTile]) {
              val worldX = x * tileWidth
              val worldY = y * tileWidth
              if (!cullObject(
                  cameraSpatial.position.xz, 
                  cameraSpatial.forward.xz, 
                  Vec2(worldX, worldY), cos(fov / 360 * 2 * Math.PI))) {
                var translation = 
                  Utility.translate(Vec4(
                      worldX, 0.0f, 
                      worldY, 
                      1.0f))
                var mv = worldToCam * translation
                var matrix = cameraToClipMatrix * mv
                renderMesh(
                    ResourceManager.meshes("uv_cube"), 
                    matrix, 
//                    None)
                    Some(ResourceManager.textures("testTex")))
              }
            }
          }
        }
      }          
      
      for (entity <- scene.entities) {
        def renderEntity(entity: Entity) = {
          var spatialComp = entity.getComponent(SpatialComponent.id)
          var renderComp = entity.getComponent(RenderComponent.id)
          if (spatialComp.isDefined && renderComp.isDefined) {
            if (!cullObject(
                cameraSpatial.position.xz, 
                cameraSpatial.forward.xz, 
                spatialComp.get.position.xz, 
                0.7)) {
              var translation = Utility.translate(Vec4(spatialComp.get.position, 1.0f))
              var rotation = Camera.getLookMatrix(
                Vec3(0.0f, 0.0f, 0.0f), 
                spatialComp.get.forward, 
                spatialComp.get.up)
              var mv = worldToCam * translation * rotation
              var matrix = cameraToClipMatrix * mv
              if (renderComp.get.texture.isDefined) {
                renderMesh(
                    ResourceManager.meshes(renderComp.get.mesh), 
                    matrix, 
                    Some(ResourceManager.textures(renderComp.get.texture.get)))
              } else {
                renderMesh(ResourceManager.meshes(renderComp.get.mesh), matrix)
              }
            }
          }
        }
        renderEntity(entity)
        for (child <- entity.children) {
          renderEntity(child)
        }
      }
  
  /* Pixel Shader Stuff --------------------------------------------------------*/
      for (y <- 0 until h) {
        val bayesRow = y % 8
        for (x <- 0 until (w - 1) * 2) {
          val bayesCollumn = x % 8
          
          val index1 = calcDoubleIndex(x, y)
          val index2 = calcDoubleIndex(x + 1, y)
         
          var diffuse = (_diffuseBuffer(index1) + _diffuseBuffer(index2)) / 2.0f
          var normal = (_normalBuffer(index1))
          var viewRay = _viewRayBuffer(index1)
          var depth = (_depthBuffer(index1) + _depthBuffer(index2)) / 2.0f
          var attenuation = 4.0f / (depth + 8.0f * depth * depth)
          var specular = clamp(normal.dot(viewRay), 0.0f, 1.0f) * -viewRay.z * attenuation * diffuse
          if (normal.y > 0.9) {
            specular = (1.0f - viewRay.y) * viewRay.z * viewRay.z * -viewRay.z * depth * 0.2f * attenuation
          }
          val diffuseLight = depth * 0.4f * attenuation * diffuse
          var lighting = specular * 0.4f + diffuseLight * 1.0f
          lighting = 1.0f - Math.exp(2.2 * -lighting).toFloat
          if (depth >= 1.0) lighting = 0.0f
          val bayer = bayerMatrix(8 * bayesRow + bayesCollumn)
          var v = (lighting + (bayer * ditherStrength)) * _ramp.size
          v = clamp(v, 0.0f, _ramp.size.toFloat - 1.0f)
          
          setPixel(calcFrontIndex(x, y), _ramp(_ramp.size - v.toInt - 1))
        }
      }
    }
/*----------------------------------------------------------------------------*/
  }
  
  def calcViewRay(x: Int, y: Int): Vec3 = {
    var hx = x * 2.0f / framebufferWidth - 1.0f
    var hy = (1.0f - y.toFloat / h) * 2.0f - 1.0f
    var viewRay = Vec3(
      hx / cameraToClipMatrix(0).x,
      -hy / cameraToClipMatrix(1).y,
      -1.0f);
    viewRay.normalize
  }

/**
 * Renders a [mesh] to screen. Applies Model-View-Projection matrix [MVP] to 
 * mesh prior to rendering.
 */
  def renderMesh(mesh: Mesh, MVP: Mat4, texture: Option[Texture] = None) = {
    mesh.transform(MVP)
    for(i <- 0 to mesh.numTriangles) {
      var triangles = mesh.getTriangles(i)
      if (!triangles.isEmpty) {
        for (triangle <- triangles) {
          if (mesh.hasUV && texture.isDefined) {
            renderTriangle(triangle, mesh.luminosity, texture)
          } else {
            renderTriangle(triangle, mesh.luminosity, None)
          }
        }
      }
    }
  }

/**
 * Renders a mesh's triangle to screen. See mesh.getTriangle function for
 * details on how to get a triangle.
 */
  def renderTriangle(triangle: Triangle, luminosity: Float, texture: Option[Texture]) = {
    val screenA = screenCoordinates(triangle.a)
    val screenB = screenCoordinates(triangle.b)
    val screenC = screenCoordinates(triangle.c)
    
    val vecA = triangle.b - triangle.a
    val vecB = triangle.c - triangle.a
    
    val screenVecA = screenB - screenA
    val screenVecB = screenC - screenA
    
    val screenNormal = screenVecA.xyz.cross(screenVecB.xyz)

/* Culling -------------------------------------------------------------------*/
    val normal = vecB.xyz.cross(vecA.xyz)      
    if (screenNormal.z < 0.0) {
      if (texture.isDefined) {
        fillTexturedTriangle(screenA, screenB, screenC, triangle.uv1, triangle.uv2, triangle.uv3, texture.get, normal.normalize)
      } else {
        fillTriangle(screenA, screenB, screenC, luminosity, normal.normalize)
      }
    }
  }
  
/**
 * Fills in a triangle, with vectors a, b and c given in screen coordinates. 
 * Information on the algorithm used can be found at:
 * http://www.sunshine2k.de/coding/java/TriangleRasterization/TriangleRasterization.html
 */
  
  def fillTriangle(a: Vec4, b: Vec4, c: Vec4, luminosity: Float, normal: Vec3) = {
    var minX = max(0, min(a.x, min(b.x, c.x)))
    var minY = max(0, min(a.y, min(b.y, c.y)))
    var maxX = min(framebufferWidth - 1, max(a.x, max(b.x, c.x)))
    var maxY = min(this.h - 1, max(a.y, max(b.y, c.y)))
        
    for (x <- minX.floor.toInt to maxX.ceil.toInt) {
      for (y <- minY.floor.toInt to maxY.ceil.toInt) {
        var bary = barycentricCoordinates(a.xy, b.xy, c.xy, Vec2(x, y));
        
        if (
          (bary.x >= 0.0f) && 
          (bary.y >= 0.0f) && 
          (bary.z >= 0.0f) && 
          (bary.x + bary.y + bary.z <= 1.01f)) {
          
          var depth = (bary.x * a.z + bary.y * b.z + bary.z * c.z) 
          var w = (bary.x * a.w + bary.x * b.w + bary.x * c.w)
          depth = depth
          val index = calcDoubleIndex(x, y)
          if (depth < _depthBuffer(index) && depth > 0.0) {
            val i = (depth * _ramp.size).toInt
            _depthBuffer(index) = depth
            _diffuseBuffer(index) = luminosity
            _normalBuffer(index) = normal
          }
        }
      }
    }
  }
  
  def fillTexturedTriangle(
      a: Vec4, b: Vec4, c: Vec4,
      uv1: Vec2, uv2: Vec2, uv3: Vec2,
      texture: Texture, 
      normal: Vec3) = {
    var minX = max(0, min(a.x, min(b.x, c.x)))
    var minY = max(0, min(a.y, min(b.y, c.y)))
    var maxX = min(framebufferWidth - 1, max(a.x, max(b.x, c.x)))
    var maxY = min(this.h - 1, max(a.y, max(b.y, c.y)))
        
    for (x <- minX.floor.toInt to maxX.ceil.toInt) {
      for (y <- minY.floor.toInt to maxY.ceil.toInt) {
        var bary = barycentricCoordinates(a.xy, b.xy, c.xy, Vec2(x, y));
        if (
          (bary.x >= 0.0f) && 
          (bary.y >= 0.0f) && 
          (bary.z >= 0.0f) && 
          (bary.x + bary.y + bary.z <= 1.01f)) {
          
          var depth = (bary.x * a.z + bary.y * b.z + bary.z * c.z) 
          var w = (bary.x * a.w + bary.y * b.w + bary.z * c.w)
          depth = depth

          var uv = uv1 * a.w * bary.x + uv2 * b.w * bary.y + uv3 * c.w * bary.z
          uv = Vec2(uv.x / w, uv.y / w)
          val index = calcDoubleIndex(x, y)
          if (depth < _depthBuffer(index) && depth > 0.0) {
            val colour = texture.getPixel(uv.x, uv.y)
            val a = colour >> 24 & 0xFF
            val aNormalized = a / 255.0f
            val r = colour >> 16 & 0xFF
            val g = colour >> 8 & 0xFF
            val b = colour & 0xFF
            if ((a != 0)) {
              val r = colour >> 16 & 0xFF
              
              val i = (depth * _ramp.size).toInt
              _depthBuffer(index) = depth
              _diffuseBuffer(index) = (r * 0.299f + g * 0.587f + b * 0.114f) / 255.0f
              _normalBuffer(index) = normal
            }
          }
        }
      }
    }
  }
  
/**
 * From Christer Ericson's "Real-Time Collision Detection". Computes barycentric
 *  coordinates of a point [pointP] in a triangle (defined by vertices [pointA],
 *   [pointB], and [pointC].
 */
  def barycentricCoordinates(
      pointA: Vec2, 
      pointB: Vec2, 
      pointC: Vec2, 
      pointP: Vec2): Vec3 = {
    val v1 = pointB - pointA
    val v2 = pointC - pointA
    val v3 = pointP - pointA
    
    val dot11 = v1.dot(v1)
    val dot12 = v1.dot(v2)
    val dot22 = v2.dot(v2)
    val dot31 = v3.dot(v1)
    val dot32 = v3.dot(v2)
    val denominator = dot11 * dot22 - dot12 * dot12
    val v = (dot22 * dot31 - dot12 * dot32) / denominator;
    val w = (dot11 * dot32 - dot12 * dot31) / denominator;
    val u = 1.0f - v - w;
    Vec3(u, v, w)
  }
  
  def linearDepth(depth: Float) = {
    (2 * zNear / (zFar + zNear - depth * (zFar - zNear)));
  }
  
/**
 * Converts the front buffer to a String, allowing the image to be rendered in a 
 * text area.
 */
  def display = this._frontBuffer.mkString("")
  
  def displayOverlay(disp: String): String = {
    var str = disp.toCharArray()
    for (i <- 0 to str.length - 1) {
      if (_frontBuffer(i) != Renderer.empty &&
        _frontBuffer(i) != '\n' && str(i) != '\n')
        str(i) = _frontBuffer(i)
    }
    str.mkString("")
  }
}