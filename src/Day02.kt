fun main() {

    val testInput = readInput("Day02_test")
    check(part1(testInput).also { println("part 1 test actual: $it") } == 1227775554L) { "part 1 failed" }
    check(part2(testInput).also { println("part 2 test actual: $it") } == 4174379265L) { "part 2 failed" }

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}


private fun part1(input: List<String>): Long {
    check(input.size == 1) { "invalid input: input must have exactly one line" }

    return input[0]
        .split(",")
        .asSequence()
        .map { it.split("-").let { arr -> arr[0].toLong()..arr[1].toLong() } }
        .flatMap { it.asSequence() }
        .filter(::isInvalidId)
        .sum()
}

private fun isInvalidId(num: Long): Boolean {
    val str = num.toString()
    if (str.length % 2 != 0) return false
    val patternLength = str.length / 2
    return str.take(patternLength) == str.substring(patternLength)
}

private fun part2(input: List<String>): Long {
    check(input.size == 1) { "invalid input: input must have exactly one line" }

    return input[0]
        .split(",")
        .asSequence()
        .map { it.split("-").let { arr -> arr[0].toLong()..arr[1].toLong() } }
        .flatMap { it.asSequence() }
        .filter(::isInvalidId2)
        .sum()
}

private fun isInvalidId2(num: Long): Boolean {
    if (num <= 10) return false
    val str = num.toString()
    for (patternLength in 1..(str.length / 2)) {
        if (str.length % patternLength != 0) continue
        val pattern = str.take(patternLength)

        if (str.windowed(patternLength, patternLength).all { w -> w == pattern }) {
            return true
        }
    }

    return false
}

