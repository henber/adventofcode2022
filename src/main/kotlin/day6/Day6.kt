package day6

import readInputText

val input = readInputText("day6.txt")


fun findDistinctCharsInSeqIndex(size: Int): Int {
    return input.windowed(size).map { it.toSet() }
        .indexOfFirst { it.size == size } + size
}

fun solveA(): Int = findDistinctCharsInSeqIndex(4)

fun solveB(): Int = findDistinctCharsInSeqIndex(14)

fun main() {
    println("Answer A: ${solveA()}")
    println("Answer B: ${solveB()}")
}