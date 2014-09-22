package o1.scene


/**
 * The Factory object is a collection of functions that can be used to create
 * entities with various characteristics. 
 */

object Factory2D {
  
  def createTestRect() = {
    var rect = new Entity()
    
    var spatialComp = new SpatialComponent() 
    rect.addComponent(spatialComp)
    
    var rendComp2D = new RenderComponent2D("testRectangle")
    rect.addComponent(rendComp2D)
    
    rect
  }
  
  def createTestRect2() = {
    var rect = new Entity()
    
    var spatialComp = new SpatialComponent() 
    rect.addComponent(spatialComp)
    
    var rendComp2D = new RenderComponent2D("testRectangle2")
    rect.addComponent(rendComp2D)
    
    rect
  }
  
  def createTestTri() = {
    var tri = new Entity()
    
    var spatialComp = new SpatialComponent() 
    tri.addComponent(spatialComp)
    
    var rendComp2D = new RenderComponent2D("testTriangle")
    tri.addComponent(rendComp2D)
    
    tri
  }
  
}