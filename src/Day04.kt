fun main() {
    val testInput = readInput("Day04_test")
    check(part1(testInput).also { println("part 1 test actual: $it") } == 13) { "part 1 failed" }
    check(part2(testInput).also { println("part 2 test actual: $it") } == 43) { "part 2 failed" }

    val input = readInput("Day04")
    part1(input).println("part 1 answer: ")
    part2(input).println("part 2 answer: ")
}

private const val ROLL = '@'
private const val ADJACENT_LIMIT = 4

private fun part1(input: List<String>): Int {
    val grid = input.map { it.toCharArray() }

    return grid.indices
        .asSequence()
        .map { i ->
            grid[0]
                .indices
                .asSequence()
                .filter { j -> grid[i][j] == ROLL }
                .filter { j -> countAdjacent(grid, i, j) < ADJACENT_LIMIT }
                .count()
        }
        .sum()
}

private fun countAdjacent(grid: List<CharArray>, x: Int, y: Int): Int {
    var count = 0

    for (i in (x - 1)..(x + 1)) {
        for (j in (y - 1)..(y + 1)) {
            if (!(i == x && j == y)
                && i in grid.indices
                && j in grid[0].indices
                && grid[i][j] == ROLL
            ) count++
        }
    }

    return count
}

private fun part2(input: List<String>): Int {
    val grid = input.map { it.toCharArray() }

    var totalRemoved = 0
    var lastCycleRemoved: Int

    do {
        lastCycleRemoved = 0
        for (i in grid.indices) {
            for (j in grid[0].indices) {
                if (grid[i][j] == ROLL && countAdjacent(grid, i, j) < ADJACENT_LIMIT) {
                    grid[i][j] = '.'
                    totalRemoved++
                    lastCycleRemoved++
                }
            }
        }
    } while (lastCycleRemoved > 0)

    return totalRemoved
}