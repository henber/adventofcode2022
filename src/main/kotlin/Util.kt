import java.io.File
import java.lang.Integer.max
import java.time.Duration
import java.time.LocalDateTime
import java.util.ArrayDeque
import kotlin.math.abs
import kotlin.math.min

fun readInput(fileName: String): List<String>
    = File("src/main/resources/$fileName").readLines()

fun readInputText(fileName: String): String
    = File("src/main/resources/$fileName").readText()

tailrec fun <T> listPartition(list: List<T>, result: MutableList<List<T>> = mutableListOf(), includeSplitterAsPartition: Boolean = false, predicate: (T) -> Boolean): List<List<T>> {
    val partition = list.takeWhile(predicate)

    if (list.isEmpty()) {
        return result
    }

    if (partition.isNotEmpty()) {
        result.add(partition)
    }

    if (includeSplitterAsPartition) {
        result.add(list.drop(partition.size).take(1))
    }

    return listPartition(list.drop(partition.size + 1), result, includeSplitterAsPartition, predicate)
}

data class Pos(val x: Int, val y: Int): Comparable<Pos> {
    operator fun plus(other: Pos) = Pos(this.x + other.x, this.y + other.y)

    override fun compareTo(other: Pos): Int = toString().compareTo(other.toString())

    override fun toString(): String = "($x $y)"
}

data class Pos3(val x: Int, val y: Int, val z: Int): Comparable<Pos3> {
    operator fun plus(other: Pos3) = Pos3(this.x + other.x, this.y + other.y, this.z + other.z)

    override fun compareTo(other: Pos3): Int = toString().compareTo(other.toString())

    override fun toString(): String = "($x $y $z)"
}

fun manhattanDist(pos1: Pos, pos2: Pos): Int = abs(pos1.x - pos2.x) + abs(pos1.y - pos2.y)

fun manhattanDist(pos1: Pos3, pos2: Pos3): Int = abs(pos1.x - pos2.x) + abs(pos1.y - pos2.y) + abs(pos1.z - pos2.z)

fun <T> List<T>.listPartition(includeSplitterAsPartition: Boolean = false, predicate: (T) -> Boolean): List<List<T>> =
    listPartition(list = this, includeSplitterAsPartition = includeSplitterAsPartition, predicate = predicate)

fun <E> List<List<E>>.transpose(): List<List<E>> {
    if (isEmpty()) return this

    val width = first().size
    if (any { it.size != width }) {
        throw IllegalArgumentException("All nested lists must have the same size, but sizes were ${map { it.size }}")
    }

    return (0 until width).map { col ->
        (0 until size).map { row -> this[row][col] }
    }
}

fun <T, R> List<List<T>>.applyMask(size: Int, maskFunc: (T, List<T>) -> R): List<List<R>> {
    return this.mapIndexed { yIndex, xList ->
        xList.mapIndexed { xIndex, x ->
            val xNeighbourIndices = (max(xIndex - size, 0)..min(xIndex + size, xList.size - 1)).filter { it != xIndex }
            val yNeighbourIndices = (max(yIndex - size, 0)..min(yIndex + size, this.size - 1)).filter { it != yIndex }
            maskFunc.invoke(x, xNeighbourIndices.map { this[yIndex][it] } + yNeighbourIndices.map { this[it][xIndex] })
        }
    }
}

fun <A, B> lazyCartesianProduct(
    listA: Iterable<A>,
    listB: Iterable<B>
): Sequence<Pair<A, B>> =
    sequence {
        listA.forEach { a ->
            listB.forEach { b ->
                yield(a to b)
            }
        }
    }

fun <T> cartProduct(vararg iterables: List<T>): Sequence<List<T>> = sequence {

    require(iterables.map { it.size.toLong() }.reduce(Long::times) <= Int.MAX_VALUE) {
        "Cartesian product function can produce result whose size does not exceed Int.MAX_VALUE"
    }

    val numberOfIterables = iterables.size
    val lstLengths = ArrayList<Int>()
    val lstRemaining = ArrayList(listOf(1))

    iterables.reversed().forEach {
        lstLengths.add(0, it.size)
        lstRemaining.add(0, it.size * lstRemaining[0])
    }

    val nProducts = lstRemaining.removeAt(0)

    (0 until nProducts).forEach { product ->
        val result = ArrayList<T>()
        (0 until numberOfIterables).forEach { iterableIndex ->
            val elementIndex = product / lstRemaining[iterableIndex] % lstLengths[iterableIndex]
            result.add(iterables[iterableIndex][elementIndex])
        }
        yield(result.toList())
    }
}

fun <T> List<List<T>>.rotate(steps: Int): List<List<T>> {
    val actualSteps = if (steps >= 0) steps % 4 else (steps % 4) + 4

    return when (actualSteps) {
        0 -> this
        1 -> this.transpose().map { it.reversed() }
        2 -> this.rotate(1).rotate(1)
        3 -> this.map { it.reversed() }.transpose()
        else -> error("unexpected steps: $actualSteps")
    }
}

fun <T> List<List<T>>.flipColumns(): List<List<T>> {
    return this.map { it.reversed() }
}

fun <T> List<List<T>>.flipRows(): List<List<T>> {
    return this.reversed()
}

fun List<Char>.asString(): String = String(this.toCharArray())

fun <T> measureTime(loggingFunction: ((Duration) -> Unit)? = null, function: () -> T): T {

    val startTime = LocalDateTime.now()
    val result: T = function.invoke()
    val duration = Duration.between(startTime, LocalDateTime.now())
    if(loggingFunction == null) {
        println("Time taken: $duration")
    } else {
        loggingFunction.invoke(duration)
    }

    return result
}

fun <T> printMatrix(matrix: List<List<T>>) {
    matrix.forEach {
        println(it.joinToString(separator = "") { it.toString() })
    }
    println()
}


class BFS {
    class Edge<C>(val coordinate: C)
}
fun <C, V> bfs(target: V, root: C, getEdges: (C) -> Collection<BFS.Edge<C>>, getValue: (C) -> V): List<C> {
    return bfs(target = target, root = root, duplicatesAllowed = false, getEdges =
    {c, cs -> getEdges(c) }, getValue = getValue)
}

fun <C, V> bfs(target: V, root: C, duplicatesAllowed: Boolean, getEdges: (C, List<C>) -> Collection<BFS.Edge<C>>, getValue: (C) -> V): List<C> {
    val queue = ArrayDeque <Pair<BFS.Edge<C>, List<C>>>()
    val visited = mutableSetOf(root)
    queue.add(Pair(BFS.Edge(root), listOf(root)))

    while (queue.isNotEmpty()) {

        val current = queue.removeFirst()
        if (getValue(current.first.coordinate) == target) {
            return current.second
        }

        getEdges(current.first.coordinate, current.second).forEach {
            if (duplicatesAllowed || !visited.contains(it.coordinate)) {
                visited.add(it.coordinate)
                queue.add(Pair(BFS.Edge(it.coordinate), current.second + listOf(it.coordinate)))
            }
        }
    }

    return emptyList()
}

fun <C, V> dfs(target: V, root: C, getEdges: (C) -> Collection<BFS.Edge<C>>, getValue: (C) -> V): List<C> {
    return bfs(target = target, root = root, duplicatesAllowed = false, getEdges =
    {c, cs -> getEdges(c) }, getValue = getValue)
}

fun <C, V> dfs(target: V, root: C, duplicatesAllowed: Boolean, getEdges: (C, List<C>) -> Collection<BFS.Edge<C>>, getValue: (C) -> V): List<C> {
    val stack = ArrayDeque <Pair<BFS.Edge<C>, List<C>>>()
    val visited = mutableSetOf(root)
    stack.add(Pair(BFS.Edge(root), listOf(root)))

    while (stack.isNotEmpty()) {

        val current = stack.removeLast()
        if (getValue(current.first.coordinate) == target) {
            return current.second
        }

        getEdges(current.first.coordinate, current.second).forEach {
            if (duplicatesAllowed || !visited.contains(it.coordinate)) {
                visited.add(it.coordinate)
                stack.add(Pair(BFS.Edge(it.coordinate), current.second + listOf(it.coordinate)))
            }
        }
    }

    return emptyList()
}