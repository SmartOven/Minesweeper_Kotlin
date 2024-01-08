package minesweeper

import kotlin.random.Random

fun main() {
    println("How many mines do you want on the field?")
    val minesCount = readln().toInt()
    val minesweeper = Minesweeper(minesCount, generator = Random(42))

    do {
        print("Set/delete mines marks (x and y coordinates): ")
        val (x, y, operation) = readln().split(" ")
        try {
            when (operation) {
                "mine" -> minesweeper.markMine(x.toInt(), y.toInt())
                "free" -> minesweeper.freeArea(x.toInt(), y.toInt())
                else -> println("Unknown operation, possible operations are \"free\" and \"mark\"")
            }
        } catch (e: IllegalArgumentException) {
            println(e.message)
        }
    } while (minesweeper.gameState == Minesweeper.GameState.RUNNING)
}
