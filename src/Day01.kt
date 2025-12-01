import kotlin.math.abs

fun main() {
    // Read test input from the `src/Day01_test.txt` file
    val testInput = readInput("Day01_test")
    check(part1(testInput).also { println("part 1 test actual: $it") } == 3) { "part 1 failed" }
    check(part2(testInput).also { println("part 2 test actual: $it") } == 6) { "part 2 failed" }

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}

private fun part1(input: List<String>): Int {
    return input
        .map { parseLine(it) }
        .map { parseClicks(it) }
        .fold(50 to 0) { (prevPosition, count), clicks ->
            var nextPosition = prevPosition + clicks % 100
            nextPosition = when {
                nextPosition >= 100 -> nextPosition % 100
                nextPosition < 0 -> nextPosition + 100
                else -> nextPosition
            }
            nextPosition to (if (nextPosition == 0) count + 1 else count)
        }
        .second
}

private fun part2(input: List<String>): Int {
    return input
        .map { parseLine(it) }
        .map { parseClicks(it) }
        .fold(50 to 0) { (prevPosition, count), clicks ->
            var newCount = abs(clicks) / 100
            var nextPosition = prevPosition + clicks % 100

            if (nextPosition >= 100 || nextPosition <= 0 && prevPosition != 0) newCount++

            nextPosition = when {
                nextPosition >= 100 -> nextPosition % 100
                nextPosition < 0 -> nextPosition + 100
                else -> nextPosition
            }

            nextPosition to (count + newCount)
        }.second
}

private fun parseLine(line: String): Pair<Char, Int> = line[0] to line.substring(1).toInt()
private fun parseClicks(it: Pair<Char, Int>): Int = if ('R' == it.first) it.second else -it.second