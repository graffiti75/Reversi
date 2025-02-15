package br.android.cericatto.reversi.ui.board

data class BoardState(
	val isLoading : Boolean = false
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
	val filled : Boolean = false
)

val sampleBoardState = listOf(
	BoardData(
		cellState = CellState.BLACK,
		position = Position(0, 0),
	),
	BoardData(
		cellState = CellState.WHITE,
		position = Position(0, 1),
	),
	BoardData(
		cellState = CellState.BLACK,
		position = Position(3, 4),
	),
	BoardData(
		cellState = CellState.WHITE,
		position = Position(3, 3),
	),
	BoardData(
		cellState = CellState.WHITE,
		position = Position(5, 2),
	),
	BoardData(
		cellState = CellState.WHITE,
		position = Position(5, 5),
	),
	BoardData(
		cellState = CellState.BLACK,
		position = Position(7, 1),
	),
	BoardData(
		cellState = CellState.BLACK,
		position = Position(7, 7),
	)
)