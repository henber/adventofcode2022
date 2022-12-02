package day2

import readInput

val input = readInput("day2.txt")
    .map {
        val parts = it.split(" ")
        Pair(parts[0][0], parts[1][0])
    }

fun scoreRound(input: Pair<Char, Char>, offset: Int): Long {
    val winScore = computeRoundResult(input, offset)
    val choiceScore = computeChoiceScore(input.second)

    return winScore + choiceScore
}

fun computeChoiceScore(char: Char): Long {
    return when (char) {
        'X' -> 1
        'Y' -> 2
        'Z' -> 3
        else -> error("invalid choice")
    }
}

fun computeRoundResult(input: Pair<Char, Char>, offset: Int): Int {
    var result = (input.second - offset - input.first) % 3

    if (result < 0) result += 3

    return when (result) {
        0 -> 3 // draw
        1 -> 6 // win
        2 -> 0 // lose
        else -> error("invalid result")
    }
}

fun solveA(): Long {
    return input.fold(0L) { acc, pair ->
        acc + scoreRound(pair, 23)
    }
}

fun replaceByStrategy(pair: Pair<Char, Char>, offset: Int): Pair<Char, Char> {
    val wantedResult = when (pair.second) {
        'X' -> 2
        'Y' -> 0
        'Z' -> 1
        else -> error("unexpected char")
    }
    var newChar = pair.first + wantedResult + offset
    if (newChar > 'Z') newChar -= 3

    return Pair(pair.first, newChar)
}

fun solveB(): Long {
    return input
        .map { replaceByStrategy(it, 23) }
        .fold(0L) { acc, pair ->
            acc + scoreRound(pair, 23)
        }
}

fun main() {
    println("Answer A: ${solveA()}")
    println("Answer A: ${solveB()}")
}
