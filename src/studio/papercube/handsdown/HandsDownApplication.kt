package studio.papercube.handsdown

import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.stage.Stage
import tornadofx.App
import tornadofx.alert
import java.io.File
import java.io.PrintStream
import java.lang.ref.WeakReference
import java.nio.channels.Channel
import java.nio.channels.FileLock
import java.time.LocalDateTime

class HandsDownApplication : App(DisplayView::class) {
    private var lockPrintStream: PrintStream? = null
    init {

    }

    override fun start(stage: Stage) {
        try {
            if (!lockSingleInstance()) {
                alert(Alert.AlertType.WARNING, "已经有另一个使用相同名单的点名器正在运行", null, ButtonType.CLOSE)
            } else {
                super.start(stage)
            }
        } catch (e: Exception) {
            alert(
                    Alert.AlertType.ERROR,
                    "启动HandsDown失败",
                    e.toString(),
                    ButtonType.CLOSE
            )
        }
    }

    private fun lockSingleInstance(): Boolean {
        val lockFile = File("${ShufflerBuilder.shufflerDataSourceFileName}.handsdowntmplck").absoluteFile
        lockFile.parentFile.mkdirs()
        lockFile.deleteOnExit()
        val fos = lockFile.outputStream()
        val channel = fos.channel
        val lock: FileLock? = channel.tryLock()
        val ps = PrintStream(fos)

        // Here a reference of output stream to the lock file must be preserved and strongly accessible,
        // because if it's reference is not held nor accessible, chances are that the output stream will be closed
        // by finalization triggered by garbage collection.
        lockPrintStream = ps

        ps.println(LocalDateTime.now())
        if (lock == null) {
            println("Cannot acquire file lock")
            return false
        }
        println("Acquired lock $lock on $lockFile")
        return true
    }

    override fun stop() {
        super.stop()
        lockPrintStream?.close()
    }
}