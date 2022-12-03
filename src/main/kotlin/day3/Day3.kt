package day3

import readInput

val alphabet = ('a'..'z').joinToString(separator = "") { "$it" }
val itemPriorityMap = (alphabet + alphabet.uppercase()).mapIndexed { index, c ->
    Pair(c, index + 1)
}.toMap()

val input = readInput("day3.txt")
    .map {
        val compartments = it.chunked(it.length / 2)
            .map { it.toSet() }
        Pair(compartments[0], compartments[1])
    }

fun solveA(): Long {
    return input.map {
        it.first.intersect(it.second).first()
    }.mapNotNull { itemPriorityMap[it] }.sum().toLong()
}

fun solveB(): Long {
    return input.asSequence()
        .chunked(3)
        .map {
            it.map { it.first.union(it.second) }
        }
        .map {
            (it[0] intersect it[1] intersect it[2]).first()
        }
        .mapNotNull { itemPriorityMap[it] }
        .sum().toLong()
}

fun main() {
    println("Answer A: ${solveA()}")
    println("Answer B: ${solveB()}")
}