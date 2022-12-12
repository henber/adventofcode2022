package day12

import Pos
import bfs

import readInput
import kotlin.math.abs

val input = readInput("day12.txt")
//val input = readInput("day12test.txt")

fun getValue(pos: Pos): Char {
    val value = input[pos.x][pos.y]
    return if (value == 'S') 'a' else if(value == 'E') 'z' else value
}

fun findPosByValue(char: Char) =
    input.mapIndexed { xIndex, s -> if (s.contains(char.toString())) Pos(xIndex, s.indexOf(char)) else Pos(-1,-1) }
        .filter { it != Pos(-1,-1) }

val root = findPosByValue('S').first()

fun solveA(): Int {

    val result = bfs('E', root, getEdges = { current ->
        listOf(
            current + Pos(-1, 0),
            current + Pos(1, 0),
            current + Pos(0, -1),
            current + Pos(0, 1)
        ).filterNot { it.x < 0 || it.x >= input.size }
            .filterNot { it.y < 0 || it.y >= input.first().length }
            .filter { getValue(it) - getValue(current) <= 1 }
            .map { BFS.Edge(it) }
    }, getValue = {
        input[it.x][it.y]
    })

    return result.size - 1
}

fun solveB(): Int {
    val results = findPosByValue('a').map {
        bfs('E', it, getEdges = { current ->
            listOf(
                current + Pos(-1, 0),
                current + Pos(1, 0),
                current + Pos(0, -1),
                current + Pos(0, 1)
            ).filterNot { it.x < 0 || it.x >= input.size }
                .filterNot { it.y < 0 || it.y >= input.first().length }
                .filter { getValue(it) - getValue(current) <= 1 }
                .map { BFS.Edge(it) }
        }, getValue = {
            input[it.x][it.y]
        })
    }
    return results.map { it.size -1 }.min()
}

fun main() {
    println("Answer A: ${solveA()}")
    println("Answer B: ${solveB()}")
}