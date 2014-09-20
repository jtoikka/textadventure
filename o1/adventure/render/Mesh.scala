package o1.adventure.render

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

import o1.math._


object Mesh {
  def apply(fileName: String): Mesh = {
    val vertexBuffer = ArrayBuffer[Float]()
    val indexBuffer = ArrayBuffer[Int]()
    for (line <- Source.fromFile(fileName).getLines()) {
      var data = line.split(" ")
      data(0) match {
        case "v" => {
          vertexBuffer += data(1).toFloat
          vertexBuffer += data(2).toFloat
          vertexBuffer += data(3).toFloat
        }
        case "f" => {
          indexBuffer += data(1).split("/").apply(0).toInt - 1
          indexBuffer += data(2).split("/").apply(0).toInt - 1
          indexBuffer += data(3).split("/").apply(0).toInt - 1
        }
        case defualt => {}
      }
    }
    new Mesh(vertexBuffer, indexBuffer)
  }
}

class Mesh(
  val vertexBuffer: ArrayBuffer[Float], 
  val indexBuffer: ArrayBuffer[Int]) {
    
  var transformedBuffer = 
    Array.fill[Vec4](vertexBuffer.size / 3)(Vec4(0.0f, 0.0f, 0.0f, 0.0f))
  
/**
 * Gets a triangle from the mesh. An [index] of 0 returns the first triangle, 
 * and `numTriangles` the last triangle. If the index is out of range, or if the
 * triangle is entirely outside of the viewing frustum, the value `None` is 
 * returned.
 */
  def getTriangle(index: Int): Option[Triangle] = {
    if ((index + 1) * 3 <= indexBuffer.size) {
      val i1 = indexBuffer(index * 3)
      val i2 = indexBuffer(index * 3 + 1)
      val i3 = indexBuffer(index * 3 + 2)
      
      var v1 = transformedBuffer(i1)
      var v2 = transformedBuffer(i2)
      var v3 = transformedBuffer(i3)
      
      if ((v1.x <= -v1.w && v2.x <= -v2.w && v3.x <= -v3.w) ||
          (v1.x >=  v1.w && v2.x >=  v2.w && v3.x >=  v3.w) ||
          (v1.y <= -v1.w && v2.y <= -v2.w && v3.y <= -v3.w) ||
          (v1.y >=  v1.w && v2.y >=  v2.w && v3.y >=  v3.w) ||
          (v1.z <= -v1.w && v2.z <= -v2.w && v3.z <= -v3.w) ||
          (v1.z >=  v1.w && v2.z >=  v2.w && v3.z >=  v3.w)) {
        None
      } else {
        Some(new Triangle(v1, v2, v3))
      }
    } else {
      None
    }
  }
  
  def numTriangles = indexBuffer.length / 3

/**
 * Transforms all vertices by the given [matrix].
 */
  def transform(matrix: Mat4) = {
    for (i <- 0 to vertexBuffer.size / 3 - 1) {
      val vertex = Vec4(
        vertexBuffer(i * 3), 
        vertexBuffer(i * 3 + 1), 
        vertexBuffer(i * 3 + 2), 
        1.0f)
      transformedBuffer(i) = matrix * vertex
    }
  }
  
}

class Triangle(val a: Vec4, val b: Vec4, val c: Vec4) {
  
}