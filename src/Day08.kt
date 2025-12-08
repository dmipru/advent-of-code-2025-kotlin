import java.util.*
import kotlin.math.sqrt

fun main() {
    val testInput = readInput("Day08_test")
    check(part1(testInput, 10).also { println("part 1 test actual: $it") } == 40) { "part 1 failed" }
    check(part2(testInput).also { println("part 2 test actual: $it") } == 25272) { "part 2 failed" }

    val input = readInput("Day08")
    part1(input, 1000).println("part 1 answer: ")
    part2(input).println("part 2 answer: ")
}

private fun part1(input: List<String>, maxConnections: Int): Int {
    val boxes = parseBoxes(input)
    val connections = getConnections(boxes)

    val pq = PriorityQueue<Connection>(Comparator.comparingDouble { it.dist })
        .also { it += connections }

    var connectionsCount = 0

    while (pq.isNotEmpty() && connectionsCount < maxConnections) {
        val connection = pq.poll()
        connection.connect()
        connectionsCount++
    }

    return boxes
        .asSequence()
        .map { it.getCircuitRoot() }
        .groupingBy { it }
        .eachCount()
        .values
        .asSequence()
        .sortedDescending()
        .take(3)
        .reduce { acc, it -> acc * it }
}

private fun part2(input: List<String>): Int {
    val boxes = parseBoxes(input)
    val connections = getConnections(boxes)

    val pq = PriorityQueue<Connection>(Comparator.comparingDouble { it.dist })
        .also { it += connections }

    var lastConnection: Connection? = null

    while (pq.isNotEmpty()) {
        val connection = pq.poll()
        if (connection.connect()) {
            lastConnection = connection
        }
    }

    return lastConnection!!.from.x * lastConnection.to.x
}

private fun parseBoxes(input: List<String>): List<Box> {
    return input
        .asSequence()
        .map { it.split(",").map { num -> num.toInt() } }
        .map { Box(it[0], it[1], it[2]) }
        .toList()
}

private fun getConnections(boxes: List<Box>): List<Connection> {
    val connections = mutableListOf<Connection>()

    for (i in boxes.indices) {
        for (j in i + 1 until boxes.size) {
            connections.add(Connection(boxes[i], boxes[j]))
        }
    }

    return connections
}

private data class Box(val x: Int, val y: Int, val z: Int) {
    private var circuitHead: Box = this

    fun getCircuitRoot(): Box {
        var box = circuitHead
        while (box != box.circuitHead) {
            box = box.circuitHead.circuitHead
        }
        return box
    }

    fun connect(otherBox: Box): Boolean {
        val root = this.getCircuitRoot()
        val otherRoot = otherBox.getCircuitRoot()
        if (root != otherRoot) {
            otherRoot.circuitHead = root
            return true
        }
        return false
    }

    fun distanceTo(other: Box): Double {
        val xd = (this.x - other.x).toDouble()
        val yd = (this.y - other.y).toDouble()
        val zd = (this.z - other.z).toDouble()
        return sqrt((xd * xd + yd * yd + zd * zd))
    }
}

private data class Connection(val from: Box, val to: Box) {
    val dist: Double = from.distanceTo(to)

    fun connect(): Boolean {
        return from.connect(to)
    }
}