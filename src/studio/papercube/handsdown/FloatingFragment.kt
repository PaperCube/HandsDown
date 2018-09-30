package studio.papercube.handsdown

import javafx.geometry.Insets
import javafx.scene.Node
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

    init {
        root.apply {
            background = Background(BackgroundFill(COLOR_INDIGO, CornerRadii.EMPTY, Insets.EMPTY))
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
            val xCenter = (visualBounds.width - width) / 2
            val yPos = height / 2 + 10.0
            x = xCenter
            y = yPos
        }

        modalStage?.isAlwaysOnTop = true
    }

    private fun Node.initListeners(){
        setOnMouseClicked {
            find(DisplayView::class).currentStage?.apply {
                show()
                isIconified = false
            }
        }
    }

    override fun onDock() {
        root.requestFocus()
    }
}