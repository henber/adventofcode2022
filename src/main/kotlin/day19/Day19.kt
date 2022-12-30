package day19

import day11.takeTurn
import measureTime
import product
import readInput

val blueprintRegex = Regex("""Blueprint (\d+): Each ore robot costs (\d+) ore. Each clay robot costs (\d+) ore. Each obsidian robot costs (\d+) ore and (\d+) clay. Each geode robot costs (\d+) ore and (\d+) obsidian.""")
val input = readInput("day19.txt")

val blueprints = input
    .mapNotNull { blueprintRegex.find(it)?.groupValues?.drop(1) }
    .map { it.map { it.toInt() } }
    .map { Blueprint(it[0], it[1], it[2], it[3], it[4], it[5], it[6]) }

data class Blueprint(
    val id: Int,
    val oreRobotOreCost: Int,
    val clayRobotOreCost: Int,
    val obsidianRobotOreCost: Int,
    val obsidianRobotClayCost: Int,
    val geodeRobotOreCost: Int,
    val geodeRobotObsidianCost: Int,
) {
    fun canAfford(robotType: RobotType, currentStatus: CurrentStatus): Boolean {
        return when(robotType) {
            RobotType.NONE -> true
            RobotType.ORE -> canAffordOreRobot(currentStatus)
            RobotType.CLAY -> canAffordClayRobot(currentStatus)
            RobotType.OBSIDIAN -> canAffordObsidianRobot(currentStatus)
            RobotType.GEODE -> canAffordGeodeRobot(currentStatus)
        }
    }

    private fun canAffordGeodeRobot(currentStatus: CurrentStatus): Boolean =
        geodeRobotOreCost <= currentStatus.ore && geodeRobotObsidianCost <= currentStatus.obsidian

    private fun canAffordObsidianRobot(currentStatus: CurrentStatus): Boolean =
        obsidianRobotOreCost <= currentStatus.ore && obsidianRobotClayCost <= currentStatus.clay

    private fun canAffordClayRobot(currentStatus: CurrentStatus): Boolean =
        clayRobotOreCost <= currentStatus.ore

    private fun canAffordOreRobot(currentStatus: CurrentStatus): Boolean =
        oreRobotOreCost <= currentStatus.ore

}

enum class RobotType {
    ORE, CLAY, OBSIDIAN, GEODE, NONE
}

fun buildRobotActions(currentStatus: CurrentStatus, blueprint: Blueprint): Set<RobotType> =
    RobotType.values().filter { blueprint.canAfford(it, currentStatus) }.toSet()

data class CurrentStatus(
    var ore: Int = 0,
    var clay: Int = 0,
    var obsidian: Int = 0,
    var geode: Int = 0,

    var oreRobots: Int = 1,
    var clayRobots: Int = 0,
    var obsidianRobots: Int = 0,
    var geodeRobots: Int = 0,

    var takenTurns: Int = 1,
) {
    fun gatherResources() {
        ore += oreRobots
        clay += clayRobots
        obsidian += obsidianRobots
        geode += geodeRobots
    }

    fun fundRobot(type: RobotType, blueprint: Blueprint) {
        when(type) {
            RobotType.GEODE -> {
                ore -= blueprint.geodeRobotOreCost
                obsidian -= blueprint.geodeRobotObsidianCost
            }
            RobotType.OBSIDIAN -> {
                ore -= blueprint.obsidianRobotOreCost
                clay -= blueprint.obsidianRobotClayCost
            }
            RobotType.CLAY -> ore -= blueprint.clayRobotOreCost
            RobotType.ORE -> ore -= blueprint.oreRobotOreCost
            RobotType.NONE -> Unit
        }
    }

    fun createRobot(type: RobotType) {
        when(type) {
            RobotType.GEODE -> geodeRobots += 1
            RobotType.OBSIDIAN -> obsidianRobots += 1
            RobotType.CLAY -> clayRobots += 1
            RobotType.ORE -> oreRobots += 1
            RobotType.NONE -> Unit
        }
    }
}

val maxGeodes = mutableMapOf<Int, Int>()

val cache = mutableMapOf<Pair<CurrentStatus, Int>, Int>()

fun getMaximumGeodes(currentStatus: CurrentStatus, blueprint: Blueprint, turns: Int): Int {

    val cacheEntry = cache[Pair(currentStatus, blueprint.id)]
    if (cacheEntry != null) {
        return cacheEntry
    }

    if (currentStatus.takenTurns > turns
        || (maxGeodes[currentStatus.takenTurns] ?: 0) >= currentStatus.geode + 6 // prova med högre
        || (currentStatus.takenTurns > 10 && currentStatus.clayRobots == 0)
        || (currentStatus.takenTurns > 17 && currentStatus.obsidianRobots == 0)
    ) {
        return currentStatus.geode
    }

    val types = buildRobotActions(currentStatus, blueprint)

    return types.map { type ->
        val status = currentStatus.copy()
        status.fundRobot(type, blueprint)

        status.gatherResources()

        status.createRobot(type)

        val maxCache = maxGeodes[status.takenTurns]
        if (maxCache == null) maxGeodes[status.takenTurns] = status.geode
        else if (status.geode > maxCache) maxGeodes[status.takenTurns] = status.geode

        cache[Pair(currentStatus, blueprint.id)] = getMaximumGeodes(status.apply { this.takenTurns += 1 }, blueprint, turns)
        cache[Pair(currentStatus, blueprint.id)]!!
    }.max()
}

fun solveA(): Int = blueprints.sumOf {
    measureTime {
        maxGeodes.clear()
        cache.clear()
        getMaximumGeodes(CurrentStatus(), it, 24) * it.id
    }
}

fun solveB(): Long = blueprints.take(3).map {
    measureTime {
        maxGeodes.clear()
        cache.clear()
        getMaximumGeodes(CurrentStatus(), it, 32).also { println(it) }
    }
}.product()

fun main() {
//    println("Answer A: ${solveA()}") // 1659
    println("Answer B: ${solveB()}")
}
