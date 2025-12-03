#set( $Code = "bar" )
fun main() {
    val testInput = readInput("Day${Day}_test")
    check(part1(testInput).also { println("part 1 test actual: \$it") } == 1) { "part 1 failed" }
    check(part2(testInput).also { println("part 2 test actual: \$it") } == 1) { "part 2 failed" }

    val input = readInput("Day$Day")
    part1(input).println("part 1 answer: ")
    part2(input).println("part 2 answer: ")
}

private fun part1(input: List<String>): Int {
    return input.size
}

private fun part2(input: List<String>): Int {
    return input.size
}