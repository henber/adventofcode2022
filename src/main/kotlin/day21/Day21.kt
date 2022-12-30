package day21

import readInput

val input = readInput("day21.txt")
val monkeyRegex = Regex("""([a-z]+): (\d+|[a-z]+)\s?([+\-*/])?\s?([a-z]+)?""")

val monkeys = input.map { monkeyRegex.find(it)?.groupValues?.drop(1)!! }.map { it.filter { it.isNotBlank() } }

fun compute(firstOperand: Long, operation: String, secondOperand: Long): Long {
    return when(operation) {
        "+" -> firstOperand + secondOperand
        "-" -> firstOperand - secondOperand
        "*" -> firstOperand * secondOperand
        "/" -> firstOperand / secondOperand
        "=" -> firstOperand.compareTo(secondOperand).toLong()
        else -> error("invalid operation")
    }
}

fun solveRiddle(monkeys: List<List<String>>): Long {
    val queue: ArrayDeque<List<String>> = ArrayDeque()
    val valueMap = mutableMapOf<String, Long>()
    valueMap.putAll(monkeys.filter { it.size == 2 }.map { Pair(it[0], it[1].toLong()) })
    queue.addAll(monkeys.filter { it.size != 2 })

    while(queue.isNotEmpty()) {
        val current = queue.removeFirst()

        val firstOperand = valueMap[current[1]]
        val secondOperand = valueMap[current[3]]
        if (firstOperand != null && secondOperand != null) { // root: 53428542654097, 21718827469549
            valueMap.put(current[0], compute(firstOperand, current[2], secondOperand))
        } else {
            queue.addLast(current)
        }
    }

    return valueMap["root"]!!
}

fun solveA(): Long = solveRiddle(monkeys)

fun LongRange.binarySearch(target: Long, valueFunc: (Long) -> Long, compareFunc: (Long, Long) -> Int): Long {
    var low = this.first
    var high = this.last

    while (low <= high) {
        val mid = (low + high).ushr(1) // safe from overflows
        val midVal = valueFunc(mid)
        val cmp = compareFunc(target, midVal)

        if (cmp < 0)
            low = mid + 1
        else if (cmp > 0)
            high = mid - 1
        else
            return mid // key found
    }
    return -(low + 1)  // key not found
}

fun solveB(): Long {
    val monkeys = input.map { monkeyRegex.find(it)?.groupValues?.drop(1)!! }
        .map { it.filter { it.isNotBlank() } }
        .map { if (it[0] == "root") listOf(it[0], it[1], "=", it[3]) else it }
        .filter { it[0] != "humn" }

    return (0..53428450070572).binarySearch(0L,
        { solveRiddle(monkeys + listOf(listOf("humn", it.toString()))
        )}) { l, l2 ->
        l2.toInt() * -1
    }
}


fun main() {
    println("Answer A: ${solveA()}")
    println("Answer B: ${solveB()}")
}
