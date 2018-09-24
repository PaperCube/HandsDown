package studio.papercube.handsdown

import java.io.File
import java.util.stream.Collectors

object ShufflerBuilder {
    fun fromFile(): Shuffler<PersonItem> {
        val list = File("list").bufferedReader(Charsets.UTF_8)
                .lines()
                .filter(String::isNotBlank)
                .map(PersonItem.Companion::parse)
                .collect(Collectors.toList())
        return if (list.hasIdenticalWeights()) UnweightedShuffler(list) else WeightedShuffler(list)
    }

    private fun List<PersonItem>.hasIdenticalWeights(): Boolean {
        var first = true
        var lastWeight = 0.0
        for (p in this) {
            if (first) {
                lastWeight = p.weight
                first = false
            } else if (p.weight != lastWeight) return false
        }
        return true
    }
}

fun <T : Shuffler<*>> T.requireNonEmpty(): T = apply {
    if (!hasNext()) throw EmptyShufflerException()
}