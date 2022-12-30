package day20

import readInput
import kotlin.math.abs

val input = readInput("day20.txt").mapIndexed { index, s ->  Pair(index, s.toLong()) }

class LinkedNodes {
    class Node(val value: Pair<Int, Long>) {
        lateinit var next: Node
        lateinit var prev: Node

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Node

            if (value != other.value) return false

            return true
        }

        override fun hashCode(): Int {
            return value.hashCode()
        }


    }

    var start: Node = NULL_NODE

    companion object {
        val NULL_NODE = Node(Pair(-1, -1)).apply {
            this.next = this
            this.prev = this
        }
    }

    fun addNode(node: Node) {
        if (start.value == Pair(-1, -1L)) {
            start = node
            node.next = node
            node.prev = node
            return
        }
        addAfterNode(node, start.prev)
    }

    fun addAfterNode(nodeToAdd: Node, after: Node) {
        val nextNode = after.next
        after.next = nodeToAdd
        nextNode.prev = nodeToAdd
        nodeToAdd.prev = after
        nodeToAdd.next = nextNode
    }

    fun removeNode(nodeToRemove: Node) {
        val next = nodeToRemove.next
        val prev = nodeToRemove.prev
        next.prev = prev
        prev.next = next
        nodeToRemove.next = nodeToRemove
        nodeToRemove.prev = nodeToRemove
    }

    fun moveAfter(nodeToMove: Node, after: Node) {
        if (nodeToMove == after) {
          return
        }
        removeNode(nodeToMove)
        addAfterNode(nodeToMove, after)
    }

    fun moveBefore(nodeToMove: Node, before: Node) {
        moveAfter(nodeToMove, before.prev)
    }

    fun takeSteps(from: Node, steps: Long): Node {
        if (steps == 0L) return from

        var result: Node = from
        var adjustedSteps = steps
        while (abs(adjustedSteps) >= input.size) {
            adjustedSteps = (adjustedSteps % input.size) + adjustedSteps / input.size
        }

        adjustedSteps += if(adjustedSteps < 0) input.size else 0
        repeat(adjustedSteps.toInt()) {
            result = result.next
        }
        return result
    }

    fun find(predicate: (Node) -> Boolean): Node {
        var node = start
        do {
            if (predicate(node)) return node
            node = node.next
        } while (node != start)
        return NULL_NODE
    }

    fun count(): Int {
        var node = start
        var count = 0
        while (node.next != start && node != NULL_NODE) {
            count += 1
            node = node.next
        }
        return count + if (start != NULL_NODE) 1 else 0
    }

    fun shiftStart(predicate: (Node) -> Boolean) {
        val node = find(predicate)
        if (node != NULL_NODE) {
            this.start = node
        }
    }

    override fun toString(): String {
        val builder = StringBuilder()
        var node = start
        builder.append('[')
        do {
            builder.append("${node.value}, ")
            node = node.next
        } while (node != start)
        return builder.dropLast(2).toString() + ']'
    }
}

fun solveA(): Long {
    val nodeChain = LinkedNodes()
    val nodes = input.map {
        val node = LinkedNodes.Node(it)
        nodeChain.addNode(node)
        node
    }

    nodes.forEach {
        val target = nodeChain.takeSteps(it, it.value.second)
        if (it.value.second < 0) {
            nodeChain.moveBefore(it, target)
        } else nodeChain.moveAfter(it, target)
    }

    val zero = nodeChain.find { it.value.second == 0L }
    return listOf(1000, 2000, 3000).map {
        nodeChain.takeSteps(zero, it.toLong()).value.second.also { println(it) }
    }.sum()
}

fun solveB(): Long {
    val nodeChain = LinkedNodes()
    val decryptionKey = 811589153
    val nodes = input.map {
        val node = LinkedNodes.Node(Pair(it.first, it.second * decryptionKey))
        nodeChain.addNode(node)
        node
    }

    repeat(10) {
        nodes.forEach {
            val target = nodeChain.takeSteps(it, it.value.second)
            if (it.value.second < 0) {
                nodeChain.moveBefore(it, target)
            } else nodeChain.moveAfter(it, target)
        }
        println("iteration done")
    }

    val zero = nodeChain.find { it.value.second == 0L }
    return listOf(1000, 2000, 3000).map {
        nodeChain.takeSteps(zero, it.toLong()).value.second.also { println(it) }
    }.sum()
}

fun main() {
    println("Answer A: ${solveA()}")
    println("Answer B: ${solveB()}")
}