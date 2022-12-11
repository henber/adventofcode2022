package day11

import crm
import listPartition
import readInput

val input = readInput("day11.txt")

var monkeys: List<Monkey> = parseMonkeys()

fun parseMonkeys(): List<Monkey> {
    return listPartition(input, mutableListOf()) {
        it.isNotBlank()
    }.map {
        val items = it[1].replace(",", "").split(" ")
            .filter { it.isNotBlank() }.drop(2)
            .toList().map { it.toLong() }
        val (operation, operationValue) = it[2].split(" ").filter { it.isNotBlank() }.drop(4)
        val test = it[3].split(" ").last().toLong()
        val targetIfTrue = it[4].split(" ").last().toInt()
        val targetIfFalse = it[5].split(" ").last().toInt()

        Monkey(
            items.toMutableList(),
            if (operation == "*") Operation.MULTIPLY else Operation.ADD,
            operationValue.toLongOrNull(),
            test, targetIfTrue, targetIfFalse
        )
    }
}

enum class Operation {
    MULTIPLY, ADD;

    fun apply(old: Long, value: Long?): Long {
        return when(this) {
            MULTIPLY -> old * (value ?: old)
            ADD -> old + (value ?: old)
        }
    }
}

data class Monkey(
    val items: MutableList<Long>,
    val operation: Operation,
    val operationValue: Long?,
    val test: Long,
    val targetIfTrue: Int,
    val targetIfFalse: Int
) {
    var inspectedItems: Int = 0
}

fun takeTurn(monkey: Monkey) {
    monkey.items.forEach {
        val worryLevel =
            monkey.operation.apply(it, monkey.operationValue) / 3

        val targetMonkey =
            if (worryLevel % monkey.test == 0L) monkey.targetIfTrue
            else monkey.targetIfFalse

        monkeys[targetMonkey].items.add(worryLevel)
        monkey.inspectedItems += 1
    }
    monkey.items.clear()
}

fun doRound() {
    monkeys.forEach { takeTurn(it) }
}

val divisors = monkeys.map { it.test }

fun takeTurnCrm(monkey: Monkey) {
    monkey.items.forEach {
        val worryLevel =
            crm(divisors.map { div -> monkey.operation.apply(it, monkey.operationValue) % div }, divisors)

        val targetMonkey =
            if (worryLevel % monkey.test == 0L) monkey.targetIfTrue
            else monkey.targetIfFalse

        monkeys[targetMonkey].items.add(worryLevel)
        monkey.inspectedItems += 1
    }
    monkey.items.clear()
}

fun solveA(): Long {
    monkeys = parseMonkeys()
    repeat(20) { doRound() }
    return monkeys.map { it.inspectedItems }
        .sortedDescending()
        .take(2)
        .fold(1) { acc, i -> acc * i }
}

fun solveB(): Long {
    monkeys = parseMonkeys()
    repeat(10) {
        repeat(1000) { monkeys.forEach { takeTurnCrm(it) } }
    }
    return monkeys.map { it.inspectedItems }
        .sortedDescending()
        .take(2)
        .fold(1) { acc, i -> acc * i }
}

fun main() {
    println("AnswerA: ${solveA()}")
    println("AnswerB: ${solveB()}")
}