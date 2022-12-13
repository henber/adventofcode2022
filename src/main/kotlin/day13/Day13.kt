package day13

import listPartition
import product
import readInput

val input = readInput("day13.txt")


interface Composite: Comparable<Composite> {
    override fun compareTo(other: Composite): Int {
        return when(compare(this, other)) {
            true -> -1
            false -> 1
            else -> 0
        }
    }
}

class Container(val values: MutableList<Composite> = mutableListOf()): Composite {
    override fun toString(): String = values.toString()
}

class Value(val value: Int, val parent: Container): Composite {
    override fun toString(): String = value.toString()
}

fun parseContainer(line: String, index: Int, parent: Container?): Pair<Int, Container> {
    if (!line.startsWith("[")) error("not a container")
    if (line.drop(1).take(1) == "]") return Pair(-1, Container(mutableListOf()))

    val container = Container()
    parent?.values?.add(container)

    var stringIndex = index + 1

    while (stringIndex < line.length) {
        when (line[stringIndex]) {
            '[' -> stringIndex = parseContainer(line, stringIndex, container).first
            ']' -> return Pair(stringIndex + 1, container)
            else -> stringIndex = parseValues(line, stringIndex, container)
        }
    }

    return Pair(stringIndex, container)
}

fun parseValues(line: String, index: Int, parent: Container): Int {
    val subString = line.substring(index).takeWhile { it != '[' && it != ']' }

    parent.values.addAll(subString.split(",")
        .filter { it.isNotBlank() }
        .map { Value(it.toInt(), parent) })

    return subString.length + index
}

fun parseRoot(line: String): Container {
    return parseContainer(line, 0, null).second
}

fun compare(left: Composite, right: Composite): Boolean? {
    return when {
        left is Container && right is Container -> {
            var result: Boolean?
            var index = 0
            do {
                if (index == left.values.size && index < right.values.size) {
                    return true
                }
                if (index < left.values.size && index == right.values.size) {
                    return false
                }
                if (index == left.values.size && index == right.values.size) return null
                result = compare(left.values[index], right.values[index])
                index += 1
            } while(result == null)
            result
        }
        left is Container && right is Value -> return compare(left, Container(mutableListOf(right)))
        left is Value && right is Container -> return compare(Container(mutableListOf(left)), right)
        else -> return when ((left as Value).value.compareTo((right as Value).value)) {
            0 -> null
            1 -> false
            -1 -> true
            else -> error("invalid compare")
        }
    }
}

fun solveA(): Int {
    val packets = input.listPartition { it.isNotBlank() }.map {
        it.map { parseRoot(it) } }
        .map { Pair(it[0], it[1]) }
        .mapIndexed { index, pair ->
            Pair(index + 1, compare(pair.first, pair.second))
        }

    return packets.filter { it.second!! }.sumOf { it.first }
}

fun solveB(): Long {
    val packets = readInput("day13.txt")
        .filter { it.isNotBlank() } + listOf("[[2]]", "[[6]]")

    return packets.asSequence().map { parseRoot(it) }.sorted()
        .mapIndexed { index, composite ->
            Pair(index + 1, composite.toString())
        }
        .filter { it.second == "[[2]]" || it.second == "[[6]]" }
        .map { it.first }
        .product()
}

fun main() {
    println("Answer A: ${solveA()}")
    println("Answer B: ${solveB()}")
}

