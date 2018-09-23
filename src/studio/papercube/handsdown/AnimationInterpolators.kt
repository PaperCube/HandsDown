package studio.papercube.handsdown

import javafx.animation.Interpolator
import kotlin.math.pow

object QuadraticEaseOutInterpolator:Interpolator(){
    override fun curve(t: Double): Double {
        return 1.0 - (1.0 - t).pow(2)
    }
}

object QuarticEaseOutInterpolator:Interpolator(){
    override fun curve(t: Double): Double {
        return 1.0 - (1.0 - t).pow(4)
    }
}