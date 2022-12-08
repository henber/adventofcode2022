package day8

import Pos
import product
import readInput
import java.lang.Integer.min

val input = readInput("day8.txt")
    .map { it.toList().map { it.toString() } }

fun <T> splitListExcludingCenter(list: List<T>, index: Int): List<List<T>> {
    if (index == 0) return listOf(emptyList(), list.subList(1, list.size))

    if (index == list.size - 1) return listOf(list.subList(0, list.size - 1), emptyList())

    return listOf(list.subList(0, index), list.subList(index + 1, list.size))
}

fun isTreeVisible(pos: Pos): Boolean {
    val value = input[pos.x][pos.y]

    val horizontalSlices = splitListExcludingCenter(input[pos.x], pos.y)
    val verticalSlices = splitListExcludingCenter((input.indices).map { input[it][pos.y] }, pos.x)

    return (horizontalSlices + verticalSlices).any { it.isEmpty() || it.all { it < value }  }
}

fun solveA(): Int {
    return input.indices.map { x -> input.first().indices.map { Pos(x, it) } }
        .flatten().count { isTreeVisible(it) }
}

fun countVisibleTrees(list: List<String>, value: String): Int =
    min(list.takeWhile { it < value }.count() + 1, list.size)

fun countVisibleTreesFrom(pos: Pos): Long {
    val value = input[pos.x][pos.y]

    val horizontalSlices = splitListExcludingCenter(input[pos.x], pos.y)
    val verticalSlices = splitListExcludingCenter((input.indices).map { input[it][pos.y] }, pos.x)

    return listOf(horizontalSlices.first().reversed(), horizontalSlices[1],
        verticalSlices.first().reversed(), verticalSlices[1]).map { countVisibleTrees(it, value) }
        .product()
}

fun solveB(): Long {
    return input.indices.map { x -> input.first().indices.map { Pos(x, it) } }
        .flatten().maxOfOrNull { countVisibleTreesFrom(it) } ?: -1
}

fun main() {
    println("Answer A: ${solveA()}")
    println("Answer A: ${solveB()}")
}

