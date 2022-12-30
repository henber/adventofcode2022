package day16

import BFS
import bfs
import readInput

val inputRegex = Regex("Valve ([A-Z]+) has flow rate=(\\d+);")

val input = readInput("day16.txt")
    .map {
        Pair(inputRegex.find(it)?.groupValues?.drop(1),
            it.split(";")[1].split(" ")
                .filter { it.isNotBlank() }
                .drop(4)
                .map { it.replace(",", "") })
    }

val caveMap: Map<String, Pair<Int, Set<String>>> = input.associate {
    Pair(it.first!![0], Pair(it.first!![1].toInt(), it.second.toSet()))
}

val nodesWithValue = caveMap.entries.filter { it.value.first > 0 }

val computedPaths = mutableMapOf<Pair<String, String>, List<String>>()

fun pathToTarget(current: String, target: String): List<String> {
    if (current == finishedRoute.from || target == finishedRoute.from) return emptyList()

    val computedValue = computedPaths[Pair(current, target)]

    if (computedValue != null) return computedValue

    val result = bfs(target, current, {
        caveMap[it]!!.second.map { BFS.Edge(it) }
    }) {
        it
    }

    computedPaths[Pair(current, target)] = result
    computedPaths[Pair(target, current)] = result.reversed()

    return result
}

data class Route(val from: String, val to: Pair<String, Pair<Int, Set<String>>>) {

    constructor(from: String, to: Map.Entry<String, Pair<Int, Set<String>>>): this(from, to.toPair())

    val path: List<String> by lazy { pathToTarget(from, to.first) }

    val cost: Int by lazy { path.size }

    fun value(remainingTurns: Int): Int = (remainingTurns - path.size) * to.second.first

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Route

        if (from != other.from) return false
        if (to != other.to) return false

        return true
    }

    override fun hashCode(): Int {
        var result = from.hashCode()
        result = 31 * result + to.hashCode()
        return result
    }
}

val finishedRoute = Route("FINISHED", Pair("FINISHED", Pair(0, emptySet())) )

val valuableRoutes = (nodesWithValue + caveMap.entries.first { it.key == "AA" }).associate { outer ->
    Pair(outer.key, nodesWithValue.mapNotNull { inner ->
        if (inner.key == outer.key) null
        else {
            Route(outer.key, inner)
        }
    })
}

fun findValuablePath(start: String,
                     path: Set<String> = setOf(),
                     valuableVisited: Set<String>,
                     currentValue: Int,
                     remainingTurns: Int): List<Pair<Int, Set<String>>> {
    val routes = valuableRoutes[start]!!
    val valuableRemaining = routes.filter { !valuableVisited.contains(it.to.first) }
        .filter { it.cost <= remainingTurns }

    if (valuableRemaining.isEmpty()) return listOf(Pair(currentValue, path))

    return valuableRemaining.flatMap {
        findValuablePath(
            it.to.first,
            path + it.to.first,
            valuableVisited + it.to.first,
            currentValue + it.value(remainingTurns),
            remainingTurns - it.path.size
        )
    }
}

fun solveA(): Int {
    val result = findValuablePath(
        start = "AA",
        valuableVisited = emptySet(),
        currentValue = 0,
        remainingTurns = 30
    )
    return result.maxBy { it.first }.first
}

fun solveB(): Int {
    val result = findValuablePath(
        start = "AA",
        valuableVisited = emptySet(),
        currentValue = 0,
        remainingTurns = 26
    )
    val joinedResults = result.map { outer ->
        Pair(outer, result.filter { inner ->
            !inner.second.any { outer.second.contains(it) }
        }.maxBy { it.first })
    }

    val joinedMaxResult = joinedResults.maxBy { it.first.first + it.second.first }
    return joinedMaxResult.first.first + joinedMaxResult.second.first
}

fun main() {
    println("Answer A: ${solveA()}") // [CH, VQ, SG, ML, AT, BD, SG, GL, DX] // 2029
    println("Answer B: ${solveB()}") // 2723
}