package day22

import Pos
import listPartition
import printMatrix
import readInput

val input = readInput("day22.txt").listPartition { it.isNotBlank() }

val rawMap = input[0]
val mapMaxXSize = rawMap.maxOf { it.length }
val map = rawMap.map { it + " ".repeat(mapMaxXSize - it.length) }

val path = input[1][0].toCharArray().toList()
    .listPartition(true) { it.isDigit() }
        .map { it.joinToString(separator = "") { it.toString() } }
    .dropLast(1)

enum class Direction(val value: Int) {
     RIGHT(0), DOWN(1), LEFT(2), UP(3);

    fun turn(direction: Direction): Direction {
        if (direction != LEFT && direction != RIGHT) error("can only turn left or right")
        return when(this) {
            LEFT -> if (direction == LEFT) DOWN else UP
            UP -> if (direction == LEFT) LEFT else RIGHT
            RIGHT -> if (direction == LEFT) UP else DOWN
            DOWN -> if (direction == LEFT) RIGHT else LEFT
        }
    }

    fun move(pos: Pos): Pair<Direction, Pos> {
        return Pair(this, when(this) {
            LEFT -> Pos(pos.x - 1, pos.y)
            UP -> Pos(pos.x, pos.y - 1)
            RIGHT -> Pos(pos.x + 1, pos.y)
            DOWN -> Pos(pos.x, pos.y + 1)
        })
    }

    fun invert(): Direction {
        return when(this) {
            UP -> DOWN
            DOWN -> UP
            LEFT -> RIGHT
            RIGHT -> LEFT
        }
    }
}

fun move(steps: Int, current: Pair<Direction, Pos>, wrapAroundFunc: (Pos, Direction, Pos) -> Pair<Direction, Pos> = ::wrapAround): List<Pair<Direction, Pos>>{
    var stepsTaken = 0
    var currentPos = current
    val moves = mutableListOf(current)

    while (stepsTaken < steps) {
        stepsTaken += 1
        var wantedPos = currentPos.first.move(currentPos.second)
        wantedPos = wrapBoundaries(wantedPos.second, currentPos.first, currentPos.second, wrapAroundFunc)
        val newPos = when (map[wantedPos.second.y][wantedPos.second.x]) {
            '.' -> wantedPos
            ' ' -> wrapAroundFunc(wantedPos.second, wantedPos.first, currentPos.second)
            '#' -> return moves
            else -> error("Invalid map value: ${map[wantedPos.second.y][wantedPos.second.x]}")
        }
        if (newPos != currentPos) {
            moves.add(newPos)
            currentPos = newPos
        }
    }
    return moves
}

fun wrapBoundaries(wantedPos: Pos, direction: Direction, currentPos: Pos, wrapAroundFunc: (Pos, Direction, Pos) -> Pair<Direction, Pos>): Pair<Direction, Pos> {
    if (wantedPos.x >= map[0].length || wantedPos.x < 0 || wantedPos.y >= map.size || wantedPos.y < 0) {
       return wrapAroundFunc(wantedPos, direction, currentPos)
    }
    return Pair(direction, wantedPos)
}

fun wrapAround(wantedPos: Pos, direction: Direction, previousPos: Pos): Pair<Direction, Pos> {
    var wrapAroundPos = when(direction) {
        Direction.LEFT -> Pos(map[0].length - 1, wantedPos.y)
        Direction.UP -> Pos(wantedPos.x, map.size - 1)
        Direction.RIGHT -> Pos(0, wantedPos.y)
        Direction.DOWN -> Pos(wantedPos.x, 0)
    }

    while(map[wrapAroundPos.y][wrapAroundPos.x] == ' ') {
        wrapAroundPos = direction.move(wrapAroundPos).second
    }

    if (map[wrapAroundPos.y][wrapAroundPos.x] == '.') {
        return Pair(direction, wrapAroundPos)
    }
    return Pair(direction, previousPos) // found '#'
}

fun solveA(): Int {
    val startXIndex = map[0].indexOf('.')
    val positions = path.fold(listOf(Pair(Direction.RIGHT, Pos(startXIndex, 0)))) { acc, s ->
        val current = acc.last()
        val result = when (s) {
            "R" -> acc + listOf(Pair(current.first.turn(Direction.RIGHT), current.second))
            "L" -> acc + listOf(Pair(current.first.turn(Direction.LEFT), current.second))
            else -> acc + move(s.toInt(), current)
        }
        result
    }

    val posMap = positions.map { Pair(it.second, it.first) }.toMap()

    printMatrix(map.mapIndexed { y, s ->
        s.mapIndexed { x, c ->
            val replacement = posMap[Pos(x, y)]
            when (replacement) {
                null -> c
                Direction.RIGHT -> '>'
                Direction.DOWN -> 'v'
                Direction.LEFT -> '<'
                Direction.UP -> '^'
            }
        }
    })

    val destination = positions.last()
    println(destination)
    return destination.first.value + (destination.second.y + 1) * 1000 + (destination.second.x + 1) * 4
}

fun printMapPos(pos: Pos): Char {
    return map[pos.y][pos.x]
}

data class Zone(val id: Int, val x: IntRange, val y: IntRange) {

    fun contains(pos: Pos): Boolean = x.contains(pos.x) && y.contains(pos.y)
}

val zones = listOf(
    Zone(1, (50 until 100),(0 until 50)),
    Zone(2, (100 until 150),(0 until 50)),
    Zone(3, (50 until 100),(50 until 100)),
    Zone(4, (0 until 50),(100 until 150)),
    Zone(5, (50 until 100),(100 until 150)),
    Zone(6, (0 until 50),(150 until 200))
).associateBy { it.id }

fun getZone(pos: Pos): Zone =
    maybeZone(pos)!!

fun getZone(id: Int): Zone = zones[id]!!

fun maybeZone(pos: Pos): Zone? =
    zones.entries.firstOrNull { it.value.x.contains(pos.x) && it.value.y.contains(pos.y) }?.value

fun getZone(previousZone: Zone, direction: Direction): Zone {
    return when(previousZone.id) {
        1 -> getZone(
            when (direction) {
                Direction.RIGHT -> 2
                Direction.DOWN -> 3
                Direction.LEFT -> 4
                Direction.UP -> 6
            }
        )

        2 -> getZone(
            when (direction) {
                Direction.RIGHT -> 5
                Direction.DOWN -> 3
                Direction.LEFT -> 1
                Direction.UP -> 6
            }
        )

        3 -> getZone(
            when (direction) {
                Direction.RIGHT -> 2
                Direction.DOWN -> 5
                Direction.LEFT -> 4
                Direction.UP -> 1
            }
        )

        4 -> getZone(
            when (direction) {
                Direction.RIGHT -> 5
                Direction.DOWN -> 6
                Direction.LEFT -> 1
                Direction.UP -> 3
            }
        )

        5 -> getZone(
            when (direction) {
                Direction.RIGHT -> 2
                Direction.DOWN -> 6
                Direction.LEFT -> 4
                Direction.UP -> 3
            }
        )

        6 -> getZone(
            when (direction) {
                Direction.RIGHT -> 5
                Direction.DOWN -> 2
                Direction.LEFT -> 1
                Direction.UP -> 4
            }
        )

        else -> error("invalid from zone: ${previousZone.id}")
    }
}

fun convertToZone(previousZone: Zone, wantedPos: Pos, zone: Zone) : Pair<Direction, Pos> {
    return when {
        (previousZone.id == 1 && zone.id == 4) -> Pair(Direction.RIGHT, Pos(zone.x.first, flipY(wantedPos.y, zone)))
        (previousZone.id == 1 && zone.id == 6) -> Pair(Direction.RIGHT, Pos(zone.x.first, translateY(wantedPos.x, zone)))
        (previousZone.id == 2 && zone.id == 3) -> Pair(Direction.LEFT, Pos(zone.x.last, translateY(wantedPos.x, zone)))
        (previousZone.id == 2 && zone.id == 5) -> Pair(Direction.LEFT, Pos(zone.x.last, flipY(wantedPos.y, zone)))
        (previousZone.id == 2 && zone.id == 6) -> Pair(Direction.UP, Pos(translateX(wantedPos.x, zone), zone.y.last))
        (previousZone.id == 3 && zone.id == 2) -> Pair(Direction.UP, Pos(translateX(wantedPos.y, zone), zone.y.last))
        (previousZone.id == 3 && zone.id == 4) -> Pair(Direction.DOWN, Pos(translateX(wantedPos.y, zone), zone.y.first))
        (previousZone.id == 4 && zone.id == 1) -> Pair(Direction.RIGHT, Pos(zone.x.first, flipY(wantedPos.y, zone)))
        (previousZone.id == 4 && zone.id == 3) -> Pair(Direction.RIGHT, Pos(zone.x.first, translateY(wantedPos.x, zone)))
        (previousZone.id == 5 && zone.id == 2) -> Pair(Direction.LEFT, Pos(zone.x.last, flipY(wantedPos.y, zone)))
        (previousZone.id == 5 && zone.id == 6) -> Pair(Direction.LEFT, Pos(zone.x.last, translateY(wantedPos.x, zone)))
        (previousZone.id == 6 && zone.id == 1) -> Pair(Direction.DOWN, Pos(translateX(wantedPos.y, zone), zone.y.first))
        (previousZone.id == 6 && zone.id == 2) -> Pair(Direction.DOWN, Pos(translateX(wantedPos.x, zone), zone.y.first))
        (previousZone.id == 6 && zone.id == 5) -> Pair(Direction.UP, Pos(translateX(wantedPos.y, zone), zone.y.last))
        (previousZone.id == 1 && zone.id == 2) -> Pair(Direction.RIGHT, Pos(zone.x.first, translateY(wantedPos.y, zone)))
        (previousZone.id == 1 && zone.id == 3) -> Pair(Direction.DOWN, Pos(translateX(wantedPos.x, zone) ,zone.y.first))
        (previousZone.id == 2 && zone.id == 1) -> Pair(Direction.LEFT, Pos(zone.x.last, translateY(wantedPos.y, zone)))
        (previousZone.id == 3 && zone.id == 1) -> Pair(Direction.UP, Pos(translateX(wantedPos.x, zone), zone.y.last))
        (previousZone.id == 3 && zone.id == 5) -> Pair(Direction.DOWN, Pos(translateX(wantedPos.x, zone), zone.y.first))
        (previousZone.id == 4 && zone.id == 5) -> Pair(Direction.RIGHT, Pos(zone.x.first, translateY(wantedPos.y, zone)))
        (previousZone.id == 4 && zone.id == 6) -> Pair(Direction.DOWN, Pos(translateX(wantedPos.x, zone), zone.y.first))
        (previousZone.id == 5 && zone.id == 3) -> Pair(Direction.UP, Pos(translateX(wantedPos.x, zone), zone.y.last))
        (previousZone.id == 5 && zone.id == 4) -> Pair(Direction.LEFT, Pos(zone.x.last, translateY(wantedPos.y, zone)))
        (previousZone.id == 6 && zone.id == 4) -> Pair(Direction.UP, Pos(translateX(wantedPos.x, zone), zone.y.last))
        else -> error("Unable to move from zone: $previousZone to ${zone.id}")
    }
}

fun translateX(x: Int, zone: Zone): Int {
    return zone.x.first + (x % 50)
}

fun translateY(y: Int, zone: Zone): Int {
    return zone.y.first + (y % 50)
}

fun flipY(y: Int, zone: Zone): Int {
     return zone.y.last - (y % 50)
}

fun flipX(x: Int, zone: Zone): Int {
    return zone.x.last - (x % 50)
}

fun wrapAroundB(wantedPos: Pos, direction: Direction, previousPos: Pos): Pair<Direction, Pos> {
    val previousZone = getZone(previousPos)

    val wantedPosZone = maybeZone(wantedPos)

    if (previousZone.id == wantedPosZone?.id) {
        return Pair(direction, wantedPos)
    }

    val targetZone = getZone(previousZone, direction)

    val newPos = convertToZone(previousZone, wantedPos, targetZone)

    if (map[newPos.second.y][newPos.second.x] == '#') {
        return Pair(direction, previousPos)
    }
    return newPos
}

fun solveB(): Int {
    val startXIndex = map[0].indexOf('.')
    val positions = path.fold(listOf(Pair(Direction.RIGHT, Pos(startXIndex, 0)))) { acc, s ->
        val current = acc.last()
        val result = when (s) {
            "R" -> acc + listOf(Pair(current.first.turn(Direction.RIGHT), current.second))
            "L" -> acc + listOf(Pair(current.first.turn(Direction.LEFT), current.second))
            else -> acc + move(s.toInt(), current, ::wrapAroundB)
        }
        result
    }
    val destination = positions.last()

    val posMap = positions.map { Pair(it.second, it.first) }.toMap()

    printMatrix(map.mapIndexed { y, s ->
        s.mapIndexed { x, c ->
            val replacement = posMap[Pos(x, y)]
            when (replacement) {
                null -> c
                Direction.RIGHT -> '>'
                Direction.DOWN -> 'v'
                Direction.LEFT -> '<'
                Direction.UP -> '^'
            }
        }
    })

    println(destination)
    return destination.first.value + (destination.second.y + 1) * 1000 + (destination.second.x + 1) * 4
}

fun main() {
    println("Answer A: ${solveA()}")
    println("Answer B: ${solveB()}")
}