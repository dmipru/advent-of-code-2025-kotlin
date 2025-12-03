import kotlin.math.max

fun main() {
    val testInput = readInput("Day03_test")
    check(part1(testInput).also { println("part 1 test actual: $it") } == 357L) { "part 1 failed" }
    check(part2(testInput).also { println("part 2 test actual: $it") } == 3121910778619L) { "part 2 failed" }

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}

private fun part1(input: List<String>): Long {
    return input
        .asSequence()
        .map { it.asSequence().map(Char::digitToInt).toList() }
        .map { it.maxCombOf(2, 0, 0, mutableMapOf()) }
        .sum()
}

private fun part2(input: List<String>): Long {
    return input
        .asSequence()
        .map { it.asSequence().map(Char::digitToInt).toList() }
        .map { it.maxCombOf(12, 0, 0, mutableMapOf()) }
        .sum()
}

private fun List<Int>.maxCombOf(digits: Int, position: Int, length: Int, cache: MutableMap<CacheKey, Long>): Long {
    check(digits <= this.size) {
        "number of digits: $digits in combination must be less than or equal the list size: ${this.size}"
    }

    val numbersInListLeft = this.size - position
    val numbersNeeded = digits - length

    if (numbersNeeded == 0 || numbersNeeded > numbersInListLeft) return 0

    val cacheKey = CacheKey(position, numbersNeeded)

    if (cacheKey in cache) {
        return cache[cacheKey]!!
    }

    val curr = this[position] * 10L.pow(numbersNeeded - 1)

    val useCurrentResult = curr + maxCombOf(digits, position + 1, length + 1, cache)
    val skipCurrentResult = maxCombOf(digits, position + 1, length, cache)

    val maxResult = max(useCurrentResult, skipCurrentResult)
    cache[cacheKey] = maxResult

    return maxResult
}

private data class CacheKey(val position: Int, val needed: Int)
