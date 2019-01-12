package studio.papercube.fxtest

import studio.papercube.handsdown.PersonItem
import studio.papercube.handsdown.ShufflerBuilder
import java.util.stream.Collectors
import java.util.stream.StreamSupport
import kotlin.reflect.jvm.jvmName

fun main(args: Array<String>) {
    val shuffler = ShufflerBuilder.fromFile()
    val shufflerSize = shuffler.size
    println("Shuffler type: ${shuffler::class.jvmName}. size = $shufflerSize")
    val list = StreamSupport.stream(shuffler.spliterator(), false)
            .limit(100_000_000)
            .collect(Collectors.toList())
    val total = list.size
    val result: MutableMap<String, Long> = list.stream().parallel()
            .collect(Collectors.groupingBy(PersonItem::name, Collectors.counting()))
    for((name, freq) in result){
        println("$name $freq / $total (${shufflerSize * freq.toDouble() / total})")
    }
}