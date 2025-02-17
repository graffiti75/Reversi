package br.android.cericatto.reversi.ui.board

data class BoardState(
	val boardData : List<BoardData> = sampleBoardState,
	val last : BoardData? = null
)

/**
 * Enum to represent the possible states of each cell.
 */
enum class CellState {
	EMPTY,
	BLACK,
	WHITE
}

/**
 * Data class to represent a Board Position.
 */
data class Position(val row: Int, val col: Int)

/**
 * Data class to control the state of each Board Piece.
 */
data class BoardData(
	val cellState: CellState = CellState.EMPTY,
	val position: Position = Position(0, 0),
	val filled : Boolean = true
)

val sampleBoardState = listOf(
	BoardData(
		cellState = CellState.BLACK,
		position = Position(2, 3),
	),
	BoardData(
		cellState = CellState.BLACK,
		position = Position(3, 4),
	),
	BoardData(
		cellState = CellState.WHITE,
		position = Position(3, 3),
	)
)