import java.util.*
import kotlin.math.min

fun main() {
    val testInput = readInput("Day10_test")
    check(part1(testInput).also { println("part 1 test actual: $it") } == 7) { "part 1 failed" }
    check(part2(testInput).also { println("part 2 test actual: $it") } == 33) { "part 2 failed" }

    val input = readInput("Day10")
    part1(input).println("part 1 answer: ")
    part2(input).println("part 2 answer: ")
}

private fun part1(input: List<String>): Int {
    return parseInput(input).sumOf { getLightsMinToggles(it) }
}

private fun getLightsMinToggles(machine: Machine): Int {
    val lightMask = bitMaskFromAllBits(machine.lights)
    val buttonBits = machine.buttons.map { bitMaskFromOnes(it) }

    var minToggles = Int.MAX_VALUE

    val queue = LinkedList<Pair<Int, Int>>()
    queue += 0 to 0
    val cache = mutableMapOf<Int, Int>()

    while (queue.isNotEmpty()) {
        val (prevState, prevToggles) = queue.poll()
        if (prevToggles >= minToggles) continue
        if (cache.containsKey(prevState) && cache[prevState]!! < prevToggles) continue
        cache[prevState] = prevToggles

        for (buttonMask in buttonBits) {
            val currentState = prevState.xor(buttonMask)
            if (currentState == lightMask) {
                minToggles = min(minToggles, prevToggles + 1)
                break
            }
            queue += currentState to (prevToggles + 1)
        }
    }

    return minToggles
}

private fun part2(input: List<String>): Int {
    return parseInput(input).sumOf { getJoltageMinToggles(it) }
}

private fun getJoltageMinToggles(machine: Machine): Int {
    return getJoltageMinToggles(
        machine.jolts,
        getCombinationsWithResults(machine.jolts.size, machine.buttons),
        HashMap()
    )
}

/**
 * Get lights from jolts by checking even/odd.
 * Find all combinations to generate the lights mask.
 * Search for unique combinations as pressing button twice performs "undo" press.
 * Combinations from 0 to max button lengths.
 * For each combination
 * -> press buttons
 * -> subtract counters
 * -> make sure it does not go negative
 * -> once subtracted it gives us all zero (00..00) lights
 * -> all zero lights means all counters are even
 * -> so current lights are all zero, to achieve it we need to press some M buttons 2 times to negate press effect
 * -> need to find how to press M buttons to get /2 counters
 * -> dividing current state /2, we get new counters
 * -> repeat the whole process again (generate lights, find all combinations, etc.)
 * Formula: minToggles = minOf (combination_length + 2 * minToggles(combination_apply(current_counter)))
 */
private fun getJoltageMinToggles(
    state: List<Int>,
    maskResults: Map<CombMask, List<Result>>,
    cache: MutableMap<List<Int>, Int>
): Int {
    if (state.all { it == 0 }) return 0
    if (state in cache) return cache[state]!!
    val stateMask = CombMask(state.map { if (it % 2 == 0) 0 else 1 })

    var minToggles = Int.MAX_VALUE

    for ((combResult, combToggles) in (maskResults[stateMask] ?: listOf())) {
        val nextState = state
            .asSequence()
            .mapIndexed { i, it -> it - combResult[i] }
            .toMutableList()
        if (nextState.any { it < 0 }) continue
        nextState.indices.forEach { nextState[it] /= 2 }

        val childToggles = getJoltageMinToggles(nextState, maskResults, cache)

        minToggles = min(
            minToggles,
            if (childToggles == Int.MAX_VALUE) Int.MAX_VALUE else combToggles + 2 * childToggles
        )
    }

    cache[state] = minToggles

    return minToggles
}

private fun getCombinationsWithResults(length: Int, buttons: List<List<Int>>): Map<CombMask, List<Result>> {
    return getCombinations(buttons.size)
        .asSequence()
        .map { getCombinationResult(length, it, buttons) }
        .groupBy({ it.mask }, { it.result })
}

private fun getCombinationResult(length: Int, comb: List<Int>, buttons: List<List<Int>>): CombResult {
    val combResult = MutableList(length) { 0 }

    var toggles = 0

    comb.forEach { buttonIndex ->
        val button = buttons[buttonIndex]
        button.forEach { counterIndex -> combResult[counterIndex]++ }
        toggles++
    }

    val mask = combResult.map { if (it % 2 == 0) 0 else 1 }
    return CombResult(CombMask(mask), Result(combResult, toggles))
}

private fun getCombinations(maxLength: Int): List<List<Int>> {
    val alphabet = (0 until maxLength).toList()
    val collector = mutableListOf<List<Int>>()
    val buffer = mutableListOf<Int>()

    fun perm(length: Int, start: Int) {
        if (buffer.size == length) {
            collector.add(buffer.toList())
            return
        }

        // for { for { for { ... } } } cycle with depth length
        // sliding windows prevent alphabet & combination duplicates
        for (i in start until alphabet.size) {
            buffer += alphabet[i]
            perm(length, i + 1)
            buffer.removeLast()
        }
    }

    for (length in 0..maxLength) {
        perm(length, 0)
    }

    return collector
}

private fun bitMaskFromAllBits(allBits: List<Int>): Int {
    var bits = 0
    allBits.forEachIndexed { bitNumber, bitValue ->
        if (bitValue == 1) {
            bits = 1.shl(bitNumber).or(bits)
        }
    }
    return bits
}

private fun bitMaskFromOnes(ones: List<Int>): Int {
    var bits = 0
    ones.forEach { bitNumber ->
        bits = 1.shl(bitNumber).or(bits)
    }
    return bits
}

private fun parseInput(input: List<String>): List<Machine> {
    return input
        .asSequence()
        .map { line ->
            val lights: MutableList<Int> = mutableListOf()
            val jolts: MutableList<Int> = mutableListOf()
            val buttons: MutableList<List<Int>> = mutableListOf()
            line.split(" ")
                .forEach { part ->
                    when {
                        part.startsWith("[") && part.endsWith("]") -> {
                            part.substring(1, part.lastIndex)
                                .map { c ->
                                    when (c) {
                                        '.' -> 0
                                        '#' -> 1
                                        else -> throw IllegalStateException("invalid part '$part' in line: '$line'")
                                    }
                                }
                                .toCollection(lights)

                        }

                        part.startsWith("{") && part.endsWith("}") -> {
                            part.substring(1, part.lastIndex)
                                .split(",")
                                .map { s -> s.toInt() }
                                .toCollection(jolts)
                        }

                        part.startsWith("(") && part.endsWith(")") -> {
                            buttons += part
                                .substring(1, part.lastIndex)
                                .split(",")
                                .map { s -> s.toInt() }
                        }

                        else -> throw IllegalArgumentException("invalid part '$part' in line: '$line'")
                    }
                }
            check(lights.isNotEmpty()) { "no lights found in line: '$line'" }
            check(jolts.isNotEmpty()) { "no jolt found in line: '$line'" }
            check(buttons.isNotEmpty()) { "no button found in line: '$line'" }
            Machine(lights, buttons, jolts)
        }
        .toList()
}

private data class Machine(
    val lights: List<Int> = mutableListOf(),
    val buttons: List<List<Int>> = mutableListOf(),
    val jolts: MutableList<Int> = mutableListOf()
)

private data class CombResult(val mask: CombMask, val result: Result)
private data class Result(val result: List<Int>, val toggles: Int)
private data class CombMask(val mask: List<Int>)
