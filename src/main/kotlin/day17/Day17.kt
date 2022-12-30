package day17

import Pos
import readInputText
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.abs

enum class RockType {
    MINUS,
    PLUS,
    ANGLE,
    PIPE,
    BLOCK;

    fun getCoords(pos: Pos): Set<Pos> {
        return when (this) {
            MINUS -> setOf(pos, pos + Pos(1, 0), pos + Pos(2, 0), pos + Pos(3, 0))
            PLUS -> setOf(pos, pos + Pos(1, 0), pos + Pos(1, -1), pos + Pos(1, 1), pos + Pos(2, 0))
            ANGLE -> setOf(pos, pos + Pos(1, 0), pos + Pos(2, 0), pos + Pos(2, 1), pos + Pos(2, 2))
            PIPE -> setOf(pos, pos + Pos(0, 1), pos + Pos(0, 2), pos + Pos(0, 3))
            BLOCK -> setOf(pos, pos + Pos(0, 1), pos + Pos(1, 0), pos + Pos(1, 1))
        }
    }

    fun getLowestLevel(pos: Pos): Pos {
        return when(this) {
            PLUS -> pos + Pos(1, -1)
            else -> pos
        }
    }

    fun getLeftmost(pos: Pos): Pos = pos

    fun getRightmost(pos: Pos): Pos {
        return when(this) {
            MINUS -> pos + Pos(3, 0)
            PLUS -> pos + Pos(2, 0)
            ANGLE -> pos + Pos(2, 0)
            PIPE -> pos
            BLOCK -> pos + Pos(1, 0)
        }
    }

    fun getTopmost(pos: Pos): Pos {
        return when(this) {
            MINUS -> pos
            PLUS -> pos + Pos(1,1)
            ANGLE -> pos + Pos(2, 2)
            PIPE -> pos + Pos(0, 3)
            BLOCK -> pos + Pos(0, 1)
        }
    }
}

data class Rock(val type: RockType, val pos: Pos) {

    fun getCoords() = type.getCoords(pos)

    fun getLowestLevel() = type.getLowestLevel(pos)

    fun getLeftmost() = type.getLeftmost(pos)

    fun getRightmost() = type.getRightmost(pos)

    fun getTopmost() = type.getTopmost(pos)
}

class FallenRocks() {
//    val rocks: ArrayDeque<Rock> = ArrayDeque()) {

//    constructor(size: Int): this(ArrayDeque(size))

    var maxLevel = -1
    var size = 0
    var windCounter: Int = 0

    var levelMap: MutableMap<Int, MutableSet<Rock>> = mutableMapOf()

    fun add(rock: Rock) {
//        rocks.add(rock)
        size += 1
        if (maxLevel < rock.getTopmost().y) {
            maxLevel = rock.getTopmost().y
        }
        rock.getCoords().map { it.y }.distinct().forEach { level ->
            if (levelMap[level] == null)
                levelMap[level] = mutableSetOf(rock)
            else levelMap[level]!!.add(rock)

            if (maxLevel > 50) levelMap.remove(maxLevel - 50)
//            if (levelMap[level]!!.map { it.getCoords().filter { it.y == level }.toSet() }
//                    .fold(emptySet<Pos>()) { acc, pos ->
//                        acc union pos
//                    }.size == 7) {
//                levelMap = levelMap.filterKeys { it >= level }.toMutableMap()
//            }
        }
    }

    fun rocksAroundLevel(level: Int): List<Rock> =
        (level - 4..level + 4).map { levelMap[it] ?: emptySet() }.fold(emptySet<Rock>()) { acc, rocks ->
            acc union rocks
        }.toList()
//        rocks.filter { abs(level - it.pos.y) <= 5 }

  /*  override fun toString(): String {
        val stringBuilder = java.lang.StringBuilder()
        val coords = rocks.flatMap { it.getCoords() }.toSet()
        for (j in maxLevel downTo 0) {
            for (i in 0..6) {
                if (coords.contains(Pos(i, j))) {
                    stringBuilder.append('#')
                } else stringBuilder.append('.')
            }
            stringBuilder.append('\n')
        }
        return stringBuilder.toString()
    }*/
}

val rockTypes = listOf(RockType.MINUS, RockType.PLUS, RockType.ANGLE, RockType.PIPE, RockType.BLOCK)

val input = readInputText("day17.txt")


fun spawnRock(rocks: FallenRocks, type: RockType): Rock {
    val level = rocks.maxLevel + 4
    return if (type == RockType.PLUS) Rock(type, Pos(2, level + 1))
        else Rock(type, Pos(2, level))
}

fun moveRock(rock: Rock, fallenRocks: FallenRocks): Pair<Rock, Boolean> {
    val direction = input[fallenRocks.windCounter % input.length]

    var movedRock = when (direction) {
        '>' -> if (rock.getRightmost().x == 6) rock else Rock(rock.type, rock.pos + Pos(1, 0))
        '<' ->  if (rock.getLeftmost().x == 0) rock else Rock(rock.type, rock.pos + Pos(-1, 0))
        else -> error("invalid direction: $direction")
    }

    val closeRocks = fallenRocks.rocksAroundLevel(rock.pos.y)
    if (closeRocks.map { it.getCoords() }.any { it.intersect(movedRock.getCoords()).isNotEmpty()  } ) {
        movedRock = rock
    }

    val fallenRock = Rock(rock.type, movedRock.pos + Pos(0, -1))
    if (closeRocks.map { it.getCoords() }.any { it.intersect(fallenRock.getCoords()).isNotEmpty()  } || fallenRock.getLowestLevel().y < 0 ) {
        return Pair(movedRock, true)
    }
    return Pair(fallenRock, false)
}

fun dropRocks(nr: Int): Int {
    val fallenRocks = FallenRocks()
    for (i in (0 until nr)) {
        dropRock(fallenRocks)

//        if (fallenRocks.rocks.size % 1000 == 0)
//            println("Rock count: ${fallenRocks.rocks.size}, maxLevel: ${fallenRocks.maxLevel + 1}, level/count ratio: ${(fallenRocks.maxLevel.toDouble() + 1) / fallenRocks.rocks.size}")
    }
    return fallenRocks.maxLevel + 1
}

fun dropRock(fallenRocks: FallenRocks): Pair<Int, Int> {
    return dropRock(1, fallenRocks)
}

fun dropRock(nr: Int, fallenRocks: FallenRocks): Pair<Int, Int> {
    for(i in (1..nr)) {
        var rock = spawnRock(fallenRocks, rockTypes[fallenRocks.size % rockTypes.size])
        do {
            val (movedRock, stuck) = moveRock(rock, fallenRocks)
            fallenRocks.windCounter += 1
            rock = movedRock
        } while (!stuck)
        fallenRocks.add(rock)
    }
//    println(fallenRocks)
    return Pair(fallenRocks.size, fallenRocks.maxLevel + 1)
}

fun solveA(): Int = dropRocks(2_022)

fun solveB(): Long {
    val factor = input.length * rockTypes.size
    val fallenRocks = FallenRocks()//factor * 70)

    val value = 1_000_000_000_000L
    //println(dropRocks(60_000)) // 90861
    val cycle = 345 // 14
    var rocks: MutableList<Pair<Int, Int>> = java.util.ArrayList(1000)
    for (i in 1..cycle + 1) {
//        println(i)
        rocks.add(dropRock(factor, fallenRocks))
    }
//    println(rocks.windowed(2).map { abs(it[0].second - it[1].second) })
    val levelsPerCycle = rocks.windowed(2).map { abs(it[0].second - it[1].second) }.take(cycle).sum()
//    println("factor: $factor, cycle: $cycle, levelsPerCycle: $levelsPerCycle")
    return (value / (factor * cycle)) * levelsPerCycle + dropRocks((value % (factor * cycle)).toInt()).toLong()
}

fun main() {
    println("Answer A: ${solveA()}")
    println("Answer B: ${solveB()}")
}

