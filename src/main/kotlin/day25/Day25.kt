package day25

import readInput
import kotlin.math.pow

val input = readInput("day25.txt")

fun getFactor(char: Char): Int {
    return when (char) {
        '0' -> 0
        '1' -> 1
        '2' -> 2
        '=' -> -2
        '-' -> -1
        else -> error("invalid SNAFU number: $char")
    }
}

fun String.toLongFromSnafu(): Long {
    return this.foldIndexed(0L) { index, acc, c ->
        val base = 5.toDouble().pow(this.length - index - 1).toLong()
        acc + (getFactor(c) * base)
    }
}

fun Long.toSnafuString(): String {
    val base5 = this.toString(5)
    val result: ArrayDeque<String> = ArrayDeque()
    var carryOver = 0
    for (i in base5.indices.reversed()) {
        val value = base5[i].digitToInt() + carryOver
        if (value < 3) {
            result.addFirst(value.toString())
            carryOver = 0
        } else if(value < 5) {
            carryOver = 1
            result.addFirst(if (value == 3) "=" else "-")
        } else {
            carryOver = 1
            result.addFirst("0")
        }
    }
    if (carryOver == 1) {
        result.addFirst("1")
    }

    return result.joinToString(separator = "") { it.toString() }
}

fun solveA(): String {
    return input.map { it.toLongFromSnafu() }
        .sum()
        .toSnafuString()
}

fun main() {
    println("Answer A: ${solveA()}")
}

