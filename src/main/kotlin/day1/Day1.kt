package day1

import listPartition
import readInput

val input = readInput("day1.txt")

val groupedInputNumbers = listPartition(input, mutableListOf()) {
    it.isNotBlank()
}.map { it.map { it.toLong() } }

fun solveA(): Long = groupedInputNumbers.maxOf { it.sum() }

fun solveB(): Long = groupedInputNumbers.sortedByDescending { it.sum() }.take(3).sumOf { it.sum() }

fun main() {
    println("Answer A: ${solveA()}")
    println("Answer B: ${solveB()}")
}