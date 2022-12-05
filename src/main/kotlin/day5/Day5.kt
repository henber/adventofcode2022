package day5

import readInput

val stacksRegex = Regex("([A-Z]|\\s\\s\\s)")
val numberRegex = Regex("(\\d+)")

val input = readInput("day5.txt")

val stacksInput = input.takeWhile {
    stacksRegex.containsMatchIn(it)
}

var stacks: List<ArrayDeque<Char>> = emptyList()

val moves = input.drop(stacksInput.size + 1)
    .takeWhile { numberRegex.containsMatchIn(it) }
    .map {
        val regexResult = numberRegex.findAll(it).toList()
        Triple(
            regexResult[0].groups[1]?.value?.toInt()!!,
            regexResult[1].groups[1]?.value?.toInt()!!,
            regexResult[2].groups[1]?.value?.toInt()!!
        )
    }

fun fillStacks() {
    stacks = (0 until 9).map { ArrayDeque() }

    stacksInput.forEach {
        val regexResult = stacksRegex.findAll(it).map {
            it.groups[1]?.value
        }
        regexResult.forEachIndexed { index, value ->
            if (value!!.isBlank()) return@forEachIndexed
            stacks[index].addLast(value[0])
        }
    }
}

fun solveA(): String {
    fillStacks()
    moves.forEach { triple ->
        (0 until triple.first).forEach {
            stacks[triple.third - 1].addFirst(stacks[triple.second - 1].removeFirst())
        }
    }

    return stacks.filter { it.isNotEmpty() }.map { it.first() }.joinToString(separator = "") { "$it" }
}

fun solveB(): String {
    fillStacks()
    moves.forEach { triple ->
        val toMove = mutableListOf<Char>()
        (0 until triple.first).forEach {
            toMove.add(stacks[triple.second - 1].removeFirst())
        }
        toMove.reversed().forEach { stacks[triple.third - 1].addFirst(it) }
    }

    return stacks.filter { it.isNotEmpty() }.map { it.first() }.joinToString(separator = "") { "$it" }
}

fun main() {
    println("Answer A: ${solveA()}")
    println("Answer A: ${solveB()}")
}
