fun main() {
    val testInputPart1 = readInput("Day11_part1_test")
    check(part1(testInputPart1).also { println("part 1 test actual: $it") } == 5L) { "part 1 failed" }
    val testInputPart2 = readInput("Day11_part2_test")
    check(part2(testInputPart2).also { println("part 2 test actual: $it") } == 2L) { "part 2 failed" }

    val input = readInput("Day11")
    part1(input).println("part 1 answer: ")
    part2(input).println("part 2 answer: ")
}

private fun part1(input: List<String>): Long {
    val nodes = parseNodeMap(input)
    return dfs("you", "out", nodes)
}

private fun part2(input: List<String>): Long {
    val nodes = parseNodeMap(input)
    return dfs("svr", "dac", nodes) *
            dfs("dac", "fft", nodes) *
            dfs("fft", "out", nodes) +
            dfs("svr", "fft", nodes) *
            dfs("fft", "dac", nodes) *
            dfs("dac", "out", nodes)
}

private fun parseNodeMap(input: List<String>): Map<String, List<String>> {
    return input.associate {
        val (before, after) = it.split(":")
        val fromNode = before.trim()
        val toNodes = after.trim().split(" ").map { str -> str.trim() }
        fromNode to toNodes
    }
}

private fun dfs(
    node: String,
    dest: String,
    nodes: Map<String, List<String>>,
    counts: MutableMap<String, Long> = mutableMapOf()
): Long {
    if (dest == node) return 1
    if (node !in nodes) return 0
    if (node in counts) {
        return counts[node]!!
    }

    val sum = nodes[node]!!.sumOf { dfs(it, dest, nodes, counts) }
    counts[node] = sum
    return sum
}