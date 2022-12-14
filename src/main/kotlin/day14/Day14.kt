package day14

import Pos
import readInput
import java.lang.Integer.max
import java.lang.Integer.min

val input = readInput("day14.txt")

fun posRange(from: Pos, to: Pos): List<Pos> {
    return (min(from.x, to.x)..max(from.x,to.x))
        .flatMap { x ->
            (min(from.y, to.y)..max(from.y,to.y)).map { y -> Pos(x, y) }
    }
}

val rockPositions = input
    .flatMap { it.replace("->", "")
        .split(" ")
        .filterNot { it.isBlank() }
        .windowed(2) }
    .map {
        it.map { it.split(",").filterNot { it.isBlank() } }
    }.flatMap {
        posRange(Pos(it[0][0].toInt(), it[0][1].toInt()), Pos(it[1][0].toInt(), it[1][1].toInt()))
    }
    .toSet()

fun spawnSand(sandPositions: MutableSet<Pos>, yMax: Int): Boolean {
    var newSand = Pos(500, 0)
    var movedSand = newSand
    do {
        newSand = movedSand
        movedSand = moveSand(newSand, sandPositions, rockPositions)
    } while (movedSand != newSand && movedSand.y <= yMax)

    if (movedSand.y > yMax) return true
    sandPositions.add(movedSand)
    return false
}

fun moveSand(newSand: Pos, sandPositions: Set<Pos>, rockPositions: Set<Pos>): Pos {
    return listOf(newSand.x, newSand.x-1, newSand.x+1)
        .map { Pos(it, newSand.y + 1) }
        .filterNot { sandPositions.contains(it) || rockPositions.contains(it) }
        .firstOrNull() ?: newSand
}

fun solveA(): Int {
    val yMax = rockPositions.map { it.y }.max()
    val sandPositions = mutableSetOf<Pos>()

    var lastResult: Boolean
    do {
        lastResult = spawnSand(sandPositions, yMax)
    } while (!lastResult)

    return sandPositions.size
}

fun spawnSandB(sandPositions: MutableSet<Pos>, rockPositions: Set<Pos>): Boolean {
    var newSand = Pos(500, 0)
    var movedSand = newSand
    do {
        newSand = movedSand
        movedSand = moveSand(newSand, sandPositions, rockPositions)
    } while (movedSand != newSand)

    sandPositions.add(movedSand)

    return movedSand == Pos(500, 0)
}

fun solveB(): Int {
    val yMax = rockPositions.map { it.y }.max()
    val sandPositions = mutableSetOf<Pos>()
    val sufficientlyLargeOffset = 200 // instead of remodeling with infinity
    val extendedRockPositions = rockPositions +
        posRange(
            Pos(rockPositions.minBy { it.x }.x - sufficientlyLargeOffset, yMax + 2),
            Pos(rockPositions.maxBy { it.x }.x + sufficientlyLargeOffset, yMax + 2))
            .toSet()

    var lastResult: Boolean
    do {
        lastResult = spawnSandB(sandPositions, extendedRockPositions)
    } while (!lastResult)

    return sandPositions.size
}

fun main() {
    println("Answer A: ${solveA()}")
    println("Answer B: ${solveB()}")
}

