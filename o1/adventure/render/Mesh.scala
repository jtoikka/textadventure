package o1.adventure.render

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map
import scala.io.Source
import o1.math._
import scala.collection.immutable.SortedMap
import scala.collection.immutable.TreeMap


class Vertex(
    val index: Int,
    val uv: Vec2 = Vec2(0.0f, 0.0f)) {
  
}

class Face(val vertices: Vector[Vertex]) {
}

object Mesh {
  def apply(fileName: String): Mesh = {
    val vertexBuffer = ArrayBuffer[Vec3]()
    val uvBuffer = ArrayBuffer[Vec2]()
    val indexBuffer = ArrayBuffer[Int]()
    val uvIndexBuffer = ArrayBuffer[Int]()
    
    for (line <- Source.fromFile(fileName).getLines()) {
      var data = line.split(" ")
      data(0) match {
        case "v" => {
          vertexBuffer += Vec3(
            data(1).toFloat,
            data(2).toFloat,
            data(3).toFloat)
        }
        case "vt" => {
          uvBuffer += Vec2(data(1).toFloat, data(2).toFloat)
        }
        case "f" => {
          val split1 = data(1).split("/")
          val split2 = data(2).split("/")
          val split3 = data(3).split("/")
          indexBuffer += split1(0).toInt - 1
          indexBuffer += split2(0).toInt - 1
          indexBuffer += split3(0).toInt - 1
          
          if (split1.size > 1) {
            uvIndexBuffer += split1(1).toInt - 1
            uvIndexBuffer += split2(1).toInt - 1
            uvIndexBuffer += split3(1).toInt - 1
          }
        }
        case default => {}
      }
    }
    val faces = ArrayBuffer[Face]()
    
    
    for (i <- 0 until indexBuffer.size by 3) {
      val posA = vertexBuffer(indexBuffer(i))
      val posB = vertexBuffer(indexBuffer(i + 1))
      val posC = vertexBuffer(indexBuffer(i + 2))
      
      var vertA = new Vertex(indexBuffer(i))
      var vertB = new Vertex(indexBuffer(i + 1))
      var vertC = new Vertex(indexBuffer(i + 2))
      
      if (!uvBuffer.isEmpty) {
        val uvA = uvBuffer(uvIndexBuffer(i))
        val uvB = uvBuffer(uvIndexBuffer(i + 1))
        val uvC = uvBuffer(uvIndexBuffer(i + 2))
        
        vertA = new Vertex(indexBuffer(i), uvA)
        vertB = new Vertex(indexBuffer(i + 1), uvB)
        vertC = new Vertex(indexBuffer(i + 2), uvC)
        
        println("A: " + posA + " UV: " + uvA)
        println("B: " + posB + " UV: " + uvB)
        println("C: " + posC + " UV: " + uvC)
      }
      
      val face = new Face(Vector[Vertex](vertA, vertB, vertC))
      
      faces += face
    }
    
    new Mesh(vertexBuffer, faces, !uvBuffer.isEmpty)
  }
}

class Mesh(
    val vertexBuffer: ArrayBuffer[Vec3],
    val faces: ArrayBuffer[Face],
    val uvMapped: Boolean) {
    
  var luminosity = 1.0f
  var transformedBuffer = 
    Array.fill[Vec4](vertexBuffer.size)(Vec4(0.0f, 0.0f, 0.0f, 0.0f))
    
  def hasUV = uvMapped
  
/**
 * Gets a triangle from the mesh. An [index] of 0 returns the first triangle, 
 * and `numTriangles` the last triangle. If the index is out of range, or if the
 * triangle is entirely outside of the viewing frustum, the value `None` is 
 * returned.
 */
  def getTriangles(index: Int): ArrayBuffer[Triangle] = {
    var array = ArrayBuffer[Triangle]()
    
    if (index < faces.size) {
      val face = faces(index)
      
      val v1 = transformedBuffer(face.vertices(0).index)
      val v2 = transformedBuffer(face.vertices(1).index)
      val v3 = transformedBuffer(face.vertices(2).index)
      
      val uv1 = face.vertices(0).uv
      val uv2 = face.vertices(1).uv
      val uv3 = face.vertices(2).uv
      
      if ((v1.x <= -v1.w && v2.x <= -v2.w && v3.x <= -v3.w) ||
          (v1.x >=  v1.w && v2.x >=  v2.w && v3.x >=  v3.w) ||
          (v1.y <= -v1.w && v2.y <= -v2.w && v3.y <= -v3.w) ||
          (v1.y >=  v1.w && v2.y >=  v2.w && v3.y >=  v3.w) ||
          (v1.z <= -v1.w && v2.z <= -v2.w && v3.z <= -v3.w) ||
          (v1.z >=  v1.w && v2.z >=  v2.w && v3.z >=  v3.w)) {
      } else {
        var points = ArrayBuffer[Vec4]()
        var uvs = ArrayBuffer[Vec2]()
        if (v1.z > 0.0f) {
          points += v1
          uvs += uv1
        }
        
        if (v1.z * v2.z < 0.0f) {
          var vecA = v1 - v2
          var point = v2 + vecA * (v2.z.abs / vecA.z.abs).toFloat
          points += point
          
          var uvA = uv1 - uv2
          var newUV = uv2 + uvA * (v2.z.abs / vecA.z.abs).toFloat
          uvs += newUV
        }
          
        if (v2.z > 0.0f) {
          points += v2
          uvs += uv2
        }
          
        if (v2.z * v3.z < 0.0f) {
          var vecA = v2 - v3
          var point = v3 + vecA * (v3.z.abs / vecA.z.abs).toFloat
          points += point
          
          var uvA = uv2 - uv3
          var newUV = uv3 + uvA * (v3.z.abs / vecA.z.abs).toFloat
          uvs += newUV
        }
        
        if (v3.z > 0.0f) {
          points += v3
          uvs += uv3
        }
                
        if (v3.z * v1.z < 0.0f) {
          var vecA = v3 - v1
          var point = v1 + vecA * (v1.z.abs / vecA.z.abs).toFloat
          points += point
                
          var uvA = uv3 - uv1
          var newUV = uv1 + uvA * (v1.z.abs / vecA.z.abs).toFloat
          uvs += newUV
        }
        
          
        if (points.size >= 3) {
          array += new Triangle(points(0), points(1), points(2), uvs(0), uvs(1), uvs(2))
        }
        if (points.size >= 4) {
          array += new Triangle(points(0), points(2), points(3), uvs(0), uvs(2), uvs(3))
        }
      }
    }
    array
  }
  
  def numTriangles = faces.size

/**
 * Transforms all vertices by the given [matrix].
 */
  def transform(matrix: Mat4) = {
    for (i <- 0 until vertexBuffer.size) {
      val v = vertexBuffer(i)
      val vertex = Vec4(
        v.x, v.y, v.z,
        1.0f)
      transformedBuffer(i) = matrix * vertex
    }
  }
  
}

class Triangle(val a: Vec4, val b: Vec4, val c: Vec4, val uv1: Vec2, val uv2: Vec2, val uv3: Vec2) {
  override def toString() = {
    "a x: " + a.x + " y: " + a.y + " z: " + a.z + " w: " + a.w + "\n" +
    "b x: " + b.x + " y: " + b.y + " z: " + b.z + " w: " + b.w + "\n" +
    "c x: " + c.x + " y: " + c.y + " z: " + c.z + " w: " + c.w
  }
}