package day10

import printMatrix
import readInput
import kotlin.math.abs

val input = readInput("day10.txt")
    .map { it.split(" ") }

val cycleValues =
    input.fold(listOf(Pair(1, 1))) { acc, strings ->
        when (strings[0]) {
            "noop" -> acc + listOf(Pair(acc.last().first + 1, acc.last().second))
            "addx" -> acc + listOf(
                Pair(acc.last().first + 1, acc.last().second),
                Pair(acc.last().first + 2, acc.last().second + strings[1].toInt())
            )
            else -> error("Invalid operation")
        }
    }.toMap()

fun solveA(): Long {
    val cyclesToCheck = 20..220 step 40
    return cyclesToCheck.sumOf {
        val result = cycleValues[it]!! * it.toLong()
        println(result)
        result
    }
}

fun solveB(): List<List<String>> {
    return cycleValues.map {
        if (abs(it.value - ((it.key-1) % 40)) < 2) "#" else "."
    }.chunked(40)
}

fun main() {
    println("Answer A: ${solveA()}")
    println("Answer B:")
    printMatrix(solveB())
}
