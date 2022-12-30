package day24

import Pos
import bfs
import readInput

val input = readInput("day24.txt")

val blizzardModulo = (input.size - 2) * (input[0].length - 2)

enum class Direction {
    NORTH,
    SOUTH,
    WEST,
    EAST;

    fun adjacentPos(pos: Pos): Pos {
        return when (this) {
            NORTH -> pos + Pos(0, -1)
            SOUTH -> pos + Pos(0, 1)
            WEST -> pos + Pos(-1, 0)
            EAST -> pos + Pos(1, 0)
        }
    }
}
data class Blizzard(val direction: Direction, val pos: Pos)
val directionSet = setOf('<', '>', '^', 'v')
val blizzards = input.flatMapIndexed { y: Int, s: String ->
    s.mapIndexed { x: Int, c: Char ->
        if (!directionSet.contains(c)) null
        else Blizzard(when(c) {
            '<' -> Direction.WEST
            '>' -> Direction.EAST
            '^' -> Direction.NORTH
            'v' -> Direction.SOUTH
            else -> error("invalid direction: $c")
        }, Pos(x, y))
    }
}.filterNotNull()


var blizzardPermutations = populateBlizzards()

fun moveBlizzards(blizzards: List<Blizzard>): List<Blizzard> {
    return blizzards.map { blizzard ->
        Blizzard(blizzard.direction, blizzard.direction.adjacentPos(blizzard.pos))
    }.map { blizzard ->
        if (input[blizzard.pos.y][blizzard.pos.x] == '#') {
            Blizzard(blizzard.direction, when (blizzard.direction) {
                Direction.NORTH -> blizzard.pos + Pos(0, input.size - 2)
                Direction.SOUTH -> blizzard.pos + Pos(0, -(input.size - 2))
                Direction.WEST -> blizzard.pos + Pos(input[0].length - 2, 0)
                Direction.EAST -> blizzard.pos + Pos(-(input[0].length - 2), 0)
            })
        } else blizzard
    }
}

fun populateBlizzards(): List<Pair<List<Blizzard>, Set<Pos>>> {
    val result = mutableListOf<Pair<List<Blizzard>, Set<Pos>>>()
    (0 until blizzardModulo).forEach {
        if (it == 0) {
            result.add(Pair(blizzards, blizzards.map { it.pos }.toSet()))
        } else {
            val movedBlizzards = moveBlizzards(result[it - 1].first)
            result.add(Pair(movedBlizzards, movedBlizzards.map { it.pos }.toSet()))
        }
    }
    return result
}

fun findPathBetweenBlizzards(start: Pos, end: Pos, offset: Int = 0): List<Pos> {
    val visitedAtTurn = mutableSetOf<Pair<Pos, Int>>()
    val result = bfs(end, start, duplicatesAllowed = true, getEdges = { pos: Pos, path: List<Pos> ->
        val positions = Direction.values().map { it.adjacentPos(pos) } + pos
        positions.asSequence().filter { it.x >= 0 && it.y >= 0 && it.x < input[0].length && it.y < input.size}
            .filterNot { visitedAtTurn.contains(Pair(it, (path.size + offset) % blizzardModulo)) }
            .filterNot {
                input[it.y][it.x] == '#'
            }.filterNot {
                blizzardPermutations[(path.size + offset) % blizzardModulo].second.contains(it)
            }.map {
                visitedAtTurn.add(Pair(it, (path.size + offset) % blizzardModulo))
                BFS.Edge(it)
            }.toList()
    }) {
        it
    }
    return result
}

fun solveA(): Int {
    val start = Pos(input[0].indexOf('.'), 0)
    val end = Pos(input[input.size - 1].indexOf('.'), input.size - 1)
    val result = findPathBetweenBlizzards(start, end)
    return result.size - 1
}

fun solveB(): Int {
    val start = Pos(input[0].indexOf('.'), 0)
    val end = Pos(input[input.size - 1].indexOf('.'), input.size - 1)
    val result1 = findPathBetweenBlizzards(start, end).size - 1
    val result2 = findPathBetweenBlizzards(end, start, result1).size - 1
    val result3 = findPathBetweenBlizzards(start, end, result1 + result2).size - 1
    println("result1: $result1, result2: $result2, result3: $result3")
    return result1 + result2 + result3
}


fun main() {
    println("Answer A: ${solveA()}")
    println("Answer A: ${solveB()}")
}