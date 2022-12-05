package day4

import readInput

val pairRegex = Regex("(\\d+)-(\\d+),(\\d+)-(\\d+)")

val input = readInput("day4.txt")
    .map {
        val regexResult = pairRegex.find(it)
        Pair(
            IntRange(regexResult!!.groupValues[1].toInt(), regexResult.groupValues[2].toInt()),
            IntRange(regexResult.groupValues[3].toInt(), regexResult.groupValues[4].toInt()))
    }

fun IntRange.contains(other: IntRange): Boolean =
    this.first <= other.first && this.last >= other.last

fun solveA(): Int {
    return input.count {
        it.first.contains(it.second) || it.second.contains(it.first)
    }
}

fun IntRange.overlap(other: IntRange): Boolean =
    (this.first >= other.first && this.first <= other.last) ||
        (this.last <= other.last && this.last >= other.first) ||
        this.contains(other)

fun solveB(): Int {
    return input.count {
        it.first.overlap(it.second)
    }
}

fun main() {
    println("Answer A: ${solveA()}")
    println("Answer B: ${solveB()}")
}