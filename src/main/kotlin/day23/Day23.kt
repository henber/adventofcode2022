package day23

import Pos
import readInput
import kotlin.math.abs

val input = readInput("day23.txt")

enum class Direction {
    NORTH,
    SOUTH,
    WEST,
    EAST
}

fun getAdjacentPositions(pos: Pos, direction: Direction): Set<Pos> {
    return when(direction) {
        Direction.NORTH -> setOf(Pos(pos.x - 1, pos.y - 1), Pos(pos.x, pos.y - 1), Pos(pos.x + 1, pos.y - 1))
        Direction.SOUTH -> setOf(Pos(pos.x - 1, pos.y + 1), Pos(pos.x, pos.y + 1), Pos(pos.x + 1, pos.y + 1))
        Direction.WEST -> setOf(Pos(pos.x - 1, pos.y - 1), Pos(pos.x - 1, pos.y), Pos(pos.x - 1, pos.y + 1))
        Direction.EAST -> setOf(Pos(pos.x + 1, pos.y - 1), Pos(pos.x + 1, pos.y), Pos(pos.x + 1, pos.y + 1))
    }
}

fun takeTurn(elves: Set<Pos>, directionOffset: Int): Set<Pos> {
    val proposedPositions = proposePositions(elves, directionOffset)
        .filter { it.position != null }

    val duplicatePositions = proposedPositions
        .groupBy { it.position }

    val filteredPositions = duplicatePositions.filter {
        it.value.size == 1
    }.map { it.value[0] }.associateBy { it.elf }

    return elves.map {
        if (filteredPositions.containsKey(it)) {
            filteredPositions[it]!!.position!!
        } else it
    }.toSet()
}

data class ProposedPosition(val elf: Pos, val position: Pos?)

fun proposePositions(elves: Set<Pos>, directionOffset: Int): List<ProposedPosition> {
    return elves.map { elf ->
        val proposedPositions = (0 until Direction.values().size).map {
            val direction = Direction.values()[(it + directionOffset) % Direction.values().size]
            val adjacentPositions = getAdjacentPositions(elf, direction).toList()
            if (adjacentPositions.none { elves.contains(it) }) {
                ProposedPosition(elf, adjacentPositions[1])
            } else null
        }.filterNotNull()
        if (proposedPositions.size == Direction.values().size || proposedPositions.isEmpty()) {
            ProposedPosition(elf, null)
        } else proposedPositions[0]
    }
}

fun solveA(): Int {
    var elves = input.flatMapIndexed { y: Int, s: String ->
        s.mapIndexed { x: Int, c: Char ->
            if (c == '#') Pos(x, y)
            else null
        }
    }.filterNotNull().toSet()
    (0 until 10).forEach {
        elves = takeTurn(elves, it)
    }
    val yMin = elves.minBy { it.y }.y
    val yMax = elves.maxBy { it.y }.y
    val xMin = elves.minBy { it.x }.x
    val xMax = elves.maxBy { it.x }.x

    return (1 + abs(yMax - yMin)) * (1 + abs(xMax - xMin)) - elves.size
}

fun solveB(): Int {
    var elves = input.flatMapIndexed { y: Int, s: String ->
        s.mapIndexed { x: Int, c: Char ->
            if (c == '#') Pos(x, y)
            else null
        }
    }.filterNotNull().toSet()

    var turnCounter = 0
    while (true) {
        val movedElves = takeTurn(elves, turnCounter)
        turnCounter += 1
        if (movedElves.containsAll(elves)) {
            return turnCounter
        }
        elves = movedElves
    }
}

fun main() {
    println("Answer A: ${solveA()}")
    println("Answer B: ${solveB()}")
}