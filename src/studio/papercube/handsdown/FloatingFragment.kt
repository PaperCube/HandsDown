package studio.papercube.handsdown

import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.BorderPane
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import javafx.stage.Screen
import tornadofx.Fragment
import tornadofx.label

class FloatingFragment : Fragment() {
    override val root = BorderPane()
    private val owner:DisplayView?

    private var mousePressOffsetX = 0.0
    private var mousePressOffsetY = 0.0

    init {
        currentWindow?.scene?.fill = null
        root.apply {
            background = Background(BackgroundFill(COLOR_INDIGO, CornerRadii(20.0), Insets.EMPTY))
            padding = Insets(20.0)
            center = label("点按以打开"){
                textFill = Color.WHITE
            }

            initListeners()
        }

        owner = params["owner"] as? DisplayView
    }

    fun initialize() {
//        println("$currentWindow, $currentStage, $modalStage")
        val visualBounds = Screen.getPrimary().visualBounds

        currentWindow?.apply {
            scene?.fill = null //crucial in making the window transparent
            val xCenter = (visualBounds.width - width) / 2
            val yPos = height / 2 + 10.0
            x = xCenter
            y = yPos
        }

        modalStage?.isAlwaysOnTop = true
    }

    private fun onMouseDragged(mouseEvent: MouseEvent) {
//        println("onDrag")
        currentWindow?.let {
            it.x = mouseEvent.screenX - mousePressOffsetX
            it.y = mouseEvent.screenY - mousePressOffsetY
        }
    }

    private fun onMousePressed(mouseEvent: MouseEvent) {
        mousePressOffsetX = mouseEvent.sceneX
        mousePressOffsetY = mouseEvent.sceneY
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onMouseClickedNoDrag(mouseEvent: MouseEvent) {
        find(DisplayView::class).currentStage?.apply {
            show()
            isIconified = false
        }
    }

    private fun Node.initListeners(){
        setOnMouseDragged { onMouseDragged(it) }
        setOnMousePressed { onMousePressed(it) }
        setOnMouseClicked {
            if (it.isStillSincePress) {
                onMouseClickedNoDrag(it)
            }
        }
    }

    override fun onDock() {
        root.requestFocus()
    }
}