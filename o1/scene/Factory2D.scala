package o1.scene

import o1.adventure.render2D._
import o1.adventure.render.ResourceManager
import java.util.UUID.randomUUID
/**
 * The Factory object is a collection of functions that can be used to create
 * entities with various characteristics. 
 */

object Factory2D {
  
  def createRectangle(h: Int,w: Int,defFill: Boolean) = {
    var ent = new Entity()
    var rect = new Rectangle2D(h,w,defFill)
    val uuid = randomUUID().toString()
    
    ResourceManager.shapes(uuid) = rect
    
    var spatialComp = new SpatialComponent() 
    ent.addComponent(spatialComp)
    
    var rendComp2D = new RenderComponent2D(uuid)
    ent.addComponent(rendComp2D)
    ent
  }
  
  def createTextRectangle(h: Int,w: Int,defFill: Boolean, 
      text: String, offsetX: Int, offsetY: Int) = {
    
    var ent = new Entity()
    var rect = new TextRect2D(new Rectangle2D(h,w,defFill),text)
    rect.offX = offsetX
    rect.offY = offsetY
    
    val uuid = randomUUID().toString()
    
    ResourceManager.shapes(uuid) = rect
    
    var spatialComp = new SpatialComponent() 
    ent.addComponent(spatialComp)
    
    var rendComp2D = new RenderComponent2D(uuid)
    ent.addComponent(rendComp2D)
    ent
  }
  
  def createTextRectangle(textRect: TextRect2D) = {
    var ent = new Entity()
    var rect = textRect
    
    val uuid = randomUUID().toString()
    
    ResourceManager.shapes(uuid) = rect
    
    var spatialComp = new SpatialComponent() 
    ent.addComponent(spatialComp)
    
    var rendComp2D = new RenderComponent2D(uuid)
    ent.addComponent(rendComp2D)
    ent
  }
  
    def createRectangle(rect: Rectangle2D) = {
    var ent = new Entity()
    
    val uuid = randomUUID().toString()
    
    ResourceManager.shapes(uuid) = rect
    
    var spatialComp = new SpatialComponent() 
    ent.addComponent(spatialComp)
    
    var rendComp2D = new RenderComponent2D(uuid)
    ent.addComponent(rendComp2D)
    ent
  }
    
  def createTriangle(tri: Triangle2D) = {
    var ent = new Entity()
    
    val uuid = randomUUID().toString()
    
    ResourceManager.shapes(uuid) = tri
    
    var spatialComp = new SpatialComponent() 
    ent.addComponent(spatialComp)
    
    var rendComp2D = new RenderComponent2D(uuid)
    ent.addComponent(rendComp2D)
    ent
  }
}