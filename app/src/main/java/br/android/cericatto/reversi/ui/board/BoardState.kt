package br.android.cericatto.reversi.ui.board

import kotlin.random.Random

const val GRID_SIZE = 8

data class BoardState(
	val boardData : List<BoardData> = sampleBoardState,
	val last : BoardData? = null,
	val currenPlayer: CellState = CellState.BLACK
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
	NORTHEAST,
	EAST,
	SOUTHEAST,
	SOUTH,
	SOUTHWEST,
	WEST,
	NORTHWEST
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

val sampleBoardState = randomBoardStates()

private fun randomBoardStates(
	seed: Int = 18
): List<BoardData> {
	val mutableList = mutableListOf<BoardData>()
	var i = 0
	while (i < seed) {
		val item = BoardData(
			cellState = if (Random.nextBoolean()) CellState.BLACK else CellState.WHITE,
			position = Position(
				row = Random.nextInt(0, GRID_SIZE),
				col = Random.nextInt(0, GRID_SIZE)
			),
		)
		val set = mutableList.map { it.position }.toSet()
		if (item.position !in set) {
			mutableList.add(item)
			i++
		}
	}
	println("i: $i")
	return mutableList
}

/*
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
	),
	BoardData(
		cellState = CellState.WHITE,
		position = Position(2, 6),
	),
	BoardData(
		cellState = CellState.WHITE,
		position = Position(3, 6),
	),
	BoardData(
		cellState = CellState.BLACK,
		position = Position(4, 6)
	)
)
*/