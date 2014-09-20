package o1.math

object Mat4 {
  def apply() = {
    new Mat4(
      0.0f, 0.0f, 0.0f, 0.0f,
      0.0f, 0.0f, 0.0f, 0.0f,
      0.0f, 0.0f, 0.0f, 0.0f,
      0.0f, 0.0f, 0.0f, 0.0f)
  }
  
  def identity() = {
    new Mat4(
      1.0f, 0.0f, 0.0f, 0.0f,
      0.0f, 1.0f, 0.0f, 0.0f,
      0.0f, 0.0f, 1.0f, 0.0f,
      0.0f, 0.0f, 0.0f, 1.0f)
  }
}

class Mat4(
  x0: Float, y0: Float, z0: Float, w0: Float,
  x1: Float, y1: Float, z1: Float, w1: Float,
  x2: Float, y2: Float, z2: Float, w2: Float,
  x3: Float, y3: Float, z3: Float, w3: Float) {

  var storage = new Array[Vec4](4)
    storage(0) = Vec4(x0, x1, x2, x3)
    storage(1) = Vec4(y0, y1, y2, y3)
    storage(2) = Vec4(z0, z1, z2, z3)
    storage(3) = Vec4(w0, w1, w2, w3)

  def apply(index: Int) = {
    storage(index)
  }

  def row(index: Int) = {
    Vec4(
      storage(0)(index), 
      storage(1)(index), 
      storage(2)(index), 
      storage(3)(index))
  }
  
  def *(other: Mat4): Mat4 = {
    val x0 = this.row(0).dot(other(0))
    val x1 = this.row(1).dot(other(0))
    val x2 = this.row(2).dot(other(0))
    val x3 = this.row(3).dot(other(0))

    val y0 = this.row(0).dot(other(1))
    val y1 = this.row(1).dot(other(1))
    val y2 = this.row(2).dot(other(1))
    val y3 = this.row(3).dot(other(1))

    val z0 = this.row(0).dot(other(2))
    val z1 = this.row(1).dot(other(2))
    val z2 = this.row(2).dot(other(2))
    val z3 = this.row(3).dot(other(2))

    val w0 = this.row(0).dot(other(3))
    val w1 = this.row(1).dot(other(3))
    val w2 = this.row(2).dot(other(3))
    val w3 = this.row(3).dot(other(3))

    new Mat4(
      x0, y0, z0, w0,
      x1, y1, z1, w1,
      x2, y2, z2, w2,
      x3, y3, z3, w3)
  }
  
  def *(other: Vec4): Vec4 = {
    val x = this.row(0).dot(other)
    val y = this.row(1).dot(other)
    val z = this.row(2).dot(other)
    val w = this.row(3).dot(other)
    Vec4(x, y, z, w)
  }
  
  def update(index: Int, vector: Vec4) = {
    storage(index) = vector
  }
  
  override def toString() = {
    "[" + this(0)(0) + "][" + this(1)(0) + "][" + this(2)(0) + "][" + this(3)(0) + "]" + "\n" +
    "[" + this(0)(1) + "][" + this(1)(1) + "][" + this(2)(1) + "][" + this(3)(1) + "]" + "\n" +
    "[" + this(0)(2) + "][" + this(1)(2) + "][" + this(2)(2) + "][" + this(3)(2) + "]" + "\n" +
    "[" + this(0)(3) + "][" + this(1)(3) + "][" + this(2)(3) + "][" + this(3)(3) + "]"
  }
  
}