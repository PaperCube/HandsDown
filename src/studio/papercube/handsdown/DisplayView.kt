package studio.papercube.handsdown

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.effect.GaussianBlur
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.stage.Screen
import javafx.stage.Window
import javafx.util.Duration
import tornadofx.*
import java.util.concurrent.atomic.AtomicReference


class DisplayView : View() {
    override val root = BorderPane()
    private var shufflerRef: AtomicReference<Shuffler<Named>?> = AtomicReference(null)
    var shuffler: Shuffler<Named>?
        get() = shufflerRef.get()
        set(value) = shufflerRef.set(value)

    private val upperStatusText = Text().lowerTextStyled()
    private val lowerStatusText = Text("点按以切换. 按Ctrl + R以重载列表").lowerTextStyled()
    private val centerTextContainer = HBox()
    //    private val centerText = Text("--")
    private var centerText: String = "--"
        set(value) {
            field = value
            centerTextContainer.makeText(value, centerTextDefaultRefreshOpacity)
        }
    private var centerTextDefaultRefreshOpacity = 1.0

    //    private val defaultStrengthGaussianBlur = GaussianBlur(20.0)
//    private val emptyGaussianBlur = GaussianBlur(0.0)
    private val gaussianBlur = GaussianBlur(0.0)
    private val maxGaussianBlurRadius = 20.0

    private val WINDOW_MIN_WIDTH = 1024
    private val WINDOW_MIN_HEIGHT = 576

    private fun HBox.makeText(string: String, initialOpacity: Double = centerTextDefaultRefreshOpacity) = apply {
        clear()
        for (c in string) {
            this += Text(c.toString()).styleLargeOpaque(initialOpacity)
        }
    }

    private fun Text.styleLargeOpaque(initialOpacity: Double) = apply {
        fill = Color.WHITE
        font = Font.font(200.0)
        opacity = initialOpacity
    }

    init {
        setWindowMinSize(1024, 576)
        currentWindow?.center()
        with(root) {
            background = Background(BackgroundFill(COLOR_INDIGO, CornerRadii.EMPTY, Insets.EMPTY))
            isFocusTraversable = true /* make sure this node can respond to key events properly */
            initListeners()
            center = borderpane {
                center = centerTextContainer.makeText(centerText).apply {
                    alignment = Pos.CENTER
                    effect = gaussianBlur
                }
            }

            bottom = vbox {
                alignment = Pos.BASELINE_CENTER
                padding = Insets(10.0)
//                add(upperStatusText)
//                add(lowerStatusText)
                this += upperStatusText
                this += lowerStatusText
            }
        }

        reloadList()

    }

    private fun Window.center() {
        val screenBounds = Screen.getPrimary().visualBounds
        x = (screenBounds.width - WINDOW_MIN_WIDTH) / 2
        y = (screenBounds.height - WINDOW_MIN_HEIGHT) / 2
    }

    private fun reloadList() {
        try {
            shuffler = ShufflerBuilder.fromFile()
            upperStatusText.text = "已刷新"
//        upperStatusText.opacity = Math.random() * 1.0
            timeline {
                cycleCount = 2
                isAutoReverse = true
                keyframe(Duration.ZERO) {
                    keyvalue(upperStatusText.opacityProperty(), 0.0)
                }
                keyframe(0.3.seconds) {
                    keyvalue(upperStatusText.opacityProperty(), 1.0)
                }
                keyframe(1.seconds) {}
            }
        } catch (e: Exception) {
            showReadFailureDialog(e)
        }
    }

    private fun showReadFailureDialog(e: Throwable) {
        Alert(Alert.AlertType.ERROR, "无法读取列表", ButtonType.OK).apply {
            width = 600.0
            headerText = "读取列表时发生问题"
            this.dialogPane.expandableContent = gridpane {
                padding = Insets(10.0)
                row {
                    useMaxWidth = true
                    label(
                            "无法读取列表。请将以UTF-8编码的逗号分隔符文件命名为list (无扩展名) 放在工作目录下。\n" +
                                    "如果您通过双击打开此应用，请把列表和应用程序本身放在同一目录下。"
                    )
                }
                row {
                    textarea(e.toString()) {
                        useMaxSize = true
                        isEditable = false
                        isWrapText = true
                    }
                }
                vgap = 10.0
            }
        }.showAndWait()
    }

    private fun forwardToNext() {
        shuffler?.let {
            val nextName = it.next().name
            val fadeDuration = 0.3
            val letterDelay = 0.1
            timeline {
                /* ======== FADE OUT ========= */
                val nodes = centerTextContainer.children.shuffled()
                keyframe(0.seconds) {
                    keyvalue(gaussianBlur.radiusProperty(), 0)
                }
                keyframe(((nodes.size * letterDelay + fadeDuration) / 2).seconds) {
                    keyvalue(gaussianBlur.radiusProperty(), maxGaussianBlurRadius)
                }
                for ((index: Int, node: Node) in nodes.withIndex()) {
                    keyframe(0.seconds) {
                        keyvalue(node.opacityProperty(), 1.0)
//                        keyvalue(node.effectProperty(), )
                    }
                    val delay = 0 + index * letterDelay
                    keyframe(delay.seconds) {
                        keyvalue(node.opacityProperty(), 1.0)
                    }
                    val fadeOutTime = delay + fadeDuration
                    keyframe(fadeOutTime.seconds) {
                        keyvalue(node.opacityProperty(), 0.0)
                    }
                }
            }.setOnFinished {
                /* ======== FADE IN ========= */
                centerTextDefaultRefreshOpacity = 0.0
                centerText = nextName
                timeline {
                    val nodes = centerTextContainer.children.shuffled()
                    keyframe(0.seconds) {
                        keyvalue(gaussianBlur.radiusProperty(), maxGaussianBlurRadius)
                    }
                    keyframe((nodes.size * letterDelay + fadeDuration + 0.35).seconds) {
                        keyvalue(gaussianBlur.radiusProperty(), 0)
                    }
                    for ((index, node) in nodes.withIndex()) {
                        keyframe(0.seconds) {
                            keyvalue(node.opacityProperty(), 0.0)
                        }
                        val delay = 0 + index * letterDelay
                        keyframe(delay.seconds) {
                            keyvalue(node.opacityProperty(), 0.0)
                        }
                        val fadeOutTime = delay + fadeDuration
                        keyframe(fadeOutTime.seconds) {
                            keyvalue(node.opacityProperty(), 1.0)
                        }
                    }
                }
            }
        }
    }

    private fun Node.initListeners() {
        setOnMouseClicked eventHandler@{ mouseEvent: MouseEvent? ->
            mouseEvent ?: return@eventHandler
            when (mouseEvent.button) {
                MouseButton.PRIMARY -> forwardToNext()
                MouseButton.SECONDARY -> reloadList()
                else -> {
                    // ignore
                }
            }
        }

        setOnKeyPressed eventHandler@{ keyEvent: KeyEvent? ->
            keyEvent ?: return@eventHandler
            with(keyEvent) {
                when {
                    isControlDown && code == KeyCode.R -> reloadList()
                    code in arrayOf(KeyCode.SPACE, KeyCode.ENTER) -> forwardToNext()
                }
            }
        }
    }

    private fun Text.lowerTextStyled() = apply {
        fill = Color.LIGHTGRAY
    }

    override fun onDock() {
        /*
         * let root obtain focus in order that key events can be received
         */
        root.requestFocus()
    }
}