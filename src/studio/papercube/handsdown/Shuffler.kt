package studio.papercube.handsdown

import java.util.*
import kotlin.collections.ArrayList

private fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    Collections.swap(this, index1, index2)
}

//private fun <T> Array<T>.swap(i: Int, j: Int) {
//    if (i == j) return
//    val a = this[i]
//    this[i] = this[j]
//    this[j] = a
//}

interface Shuffler<out T> : Iterator<T>, Iterable<T> {
    override fun iterator(): Iterator<T> {
        return this
    }
}

class UnweightedShuffler<out T>(items: List<T>) : Shuffler<T> {
    private val list = ArrayList(items)
    private val size = items.size

    private var index = 0

    init {
        list.shuffle()
    }

    override fun hasNext(): Boolean {
        return size != 0
    }

    override fun next(): T {
        if (index >= size) {
            list.shuffle()
            index = 0
        }
        return list[index++]
    }
}

class WeightedShuffler<out T : Weighed>(items: List<T>) : Shuffler<T> {
    private val list = ArrayList(items)
    private val size = items.size
    private val weightRange = DoubleArray(items.size + 1)
    private val weightSum: Double

    private val random = Random()

    private fun nextRandomPoint() = random.nextDouble() * weightSum

    init {
        for (i in 0 until size) {
            weightRange[i + 1] = weightRange[i] + list[i].weight
        }
        weightSum = weightRange[size]
    }

    override fun hasNext(): Boolean {
        return size != 0
    }

    override fun next(): T {
        return list[binarySearch(nextRandomPoint())]
    }

    private fun binarySearch(v: Double): Int {
        var l = 0
        var r = size
        while (l + 1 != r) {
            val mid = (l + r) / 2
            if (weightRange[mid] <= v && v <= weightRange[mid + 1]) return mid
            if (v > weightRange[mid]) {
                l = mid
            } else r = mid
        }
        return l
    }
}