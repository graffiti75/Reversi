package br.android.cericatto.reversi.ui.board

const val GRID_SIZE = 8

data class BoardState(
	val boardData : List<BoardData> = sampleBoardState,
	val last : BoardData? = null,
	val currenPlayer: CellState = CellState.WHITE
)

/**
 * Enum to represent the possible states of each Cell.
 */
enum class CellState {
	EMPTY,
	BLACK,
	WHITE
}

/**
 * Possible Movements from the pieces into the Board
 */
enum class Movement {
	NORTH,
//	NORTHEAST,
//	EAST,
//	SOUTHEAST,
	SOUTH,
//	SOUTHWEST,
//	WEST,
//	NORTHWEST
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
	val filled : Boolean = true,
)

val sampleBoardState = listOf(
	BoardData(
		cellState = CellState.BLACK,
		position = Position(1, 3),
	),
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
	),
	BoardData(
		cellState = CellState.WHITE,
		position = Position(4, 3),
	),
	BoardData(
		cellState = CellState.WHITE,
		position = Position(5, 3),
	)

)