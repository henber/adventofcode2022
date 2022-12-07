package day7

import readInput
import java.util.Deque
import kotlin.contracts.contract

val input = readInput("day7.txt")

interface Component {
    val name: String

    fun size(): Long

    fun allDirSizes(): List<Pair<String, Long>>
}

class Folder(override val name: String, val parent: Folder?) : Component {

    val components: MutableList<Component> = mutableListOf()

    override fun size(): Long {
        return components.sumOf { it.size() }
    }

    override fun allDirSizes(): List<Pair<String, Long>> {
        return components.map { it.allDirSizes() }.flatten() + Pair(name, size())
    }
}

class File(override val name: String, val size: Long) : Component {

    override fun size(): Long = size

    override fun allDirSizes(): List<Pair<String, Long>> {
        return emptyList()
    }
}

fun <T> ArrayDeque<T>.removeWhile(predicate: (T) -> Boolean): List<T> {
    val mutableList = mutableListOf<T>()
    while(this.isNotEmpty() && predicate(this.first())) {
        mutableList.add(this.removeFirst())
    }
    return mutableList
}

fun buildDirectoryStructure(instructions: ArrayDeque<String>): Component {
    val root = Folder("/", null)
    var currentFolder = root

    while(instructions.isNotEmpty()) {
        val instruction = instructions.removeFirst()
        val subInstructions = instructions.removeWhile { !it.startsWith("\$") }

        currentFolder = runInstruction(instruction, subInstructions, currentFolder)
    }
    return root
}

fun runInstruction(
    instruction: String,
    subInstructions: List<String>,
    currentFolder: Folder
): Folder {
    when (instruction) {
        "\$ ls" -> createFilesAndFolders(subInstructions, currentFolder)
        "\$ cd .." -> return currentFolder.parent!!
        else -> {
            if (!instruction.startsWith("\$ cd")) error("Invalid instruction: $instruction")
            return currentFolder.components.first { it.name == instruction.split(" ").last() } as Folder
        }
    }
    return currentFolder
}

fun createFilesAndFolders(subInstructions: List<String>, currentFolder: Folder) {
    subInstructions.map { it.split(" ") }
        .forEach {
            when(it[0]) {
                "dir" -> currentFolder.components.add(Folder(it[1], currentFolder))
                else -> currentFolder.components.add(File(it[1], it[0].toLong()))
            }
        }
}

fun solveA(): Long {
    val inputQueue = ArrayDeque(input.drop(1))
    val root = buildDirectoryStructure(inputQueue)

    return root.allDirSizes().filter { it.second <= 100000 }
        .sumOf { it.second }
}

fun solveB(): Long {
    val inputQueue = ArrayDeque(input.drop(1))
    val root = buildDirectoryStructure(inputQueue)

    val totalSpace = 70000000
    val neededSpace = 30000000
    val currentFreeSpace = totalSpace - root.size()
    val needToDelete = neededSpace - currentFreeSpace

    return root.allDirSizes().filter { it.second >= needToDelete }
        .minByOrNull { it.second }!!.second
}

fun main() {
    println("Answer A: ${solveA()}")
    println("Answer B: ${solveB()}")
}

