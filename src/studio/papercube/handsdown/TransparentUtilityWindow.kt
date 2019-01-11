package studio.papercube.handsdown

import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.BorderPane
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import tornadofx.Fragment

class TransparentUtilityWindow() : Fragment() {
    override val root = BorderPane()

    init{

    }

    fun postInstantiation(){
        currentWindow?.scene?.fill = Color.TRANSPARENT
        modalStage?.opacity = 0.0
        root.background = Background(BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY))
    }
}