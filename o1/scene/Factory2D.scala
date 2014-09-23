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
  
  def createTextRectangle(h: Int,w: Int,defFill: Boolean, text: String) = {
    var ent = new Entity()
    var rect = new TextRect2D(new Rectangle2D(h,w,defFill),text)
    
    val uuid = randomUUID().toString()
    
    ResourceManager.shapes(uuid) = rect
    
    var spatialComp = new SpatialComponent() 
    ent.addComponent(spatialComp)
    
    var rendComp2D = new RenderComponent2D(uuid)
    ent.addComponent(rendComp2D)
    ent
  }
}