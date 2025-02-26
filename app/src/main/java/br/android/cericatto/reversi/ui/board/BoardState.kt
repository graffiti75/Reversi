package br.android.cericatto.reversi.ui.board

import androidx.compose.ui.geometry.Offset
import kotlin.random.Random

const val GRID_SIZE = 8
const val SEED = 4
val FIRST_PLAYER = CellState.BLACK

/**
 * Enum to represent the possible states of each Cell.
 */
enum class CellState {
	EMPTY,
	BLACK,
	WHITE
}

data class Score(
	val black: Int = 0,
	val white: Int = 0
)

/**
 * Data class to represent a Board Position.
 */
data class BoardPosition(val row: Int, val col: Int)

data class BoardState(
	val clickedPosition : Offset? = null,
	val history: List<Snapshot> = listOf(Snapshot(sampleBoardState)),
	val boardData : List<BoardCell> = sampleBoardState,
	val last : BoardCell = BoardCell(
		cellState = FIRST_PLAYER,
		filled = false
	),
	val score: Score = Score(),
	val round: Int = 0,
	val animationProgress: Float = 0f
)

/**
 * Data class to control the state of each Cell Piece.
 */
data class BoardCell(
	val cellState: CellState = CellState.EMPTY,
	val boardPosition: BoardPosition = BoardPosition(0, 0),
	val filled : Boolean = true,
	val shouldAnimate: Boolean = false
)

data class Snapshot(
	val snapshot: List<BoardCell>
)

val sampleBoardState = randomBoardStates()

private fun randomBoardStates(
	seed: Int = SEED
): List<BoardCell> {
	val mutableList = mutableListOf<BoardCell>()
	var i = 0
	while (i < seed) {
		val item = BoardCell(
			cellState = if (Random.nextBoolean()) CellState.BLACK else CellState.WHITE,
			boardPosition = BoardPosition(
				row = Random.nextInt(0, GRID_SIZE),
				col = Random.nextInt(0, GRID_SIZE)
			),
		)
		val set = mutableList.map { it.boardPosition }.toSet()
		if (item.boardPosition !in set) {
			mutableList.add(item)
			i++
		}
	}
	println("i: $i")
	return mutableList
}

/**
 * Possible Movements from the pieces into the Board
 */
enum class Movement {
	NORTH,
	NORTHEAST,
	EAST,
	SOUTHEAST,
	SOUTH,
	SOUTHWEST,
	WEST,
	NORTHWEST
}