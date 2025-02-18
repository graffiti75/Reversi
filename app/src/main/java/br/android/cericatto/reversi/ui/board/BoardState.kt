package br.android.cericatto.reversi.ui.board

import androidx.compose.ui.geometry.Offset
import kotlin.random.Random

const val GRID_SIZE = 8
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
	val boardData : List<BoardData> = sampleBoardState,
	val last : BoardData = BoardData(
		cellState = FIRST_PLAYER,
		filled = false
	),
	val score: Score = Score(),
	val round: Int = -1
)

/**
 * Data class to control the state of each Board Piece.
 */
data class BoardData(
	val cellState: CellState = CellState.EMPTY,
	val boardPosition: BoardPosition = BoardPosition(0, 0),
	val filled : Boolean = true,
)

val sampleBoardState = randomBoardStates()

private fun randomBoardStates(
	seed: Int = 18
): List<BoardData> {
	val mutableList = mutableListOf<BoardData>()
	var i = 0
	while (i < seed) {
		val item = BoardData(
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