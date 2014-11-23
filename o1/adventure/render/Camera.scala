package o1.adventure.render

import o1.math._

object Camera {
  val degreesToRadians = scala.math.Pi * 2.0f / 360.0f
  
  def calcFrustumScale(fovDegrees: Float): Float = {
    val degToRadians = scala.math.Pi * 2.0f / 360.0f
    var fovRad = fovDegrees * Camera.degreesToRadians
    1.0f / scala.math.tan(fovRad / 2.0f).toFloat;
  }
  
  def perspectiveProjection(
    width: Float, height: Float, 
    fieldOfView: Float, 
    zNear: Float, zFar: Float): Mat4 = {
    
    var cameraToClipMatrix = Mat4()
    val frustumScale = calcFrustumScale(fieldOfView);
    
    cameraToClipMatrix(0)(0) = frustumScale / (width / height)
    cameraToClipMatrix(1)(1) = frustumScale
    cameraToClipMatrix(2)(2) = (zFar + zNear) / (zNear - zFar)
    cameraToClipMatrix(2)(3) = -1.0f
    cameraToClipMatrix(3)(2) = (2 * zFar * zNear) / (zNear - zFar)
    
    cameraToClipMatrix
  }
  
  def getLookMatrix(position: Vec3, forward: Vec3, up: Vec3): Mat4 = {
    var xAxis = forward.cross(up).normalize()
    var yAxis = xAxis.cross(forward).normalize()
    var matrix = Mat4.identity()
    
    matrix(0)(0) = xAxis.x
    matrix(1)(0) = xAxis.y
    matrix(2)(0) = xAxis.z
    
    matrix(0)(1) = yAxis.x
    matrix(1)(1) = yAxis.y
    matrix(2)(1) = yAxis.z
    
    matrix(0)(2) = forward.x
    matrix(1)(2) = forward.y
    matrix(2)(2) = forward.z
    
//    matrix(0) = Vec4(xAxis, 0.0f)
//    matrix(1) = Vec4(yAxis, 0.0f)
//    matrix(2) = Vec4(forward, 0.0f)
    
    var translation = Mat4.identity()
    translation(3) = Vec4(position, 1.0f)
    
    var flipX = Mat4.identity()
    flipX(0)(0) = -1
    
    flipX * matrix * translation
  }
  
  def getTransposeLookMatrix(position: Vec3, forward: Vec3, up: Vec3): Mat4 = {
    var xAxis = forward.cross(up).normalize()
    var yAxis = xAxis.cross(forward).normalize()
    var matrix = Mat4.identity()
    
    matrix(0) = Vec4(xAxis, 0.0f)
    matrix(1) = Vec4(yAxis, 0.0f)
    matrix(2) = Vec4(forward, 0.0f)
    
    var translation = Mat4.identity()
    translation(3) = Vec4(position, 1.0f)
    
    var flipX = Mat4.identity()
    flipX(0)(0) = -1
    
    flipX * matrix * translation
  }
}