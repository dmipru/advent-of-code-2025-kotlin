import java.util.*

fun main() {
    val testInput = readInput("Day07_test")
    check(part1(testInput).also { println("part 1 test actual: $it") } == 21) { "part 1 failed" }
    check(part2(testInput).also { println("part 2 test actual: $it") } == 40L) { "part 2 failed" }

    val input = readInput("Day07")
    part1(input).println("part 1 answer: ")
    part2(input).println("part 2 answer: ")
}

private fun part1(input: List<String>): Int {
    val startIndex = input[0].indexOfFirst { it == 'S' }
    check(startIndex != -1) { "Beam start index not found in ${input[0]}" }
    val manifold = input.map { it.toCharArray() }.toTypedArray()

    var splitCount = 0

    val queue = LinkedList<Pair<Int, Int>>()
    queue.add(0 to startIndex)

    while (queue.isNotEmpty()) {
        var (si, sj) = queue.poll()
        si++

        while (si < manifold.size && manifold[si][sj] == '.') {
            manifold[si][sj] = '|'
            si++
        }

        if (si == manifold.size || manifold[si][sj] != '^') continue

        splitCount++

        for (splitJ in arrayOf(sj - 1, sj + 1)) {
            if (splitJ in manifold[0].indices) {
                manifold[si][splitJ] = '|'
                queue.add(Pair(si, splitJ))
            }
        }
    }

    return splitCount
}

private fun part2(input: List<String>): Long {
    val startIndex = input[0].indexOfFirst { it == 'S' }
    check(startIndex != -1) { "Beam start index not found in ${input[0]}" }
    return dfs(
        input,
        0,
        startIndex,
        Array(input.size) { LongArray(input[0].length) }
    )
}

private fun dfs(input: List<String>, i: Int, j: Int, cache: Array<LongArray>): Long {
    if (j !in input[0].indices) return 0
    if (i !in input.indices) return 1

    if (cache[i][j] == 0L) {
        cache[i][j] = when (input[i][j]) {
            '.', 'S' -> {
                dfs(input, i + 1, j, cache)
            }

            '^' -> {
                dfs(input, i, j - 1, cache) + dfs(input, i, j + 1, cache)
            }

            else -> 0
        }
    }

    return cache[i][j]
}