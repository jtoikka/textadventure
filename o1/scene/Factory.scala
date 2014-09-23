package o1.scene


/**
 * The Factory object is a collection of functions that can be used to create
 * entities with various characteristics. 
 */

object Factory {
  
  def createCamera() = {
    var camera = new Entity()
    var spatialComp = new SpatialComponent()
    
    camera.addComponent(spatialComp)
    camera
  }
  
  def createSphere() = {
    var sphere = new Entity()
    var spatialComp = new SpatialComponent()
    
    sphere.addComponent(spatialComp)
    
    var renderComp = new RenderComponent("sphere")
    
    sphere.addComponent(renderComp)
    sphere
  }
  
  def createCube() = {
    var sphere = new Entity()
    var spatialComp = new SpatialComponent()
    
    sphere.addComponent(spatialComp)
    
    var renderComp = new RenderComponent("cube")
    
    sphere.addComponent(renderComp)
    sphere
  }
  
  def createMonkey() = {
    var monkey = new Entity()
    var spatialComp = new SpatialComponent()
    
    monkey.addComponent(spatialComp)
    
    var renderComp = new RenderComponent("cube")
    
    monkey.addComponent(renderComp)
    monkey
  }
    def createPlate() = {
    var monkey = new Entity()
    var spatialComp = new SpatialComponent()
    
    monkey.addComponent(spatialComp)
    
    var renderComp = new RenderComponent("plate")
    
    monkey.addComponent(renderComp)
    monkey
  }
}