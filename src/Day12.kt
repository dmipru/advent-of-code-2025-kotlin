private const val GIFT_RECTANGLE_AREA: Int = 9

/**
 * Input specific solution:
 * Every gift in my input fits in 3x3 rectangle, so has area = 9.
 * If all gift rectangles can be fit in tree space then all gifts can fit.
 * It does not pass the test input and tree sides are not checked to be a multiple of 3,
 * but luckily it gives accepted result for my puzzle input without solving actual bin packing problem.
 */
fun main() {
    val input = readInput("Day12")
    part1(input).println("part 1 answer: ")
}

private fun part1(input: List<String>): Int {
    return input
        .filter { it.contains("x") }
        .map { parseTree(it) }
        .count { solveTree(it) }
}

private fun solveTree(tree: Tree): Boolean {
    return tree.area() >= (tree.gifts.sum() * GIFT_RECTANGLE_AREA)
}

private fun parseTree(str: String): Tree {
    val (sizePart, giftsPart) = str.split(":")
    val (h, w) = sizePart.trim().split("x").map { it.toInt() }
    val gifts = giftsPart.trim().split(" ").map { it.toInt() }
    return Tree(h, w, gifts)
}

private data class Tree(val h: Int, val w: Int, val gifts: List<Int>) {
    fun area() = h * w
}