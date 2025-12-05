fun main() {
    val testInput = readInput("Day05_test")
    check(part1(testInput).also { println("part 1 test actual: $it") } == 3) { "part 1 failed" }
    check(part2(testInput).also { println("part 2 test actual: $it") } == 14L) { "part 2 failed" }

    val input = readInput("Day05")
    part1(input).println("part 1 answer: ")
    part2(input).println("part 2 answer: ")
}

private fun part1(input: List<String>): Int {
    val (ranges, ids) = parseInput(input)

    var freshCount = 0

    for (id in ids) {
        for (range in ranges) {
            if (id in range) {
                freshCount++
                break
            }
        }
    }

    return freshCount
}

private fun part2(input: List<String>): Long {
    val (unsortedRanges, _) = parseInput(input)
    val ranges = unsortedRanges.sortedBy { it.first }

    var freshCount = 0L

    var (commonRangeFirst, commonRangeLast) = ranges.first().let { it.first to it.last }

    ranges
        .subList(1, ranges.size)
        .forEach { range ->
            when {
                // gap case
                range.first > commonRangeLast -> {
                    freshCount += commonRangeLast - commonRangeFirst + 1
                    commonRangeFirst = range.first
                    commonRangeLast = range.last
                }

                // extend case
                range.last > commonRangeLast -> {
                    commonRangeLast = range.last
                }
            }
        }

    freshCount += commonRangeLast - commonRangeFirst + 1

    return freshCount
}

private fun parseInput(input: List<String>): Pair<List<LongRange>, List<Long>> {
    val ranges = mutableListOf<LongRange>()
    val ids = mutableListOf<Long>()

    var isRange = true

    for (line in input) {
        if (line.isBlank()) {
            isRange = false
            continue
        }

        if (isRange) {
            val rawRange = line.split("-")
            ranges += rawRange[0].toLong()..rawRange[1].toLong()
        } else {
            ids += line.toLong()
        }
    }

    return ranges to ids
}
