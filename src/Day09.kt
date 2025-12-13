import kotlin.math.max
import kotlin.math.min
import kotlin.time.measureTime

fun main() {
    val testInput = readInput("Day09_test")
    check(part1(testInput).also { println("part 1 test actual: $it") } == 50L) { "part 1 failed" }
    check(part2(testInput).also { println("part 2 test actual: $it") } == 24L) { "part 2 failed" }

    val input = readInput("Day09")
    measureTime { part1(input).println("part 1 answer: ") }
        .also { time -> println("part 1 time elapsed: $time") }
    measureTime { part2(input).println("part 2 answer: ") }
        .also { time -> println("part 2 time elapsed: $time") }
}

private fun part1(input: List<String>): Long {
    val points = parsePoints(input)
    return points
        .indices
        .flatMap { i -> (i + 1 until points.size).asSequence().map { j -> Rectangle(points[i], points[j]).area() } }
        .max()
}

private fun part2(input: List<String>): Long {
    val vertices: List<Point> = parsePoints(input)
    val compactedPoints = compactPoints(vertices)
    val compactedVertices = vertices.map { compactedPoints.compact(it) }
    val compactedEdges: List<Edge> = vertices
        .asSequence()
        .windowed(2, 1)
        .map { (s, e) -> compactedPoints.compact(Edge.ordered(s, e)) }
        .toMutableList()
        .apply { add(compactedPoints.compact(Edge.ordered(vertices.last(), vertices.first()))) }

    var maxArea = 0L

    for (i in compactedVertices.indices) {
        for (j in i + 1 until compactedVertices.size) {
            val rect = Rectangle(compactedVertices[i], compactedVertices[j])
            val area = rect.scaledArea(compactedPoints)
            if (area > maxArea && rect.isInsidePolygon(compactedEdges)) {
                maxArea = area
            }
        }
    }

    return maxArea
}

private fun parsePoints(input: List<String>): List<Point> {
    return input
        .asSequence()
        .map { it.split(",") }
        .map { Point(it[0].toInt(), it[1].toInt()) }
        .toList()
}

private fun compactPoints(points: List<Point>): CompactedPoints {
    val indexedX = points
        .asSequence()
        .map { it.x }
        .distinct()
        .sorted()
        .withIndex()
        .toList()

    val indexedY = points
        .asSequence()
        .map { it.y }
        .distinct()
        .sorted()
        .withIndex()
        .toList()

    return CompactedPoints(
        indexedX.associateTo(HashMap()) { it.value to it.index },
        indexedX.associateTo(HashMap()) { it.index to it.value },
        indexedY.associateTo(HashMap()) { it.value to it.index },
        indexedY.associateTo(HashMap()) { it.index to it.value },
    )
}

private data class Point(val x: Int, val y: Int) {
    fun isOnEdge(edges: List<Edge>): Boolean {
        return edges.any { it.contains(this) }
    }

    // Ray casting algorithm
    fun isInsidePolygon(edges: List<Edge>): Boolean {
        return edges
            .count {
                it.isVertical()
                        && x > it.from.x
                        && y > it.minY && y <= it.maxY
            } % 2 == 1
    }
}

private data class Rectangle(val minX: Int, val minY: Int, val maxX: Int, val maxY: Int) {
    constructor(point1: Point, point2: Point) : this(
        min(point1.x, point2.x),
        min(point1.y, point2.y),
        max(point1.x, point2.x),
        max(point1.y, point2.y)
    )

    fun area(): Long {
        return (maxX - minX + 1).toLong() * (maxY - minY + 1)
    }

    fun scaledArea(compactedPoints: CompactedPoints): Long {
        return (compactedPoints.indexToX[maxX]!! - compactedPoints.indexToX[minX]!! + 1).toLong() *
                (compactedPoints.indexToY[maxY]!! - compactedPoints.indexToY[minY]!! + 1)
    }

    fun edges(): List<Edge> {
        return listOf(
            Edge(Point(minX, minY), Point(maxX, minY)),
            Edge(Point(minX, minY), Point(minX, maxY)),
            Edge(Point(maxX, maxY), Point(maxX, minY)),
            Edge(Point(maxX, maxY), Point(minX, maxY)),
        )
    }

    fun isInsidePolygon(polygonEdges: List<Edge>): Boolean {
        return edges()
            .asSequence()
            .flatMap { it.points() }
            .all { it.isOnEdge(polygonEdges) || it.isInsidePolygon(polygonEdges) }
    }
}

private data class Edge(val from: Point, val to: Point) {

    companion object {
        fun ordered(s: Point, e: Point): Edge {
            return if (s.x == e.x) {
                Edge(Point(s.x, min(s.y, e.y)), Point(e.x, max(s.y, e.y)))
            } else {
                Edge(Point(min(s.x, e.x), s.y), Point(max(s.x, e.x), s.y))
            }
        }
    }

    val minX = min(from.x, to.x)
    val minY = min(from.y, to.y)
    val maxX = max(from.x, to.x)
    val maxY = max(from.y, to.y)

    fun isVertical() = from.x == to.x

    fun points(): List<Point> {
        val result = mutableListOf<Point>()
        if (from.x == to.x) {
            for (y in minY..maxY) {
                result.add(Point(from.x, y))
            }
        } else {
            for (x in minX..maxX) {
                result.add(Point(x, from.y))
            }
        }
        return result
    }

    fun contains(point: Point): Boolean {
        if (isVertical()) {
            return point.x == from.x && point.y in minY..maxY
        }
        return point.y == from.y && point.x in minX..maxX
    }
}

private data class CompactedPoints(
    val xToIndex: Map<Int, Int>,
    val indexToX: Map<Int, Int>,
    val yToIndex: Map<Int, Int>,
    val indexToY: Map<Int, Int>
) {
    fun compact(point: Point): Point {
        return Point(xToIndex[point.x]!!, yToIndex[point.y]!!)
    }

    fun compact(edge: Edge): Edge {
        return Edge(compact(edge.from), compact(edge.to))
    }
}