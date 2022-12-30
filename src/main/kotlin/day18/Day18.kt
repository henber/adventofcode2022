package day18

import Pos3
import manhattanDist
import readInput

val input = readInput("day18.txt")
    .map { it.split(",").map { it.toInt() } }
    .map { Pos3(it[0],it[1],it[2]) }
    .toSet()



fun solveA(): Int {
    return input.map { toCheck ->
        6 - input.filterNot { toCheck == it }
            .map { if (manhattanDist(toCheck, it) == 1) 1 else 0 }
            .sum()
    }.sum()
}

fun isPocketNode(pos: Pos3, pocketNodes: Set<Pos3>): Boolean {
    if (6 - input.filterNot { pos == it }
                .map { if (manhattanDist(pos, it) == 1) 1 else 0 }
                .sum() == 0) {
        return true
    }
    val pocketPlusInput = pocketNodes.toSet() union input
    val filteredFixedXY = pocketPlusInput.filter { it.x == pos.x  && it.y == pos.y}
    val filteredFixedXZ = pocketPlusInput.filter { it.x == pos.x  && it.z == pos.z}
    val filteredFixedYZ = pocketPlusInput.filter { it.z == pos.z  && it.y == pos.y}

    return filteredFixedXY.any { it.z < pos.z }.and(filteredFixedXY.any { it.z > pos.z })
        .and(filteredFixedXZ.any { it.y < pos.y }).and(filteredFixedXZ.any { it.y > pos.y })
        .and(filteredFixedYZ.any { it.x < pos.x }).and(filteredFixedYZ.any { it.x > pos.x })
}

fun solveB(): Int {
    val xMax = input.maxOf { it.x }
    val yMax = input.maxOf { it.y }
    val zMax = input.maxOf { it.z }
    val pocketNodes = mutableSetOf<Pos3>()

    for (i in 1..xMax) {
        for (j in 1..yMax) {
            for (k in 1..zMax) {
                val pos = Pos3(i, j, k)
                if ( input.contains(pos) ) continue
                if (isPocketNode(pos, pocketNodes)) pocketNodes.add(pos)
            }
        }
    }

    return solveA() - pocketNodes.map { pocketNode ->
        input.map { if (manhattanDist(pocketNode, it) == 1) 1 else 0 }.sum()
    }.sum()
}

fun main() {
    println("Answer A: ${solveA()}")
    println("Answer B: ${solveB()}")
}