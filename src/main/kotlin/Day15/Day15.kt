package Day15

import Pos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import manhattanDist
import measureTime
import readInput
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.max
import kotlin.math.min

val inputRegex =
    Regex("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)")
val input = readInput("day15.txt")
    .map { inputRegex.find(it) }
    .map {
        Pair(
            Pos(it!!.groups[1]!!.value.toInt(), it.groups[2]!!.value.toInt()),
            Pos(it.groups[3]!!.value.toInt(), it.groups[4]!!.value.toInt())
        )
    }

fun findRangeOnLevel(pos: Pos, distance: Int, level: Int): IntRange {
    val remainingDist = distance - manhattanDist(pos, Pos(pos.x, level))

    if (remainingDist < 0) {
        return IntRange.EMPTY
    }

    return IntRange(pos.x - remainingDist, pos.x + remainingDist)
}

fun solveA(): Int {
    val wantedLevel = 2_000_000
    val sensorDist = input.map {
        Pair(it.first, manhattanDist(it.first, it.second))
    }

    val beacons = input.map { it.second }.toSet()

    return sensorDist
        .map { findRangeOnLevel(it.first, it.second, wantedLevel) }
//        .map { it.toSet() }
        .fold(emptySet<Int>()) { acc, ints ->
            acc union ints
        }.filterNot {
            beacons.contains(Pos(it, wantedLevel))
        }.size
}

fun findGapsInRanges(ranges: List<IntRange>): List<IntRange> {
    val sortedRanges = ranges
        .filter { !it.isEmpty() }
        .sortedBy { it.first }

    val gaps = mutableListOf<IntRange>()
    var currentIndex = 0
    sortedRanges.fold(mutableListOf<IntRange>()) { acc, intRange ->
        if (acc.isNotEmpty()) {
            val currentRange = acc[currentIndex]
            if (intRange.last > currentRange.last && intRange.first <= currentRange.last) {
                acc[currentIndex] = IntRange(currentRange.first, intRange.last)
            } else if (intRange.first > 4_000_000 || intRange.last < currentRange.last) return@fold acc
            else {
                // gap found
                gaps.add(IntRange(max(currentRange.last + 1, xMin), min(intRange.first - 1, xMax)))
                currentIndex += 1
                acc.add(intRange)
            }
        } else {
            acc.add(intRange)
        }
        acc
    }
    return gaps.filterNot {
        it.isEmpty()
    }
}

val xMin = 0
val yMin = 0
val xMax = 4_000_000
val yMax = 4_000_000

fun solveB(): Long {

    val sensorDist = input.map {
        Pair(it.first, manhattanDist(it.first, it.second))
    }

    (yMin..yMax).map { y ->
        val invalidRanges = sensorDist
            .map { findRangeOnLevel(it.first, it.second, y) }

        val gaps = findGapsInRanges(invalidRanges)
        gaps.forEach {
            return it.first * 4_000_000L + y
        }
    }

    return -1
}

fun main() {
    println("Answer A: ${solveA()}")
    println("Answer B: ${solveB()}")
}



