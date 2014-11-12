package o1.adventure.ui

import scala.collection.mutable.Map
import scala.swing._
import scala.swing.event._
import scala.swing.GridBagPanel.Anchor._
import scala.swing.GridBagPanel.Fill
import javax.swing.UIManager
import javax.swing.Timer
import o1.adventure.Adventure
import java.awt.Color
import scala.swing.Font
import o1.event.Listener
import o1.event.EventType._
import java.awt.Font
import java.io.File

////////////////// NOTE TO STUDENTS //////////////////////////
// For the purposes of our course, it's not necessary    
// that you understand or even look at the code in this file.
//////////////////////////////////////////////////////////////

/**
 * The singleton object `AdventureGUI` represents a GUI-based version of the Adventure
 * game application. The object serves as a possible entry point for the game, and can
 * be run to start up a user interface that operates in a separate window. The GUI reads
 * its input from a text field and displays information about the game world in uneditable
 * text areas.
 *
 * '''NOTE TO STUDENTS: In this course, you don't need to understand how this object works
 * or can be used, apart from the fact that you can use this file to start the program.'''
 *
 * @see [[AdventureTextUI]]
 */
object AdventureGUI extends SimpleSwingApplication with Listener {
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)
  var keyMap = Map[scala.swing.event.Key.Value, Boolean](
    (Key.W, false), (Key.S, false),
    (Key.A, false), (Key.D, false),
    (Key.Up, false), (Key.Down, false),
    (Key.Right, false), (Key.Left, false),
    (Key.M, false), (Key.N, false),
    (Key.Enter, false), (Key.Escape, false),
    (Key.I, false), (Key.Q, false),
    (Key.Space, false))

  def top = new MainFrame {

    // Access to the internal logic of the application: 
    val game = new Adventure()

    // Components: 

    val renderArea = new TextArea(game.screenHeight, game.screenWidth) {
      editable = false
      wordWrap = false
      lineWrap = false

      background = Color.BLACK
      foreground = Color.WHITE

      font = Font.createFont(Font.TRUETYPE_FONT, new File("data/font/unifont-7.0.06.ttf"))
      font = font.deriveFont(12f)
      
//      font = new Font(Font.MONOSPACED, 0, 12)

      listenTo(keys)
    }
    this.listenTo(renderArea.keys)
    val turnCounter = new Label

    // Events: 

    renderArea.reactions += {
      case KeyPressed(_, Key.Escape, _, _) =>
        keyMap(Key.Escape) = true
      case KeyReleased(_, Key.Escape, _, _) =>
        keyMap(Key.Escape) = false
      case KeyPressed(_, Key.Enter, _, _) =>
        keyMap(Key.Enter) = true
      case KeyReleased(_, Key.Enter, _, _) =>
        keyMap(Key.Enter) = false
      case KeyPressed(_, Key.Q, _, _) =>
        keyMap(Key.Q) = true
      case KeyReleased(_, Key.Q, _, _) =>
        keyMap(Key.Q) = false
      case KeyPressed(_, Key.W, _, _) =>
        keyMap(Key.W) = true
      case KeyReleased(_, Key.W, _, _) =>
        keyMap(Key.W) = false
      case KeyPressed(_, Key.S, _, _) =>
        keyMap(Key.S) = true
      case KeyReleased(_, Key.S, _, _) =>
        keyMap(Key.S) = false
      case KeyPressed(_, Key.A, _, _) =>
        keyMap(Key.A) = true
      case KeyReleased(_, Key.A, _, _) =>
        keyMap(Key.A) = false
      case KeyPressed(_, Key.D, _, _) =>
        keyMap(Key.D) = true
      case KeyReleased(_, Key.D, _, _) =>
        keyMap(Key.D) = false
      case KeyPressed(_, Key.Up, _, _) =>
        keyMap(Key.Up) = true
      case KeyReleased(_, Key.Up, _, _) =>
        keyMap(Key.Up) = false
      case KeyPressed(_, Key.Down, _, _) =>
        keyMap(Key.Down) = true
      case KeyReleased(_, Key.Down, _, _) =>
        keyMap(Key.Down) = false
      case KeyPressed(_, Key.Left, _, _) =>
        keyMap(Key.Left) = true
      case KeyReleased(_, Key.Left, _, _) =>
        keyMap(Key.Left) = false
      case KeyPressed(_, Key.Right, _, _) =>
        keyMap(Key.Right) = true
      case KeyReleased(_, Key.Right, _, _) =>
        keyMap(Key.Right) = false
      case KeyPressed(_, Key.M, _, _) =>
        keyMap(Key.M) = true
      case KeyReleased(_, Key.M, _, _) =>
        keyMap(Key.M) = false
      case KeyPressed(_, Key.N, _, _) =>
        keyMap(Key.N) = true
      case KeyReleased(_, Key.N, _, _) =>
        keyMap(Key.N) = false
      case KeyPressed(_, Key.I, _, _) =>
        keyMap(Key.I) = true
      case KeyReleased(_, Key.I, _, _) =>
        keyMap(Key.I) = false
      case KeyPressed(_, Key.Space, _, _) =>
        keyMap(Key.Space) = true
      case KeyReleased(_, Key.Space, _, _) =>
        keyMap(Key.Space) = false

    }
    var time = System.currentTimeMillis()
    var timeExtra = 0.0

    val updatePeriod = 33 //33

    var timer = new Timer(updatePeriod, new java.awt.event.ActionListener {
      def actionPerformed(e: java.awt.event.ActionEvent) = {
        var newTime = System.currentTimeMillis()
        var delta = newTime - time + timeExtra
//        println(1.0 / (newTime - time) * 1000) //fps
        var numUpdates = (delta / updatePeriod).toInt
        timeExtra = delta - numUpdates * updatePeriod
        time = newTime
        for (i <- 0 until numUpdates)
          update(updatePeriod / 100.0f)
      }
    })

    timer.start()

    // Layout: 
    this.contents = new GridBagPanel {
      layout += renderArea -> new Constraints(1, 0, 1, 1, 1, 1, NorthWest.id, Fill.Both.id, new Insets(5, 5, 5, 5), 0, 0)
    }

    // Menu:

    this.menuBar = new MenuBar {
      contents += new Menu("Program") {
        val quitAction = Action("Quit") { dispose() }
        contents += new MenuItem(quitAction)
      }
    }

    // Set up the initial state of the GUI:

    this.title = game.title
    //    this.updateInfo(this.game.welcomeMessage)
    this.location = new Point(50, 50)
    this.pack()
    this.renderArea.requestFocusInWindow()

    def update(time: Double) = {
      this.game.update(time, keyMap)
      updateInfo("update")
      handleEvents(time.toFloat)
    }

    def updateInfo(info: String) = {
      this.title = game.title
      this.renderArea.text = game.display
    }
  }
  
  eventHandlers = scala.collection.immutable.Map(
    (E_SYSTEM_EXIT, (event, delta) => {
      dispose()
    }))

  def dispose(): Unit = {
    println("Application Exit!")
    this.quit()
  }
}  
  
