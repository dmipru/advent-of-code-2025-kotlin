fun main() {
    val testInput = readInput("Day06_test")
    check(part1(testInput).also { println("part 1 test actual: $it") } == 4277556L) { "part 1 failed" }
    check(part2(testInput).also { println("part 2 test actual: $it") } == 3263827L) { "part 2 failed" }

    val input = readInput("Day06")
    part1(input).println("part 1 answer: ")
    part2(input).println("part 2 answer: ")
}

private fun part1(input: List<String>): Long {
    if (input.isEmpty()) return 0
    val matrix = input.map { it.trim().split(Regex("\\s+")) }

    check(matrix.all { it.size == matrix[0].size }) {
        "invalid input: not all problems have the same number of numbers"
    }

    return matrix[0].indices
        .map { j ->
            Problem(
                (0 until matrix.lastIndex).map { i -> matrix[i][j].toLong() },
                matrix[matrix.lastIndex][j].toOperator()
            )
        }
        .sumOf { it.solve() }
}

private fun part2(input: List<String>): Long {
    if (input.isEmpty()) return 0
    // in case if IDE removed trailing whitespaces on input copy-pase
    val matrix = padTrailingWhitespaces(input)

    val problems = mutableListOf<Problem>()
    val numbers = mutableListOf<Long>()
    var operator: Operator? = null

    // Problems are separated with a column consisting only of spaces
    // Assume operator is in column, but not always on first position
    for (j in matrix[0].indices) {
        if (isEmptyCol(matrix, j)) {
            check(numbers.isNotEmpty() && operator != null) {
                "failed to parse column: operator: $operator, numbers: $numbers"
            }
            problems.add(Problem(numbers.toList(), operator))
            numbers.clear()
        } else {
            numbers += parseColNumber(matrix, j)
            if (!matrix[matrix.lastIndex][j].isWhitespace()) {
                operator = matrix[matrix.lastIndex][j].toOperator()
            }
        }
    }

    check(numbers.isNotEmpty() && operator != null) {
        "failed to parse column: operator: $operator, numbers: $numbers"
    }

    problems.add(Problem(numbers, operator))

    return problems.sumOf { it.solve() }
}

private fun padTrailingWhitespaces(input: List<String>): List<String> {
    val lineLength = input.maxOf { it.length }
    val matrix = input.map { if (it.length < lineLength) it + " ".repeat(lineLength - it.length) else it }
    return matrix
}

private fun parseColNumber(matrix: List<String>, col: Int): Long {
    var number = 0L
    for (i in 0 until matrix.lastIndex) {
        val c = matrix[i][col]
        if (c.isDigit()) {
            number = number * 10 + c.digitToInt()
        }
    }
    return number
}

private fun isEmptyCol(matrix: List<String>, col: Int): Boolean {
    return matrix.indices.all { i -> matrix[i][col].isWhitespace() }
}

private data class Problem(val numbers: List<Long>, val operator: Operator) {
    fun solve(): Long {
        return numbers.reduce { acc, num -> operator.eval(acc, num) }
    }
}

private enum class Operator {
    PLUS {
        override fun eval(acc: Long, num: Long): Long = acc + num
    },
    MULTIPLY {
        override fun eval(acc: Long, num: Long): Long = acc * num
    };

    abstract fun eval(acc: Long, num: Long): Long
}

private fun String.toOperator(): Operator = when (this) {
    "+" -> Operator.PLUS
    "*" -> Operator.MULTIPLY
    else -> throw IllegalArgumentException("Unknown operator: $this")
}

private fun Char.toOperator(): Operator = when (this) {
    '+' -> Operator.PLUS
    '*' -> Operator.MULTIPLY
    else -> throw IllegalArgumentException("Unknown operator: $this")
}
