package o1.math

import scala.math._

object Utility {
  def rotateY(radians: Float): Mat4 = {
    var matrix = Mat4.identity()
    var cosAng = cos(radians).toFloat
    var sinAng = sin(radians).toFloat
    
    matrix(0)(0) = cosAng
    matrix(2)(0) = sinAng
    matrix(0)(2) = -sinAng
    matrix(2)(2) = cosAng
    
    matrix
  }
  
  def rotateZ(radians: Float): Mat4 = {
    var matrix = Mat4.identity()
    var cosAng = cos(radians).toFloat
    var sinAng = sin(radians).toFloat
    
    matrix(0)(0) = cosAng
    matrix(0)(1) = -sinAng
    matrix(1)(0) = sinAng
    matrix(1)(1) = cosAng
    
    matrix
  }
  
  def rotateX(radians: Float): Mat4 = {
    var matrix = Mat4.identity()
    var cosAng = cos(radians).toFloat
    var sinAng = sin(radians).toFloat
    
    matrix(1)(1) = cosAng
    matrix(2)(1) = -sinAng
    matrix(1)(2) = sinAng
    matrix(2)(2) = cosAng
    
    matrix
  }
  
  def rotateAxis(radians: Float, axis: Vec3): Mat4 = {
    var matrix = Mat4.identity()
    var cosAng = cos(radians).toFloat
    var rCosAng = 1 - cosAng
    var sinAng = sin(radians).toFloat
    
    matrix(0)(0) = cosAng + axis.x * axis.x * rCosAng
    matrix(1)(0) = axis.x * axis.y * rCosAng - axis.z * sinAng
    matrix(2)(0) = axis.x * axis.z * rCosAng + axis.y * sinAng
    
    matrix(0)(1) = axis.y * axis.x * rCosAng + axis.z * sinAng
    matrix(1)(1) = cosAng + axis.y * axis.y * rCosAng
    matrix(2)(1) = axis.y * axis.z * rCosAng - axis.x * sinAng
    
    matrix(0)(2) = axis.z * axis.x * rCosAng - axis.y * sinAng
    matrix(1)(2) = axis.z * axis.y * rCosAng + axis.x * sinAng
    matrix(2)(2) = cosAng + axis.z * axis.z * rCosAng
    
    matrix
  }
  
  def translate(direction: Vec4): Mat4 = {
    var matrix = Mat4.identity()
    matrix(3) = direction
    matrix(3)(3) = 1.0f
    matrix
  }
  
  def scale(amount: Vec3): Mat4 = {
    var matrix = Mat4.identity()
    matrix(0)(0) = amount.x
    matrix(1)(1) = amount.y
    matrix(2)(2) = amount.z
    
    matrix
  }
}