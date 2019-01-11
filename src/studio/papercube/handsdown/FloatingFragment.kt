package studio.papercube.handsdown

import javafx.event.Event
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.effect.BlurType
import javafx.scene.effect.DropShadow
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
//        currentStage?.initStyle(StageStyle.UTILITY)
        root.apply {
            background = Background(BackgroundFill(COLOR_LIGHT_GREY, CornerRadii(5.0), Insets(10.0)))
            padding = Insets(30.0)
            center = label("轻触以打开"){
                textFill = Color.BLACK
            }

            initListeners()
        }

        owner = params["owner"] as? DisplayView
    }

    fun postInstantiation() {
//        println("$currentWindow, $currentStage, $modalStage")
        val visualBounds = Screen.getPrimary().visualBounds

        currentWindow?.apply {
            scene?.fill = null //crucial in making the window transparent
            val xCenter = (visualBounds.width - width) / 2
            val yPos = height / 2 + 10.0
            x = xCenter
            y = yPos
        }

        modalStage?.apply {
            isAlwaysOnTop = true
            setOnCloseRequest(Event::consume)
        }

        val effectDropShadow = DropShadow(BlurType.GAUSSIAN, Color.BLACK, 7.0, 0.0, 2.0, 2.0)
        root.effect = effectDropShadow
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
            requestFocus()
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