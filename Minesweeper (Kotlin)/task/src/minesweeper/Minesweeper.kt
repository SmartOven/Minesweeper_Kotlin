package minesweeper

import java.util.*
import kotlin.random.Random
import kotlin.random.nextInt

class Minesweeper(
        minesCount: Int,
        private val generator: Random = Random.Default
) {
    private val rows = 9
    private val columns = 9
    private val mineCell = 'X'
    private val safeCell = '/'
    private val hiddenCell = '.'
    private val markedCell = '*'
    private val directionsAround = listOf(
            Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
            Pair(0, -1), Pair(0, 1),
            Pair(1, -1), Pair(1, 0), Pair(1, 1)
    )

    private var state = GameState.RUNNING
    val gameState: GameState
        get() = state

    private val safeCells = mutableListOf<Pair<Int, Int>>()
    private val mineCells = mutableSetOf<Pair<Int, Int>>()

    private var unMarkedMinesCount = minesCount

    private val gameField = List(rows) { MutableList(columns) { hiddenCell } }
    private val visited = List(rows) { MutableList(columns) { false } }

    init {
        generateMines(minesCount)
        printGameField()
    }

    /**
     * It marks/un-marks cell as possible mine and checks if user marked/un-marked the actual mine or not
     */
    fun markMine(x: Int, y: Int) {
        val coords = Pair(y - 1, x - 1)
        val (i, j) = coords
        if (i !in gameField.indices || j !in gameField[i].indices) {
            throw IllegalArgumentException("Out of bounds!")
        }
        if (gameField[i][j].isDigit()) {
            throw IllegalArgumentException("There is a number here!")
        }
        if (mineCells.contains(coords)) {
            if (gameField[i][j] == markedCell) {
                // un-marking actual marked mine
                unMarkedMinesCount++
            } else {
                // marking actual mine
                unMarkedMinesCount--
            }
        }
        gameField[i][j] = if (gameField[i][j] == markedCell) hiddenCell else markedCell
        printGameField()
        if (unMarkedMinesCount == 0) {
            state = GameState.WIN
            println("Congratulations! You found all the mines!")
        }
    }

    /**
     * It frees the area around chosen cell if it doesn't
     * contain a mine, otherwise player lose the game
     */
    fun freeArea(x: Int, y: Int) {
        var coords = Pair(y - 1, x - 1)
        if (mineCells.contains(coords)) {
            state = GameState.LOSE
            printGameField(showMines = true)
            println("You stepped on a mine and failed!")
            return
        }

        val queue: Queue<Pair<Int, Int>> = LinkedList()
        queue.add(coords)
        while (queue.isNotEmpty()) {
            coords = queue.poll()
            val minesAroundCount = countMinesAround(coords.first, coords.second)
            if (minesAroundCount > 0) {
                gameField[coords.first][coords.second] = '0' + minesAroundCount
                continue
            }
            gameField[coords.first][coords.second] = safeCell
            for ((dx, dy) in directionsAround) {
                val newCoords = Pair(coords.first + dx, coords.second + dy)
                if (newCoords.first !in gameField.indices || newCoords.second !in gameField[newCoords.first].indices) {
                    continue
                }
                if (visited[newCoords.first][newCoords.second]) {
                    continue
                }
                if (gameField[newCoords.first][newCoords.second].isDigit()) {
                    continue
                }
                queue.add(newCoords)
                visited[newCoords.first][newCoords.second] = true
            }
        }
        printGameField()
    }

    private fun printGameField(showMines: Boolean = false) {
        val stringBuilder = StringBuilder()
        stringBuilder.append(" │123456789│\n")
        stringBuilder.append("—│—————————│\n")
        for (i in gameField.indices) {
            stringBuilder.append("${i + 1}│")
            for (j in gameField[i].indices) {
                if (showMines && mineCells.contains(Pair(i, j))) {
                    stringBuilder.append(mineCell)
                } else {
                    stringBuilder.append(gameField[i][j])
                }
            }
            stringBuilder.append("│\n")
        }
        stringBuilder.append("—│—————————│\n")
        println(stringBuilder.toString())
    }

    /**
     * It generates mines and adds it to mineCells
     */
    private fun generateMines(minesCount: Int) {
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                safeCells.add(Pair(i, j))
            }
        }
        repeat(minesCount) {
            val coordsIndex = generator.nextInt(safeCells.indices)
            val coords = safeCells[coordsIndex]
            mineCells.add(coords)
            safeCells.removeAt(coordsIndex)
        }
    }

    private fun countMinesAround(x: Int, y: Int): Int {
        var count = 0
        for ((dx, dy) in directionsAround) {
            val newCoords = Pair(x + dx, y + dy)
            if (newCoords.first !in gameField.indices || newCoords.second !in gameField[newCoords.first].indices) {
                continue
            }
            if (mineCells.contains(newCoords)) {
                count++
            }
        }
        return count
    }

    enum class GameState {
        RUNNING,
        WIN,
        LOSE,
    }
}
