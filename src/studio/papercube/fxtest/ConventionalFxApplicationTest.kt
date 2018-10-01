package studio.papercube.fxtest

import javafx.application.Application
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.stage.Stage
import javafx.stage.StageStyle
import tornadofx.add

class ConventionalFxApplicationTest : Application() {
    override fun start(primaryStage: Stage?) {
        val displayScene = DisplayScene(RootPane())
        primaryStage!!
        primaryStage.initStyle(StageStyle.TRANSPARENT)
        primaryStage.scene = displayScene
        primaryStage.show()
    }
}

class DisplayScene(parent: Parent) : Scene(parent) {
    init {
        fill = null
    }
}

private class RootPane() : FlowPane() {
    init {
//        add(Circle(50.0, Color.RED))
//        add()
        children.add(Circle(50.0, Color.RED))
    }
}