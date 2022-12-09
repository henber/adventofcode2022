package day9

import Pos
import readInput
import kotlin.math.abs

val input = readInput("day9.txt")
    .map { it.split(" ") }
    .map { Pair(
        when (it[0]) {
            "U" -> Direction.UP
            "D" -> Direction.DOWN
            "L" -> Direction.LEFT
            "R" -> Direction.RIGHT
            else -> error("Invalid direction")
        }, it[1].toInt()
    ) }


fun moveTail(head: Pos, tail: Pos): Pos {
    return when {
        head == tail -> tail
        abs(head.y - tail.y) == 1 && abs(head.x - tail.x) == 1 -> tail
        head.x == tail.x -> if (abs(head.y - tail.y) > 1) tail + Pos(0, if (head.y > tail.y) 1 else -1) else tail
        head.y == tail.y -> if (abs(head.x - tail.x) > 1) tail + Pos(if (head.x > tail.x) 1 else -1, 0) else tail
        else -> tail + Pos(if (head.x > tail.x) 1 else -1, if (head.y > tail.y) 1 else -1)
    }
}

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

fun moveHead(direction: Direction, head: Pos): Pos {
    return when(direction) {
        Direction.UP -> head + Pos(0, 1)
        Direction.DOWN -> head + Pos(0, -1)
        Direction.LEFT -> head + Pos(-1, 0)
        Direction.RIGHT -> head + Pos(1, 0)
    }
}

fun solveA(): Int {
    var head = Pos(0,0)
    var tail = Pos(0, 0)
    val tailSet = mutableSetOf(tail)

    input.forEach { pair ->
        (1..pair.second).forEach {
            head = moveHead(pair.first, head)
            tail = moveTail(head, tail)
            tailSet.add(tail)
        }
    }
    return tailSet.size
}

fun solveB(): Int {
    var head = Pos(0, 0)
    val tails = (1..9).map { Pos(0, 0) }.toMutableList()
    val tailSet = mutableSetOf(tails.last())

    input.forEach { pair ->
        (1..pair.second).forEach { _ ->
            head = moveHead(pair.first, head)
            tails.forEachIndexed { index, pos ->
                if (index == 0) tails[index] = moveTail(head, pos)
                else tails[index] = moveTail(tails[index - 1], pos)
            }
            tailSet.add(tails.last())
        }
    }
    return tailSet.size
}

fun main() {
    println("Answer A: ${solveA()}")
    println("Answer B: ${solveB()}")
}