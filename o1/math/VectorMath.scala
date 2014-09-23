package o1.math

import scala.math.pow

object Vec2 {
  def apply(x: Float, y: Float) = {
    new Vec2(x, y)
  }
  
  def apply() = {
    new Vec2(0.0f, 0.0f)
  }
}

class Vec2(var x: Float, var y: Float) {
  def +(other: Vec2) = new Vec2(this.x + other.x, this.y + other.y)
  
  def -(other: Vec2) = new Vec2(this.x - other.x, this.y - other.y)
  
  def *(other: Vec2) = new Vec2(this.x * other.x, this.y * other.y)
  
  def *(value: Float) = new Vec2(this.x * value, this.y * value)
  
  def /(other: Vec2) = new Vec2(this.x / other.x, this.y * other.y)
  
  def /(value: Float) = new Vec2(this.y / value, this.y / value)
  
  def dot(other: Vec2): Float = this.x * other.x + this.y * other.y
  
  def length() = pow(this.dot(this), 0.5).toFloat
  
  def normalize(): Vec2 = this / length
  
  def cross(other: Vec2): Float = this.x * other.y - this.y * other.x
}

object Vec3 {
  def apply(x: Float, y: Float, z: Float) = {
    new Vec3(x, y, z)
  }
  
  def apply() = {
    new Vec3(0.0f, 0.0f, 0.0f)
  }
}

class Vec3(var x: Float, var y: Float, var z: Float) {
  def +(other: Vec3) = 
    new Vec3(this.x + other.x, this.y + other.y, this.z + other.z)
  
  def -(other: Vec3) = 
    new Vec3(this.x - other.x, this.y - other.y, this.z - other.z)
  
  def *(other: Vec3) =
    new Vec3(this.x * other.x, this.y * other.y, this.z * other.z)
  
  def *(value: Float) =
    new Vec3(this.x * value, this.y * value, this.z * value)
  
  def /(other: Vec3) =
    new Vec3(this.x / other.x, this.y / other.y, this.z / other.z)
  
  def /(value: Float) =
    new Vec3(this.x / value, this.y / value, this.z / value)
  
  def dot(other: Vec3): Float = 
    this.x * other.x + this.y * other.y + this.z * other.z
    
  def cross(other: Vec3) = 
   new Vec3(
     this.y * other.z - this.z * other.y,
     this.z * other.x - this.x * other.z,
     this.x * other.y - this.y * other.x
   )
  
  def length() = pow(this.dot(this), 0.5).floatValue()
  
  def normalize() = this / length
  
  def xy() = Vec2(this.x, this.y)
  
  override def toString() = {
    "x: " + x + " y: " + y + " z: " + z
  }
}

object Vec4 {
  def apply(x: Float, y: Float, z: Float, w: Float) = {
    new Vec4(x, y, z, w)
  }
  
  def apply() = {
    new Vec4(0.0f, 0.0f, 0.0f, 0.0f)
  }
  
  def apply(vector: Vec3, w: Float) = {
    new Vec4(vector.x, vector.y, vector.z, w)
  }
}

class Vec4(var x: Float, var y: Float, var z: Float, var w: Float) {
  def +(other: Vec4) = 
    new Vec4(this.x + other.x, this.y + other.y, this.z + other.z, this.w + other.w)
  
  def -(other: Vec4) = 
    new Vec4(this.x - other.x, this.y - other.y, this.z - other.z, this.w - other.w)
  
  def *(other: Vec4): Vec4 =
    new Vec4(this.x * other.x, this.y * other.y, this.z * other.z, this.w * other.w)
  
  def *(value: Float): Vec4 =
    new Vec4(this.x * value, this.y * value, this.z * value, this.w * value)
  
  def /(other: Vec4) =
    new Vec4(this.x / other.x, this.y / other.y, this.z / other.z, this.w / other.w)
  
  def /(value: Float) =
    new Vec4(this.x / value, this.y / value, this.z / value, this.w / value)
  
  def dot(other: Vec4): Float = 
    this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w
  
  def length() = pow(this.dot(this), 0.5).floatValue()
  
  def normalize() = this / length
  
  def xy() = Vec2(this.x, this.y)
  
  def xyz() = Vec3(this.x, this.y, this.z)
  
  def apply(index: Int): Float = {
    index match {
      case 0 => {
        this.x
      }
      case 1 => {
        this.y
      }
      case 2 => {
        this.z
      }
      case 3 => {
        this.w
      }
      case default => {
        0.0f
      }
    }
  }
  
  def update(index: Int, value: Float) = {
    index match {
      case 0 => {
        this.x = value
      }
      case 1 => {
        this.y = value
      }
      case 2 => {
        this.z = value
      }
      case 3 => {
        this.w = value
      }
      case default => {
        0.0f
      }
    }
  }
}