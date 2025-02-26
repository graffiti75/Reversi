package br.android.cericatto.reversi.ui.board

import androidx.compose.ui.geometry.Offset
import kotlin.math.absoluteValue

/**
 * Represents a direction to check on the board using coordinate modifiers.
 *
 * @param rowModifier How the row changes in this direction (+1, 0, or -1)
 * @param colModifier How the column changes in this direction (+1, 0, or -1)
 */
data class Direction(val rowModifier: Int, val colModifier: Int)

enum class Directions {
	NORTH,
	NORTH_EAST,
	EAST,
	SOUTH_EAST,
	SOUTH,
	SOUTH_WEST,
	WEST,
	NORTH_WEST
}

fun List<BoardCell>.getCellByPosition(position: BoardPosition): BoardCell? {
	println("------------------------- getCellByPosition")
	val validPosition = isValidPosition(position.row, position.col)
	if (!validPosition) return null

	val cellExistsOnBoard = this.filter {
		it.boardPosition.col == position.col && it.boardPosition.row == position.row
	}
	val notEmpty = cellExistsOnBoard.isNotEmpty()
	println("----- getCellByPosition -> notEmpty: $notEmpty")
	val cell = if (notEmpty) {
		cellExistsOnBoard.first()
	} else {
		BoardCell(
			cellState = CellState.HINT,
			boardPosition = position,
			filled = false
		)
	}
	println("----- getCellByPosition -> returned cell: $cell")
	return cell
}

fun boardPosition(
	position: Offset,
	cellSize: Float
): Pair<Int, Int> {
	val col = (position.x / cellSize).toInt()
	val row = (position.y / cellSize).toInt()
	return Pair(row, col)
}

fun boardPositionIsFilled(
	position: Offset,
	cellSize: Float
): Boolean {
	val pair = boardPosition(position, cellSize)
	val col = pair.second
	val row = pair.first
	val contains = sampleBoardState.find {
		it.boardPosition.col == col && it.boardPosition.row == row
	}
	return (contains != null)
}

fun centerPosition(
	cellSize: Float,
	row: Int,
	col: Int
): Offset {
	val centerX = (col * cellSize) + (cellSize / 2)
	val centerY = (row * cellSize) + (cellSize / 2)
	return Offset(centerX, centerY)
}

fun calculateCenterClickedPosition(
	position: Offset,
	cellSize: Float
): Offset {
	val pair = boardPosition(position, cellSize)
	return centerPosition(
		cellSize = cellSize,
		row = pair.first,
		col = pair.second
	)
}

fun isDiagonal(
	current: BoardPosition,
	next: BoardPosition
): Boolean {
	val x = (next.col - current.col).absoluteValue
	val y = (next.row - current.row).absoluteValue
	return x == y
}

fun differenceBetweenTwoPoints(
	current: BoardPosition,
	next: BoardPosition
): Int {
	val x = ((next.col - current.col).absoluteValue).toDouble()
	val y = ((next.row - current.row).absoluteValue).toDouble()
	return x.coerceAtMost(y).toInt()
}

fun calculateScore(list: List<BoardCell>) = Score(
	black = list.count { it.cellState == CellState.BLACK },
	white = list.count { it.cellState == CellState.WHITE }
)

/**
 * Unified method to check the board in any direction for pieces that can be captured.
 *
 * @param current The current board piece being checked
 * @param list The complete list of board pieces
 * @param direction The direction to check, defined by row and column modifiers
 *
 * @return List of pieces that can be captured in the specified direction
 */
fun checkBoardDirection(
	current: BoardCell,
	list: List<BoardCell>,
	direction: Direction
): List<BoardCell> {
//	println("-------------------- checking direction: rowMod=${direction.rowModifier}, colMod=${direction.colModifier}")
//	println("current: $current")
//	println("list: $list")

	val startCol = current.boardPosition.col
	val startRow = current.boardPosition.row
	val state = current.cellState

	var itemCol = startCol + direction.colModifier
	var itemRow = startRow + direction.rowModifier

	var differentColor = true
	val visited = mutableListOf<BoardCell>()

	// Continue while we're within board bounds and haven't found a same-colored piece.
	while (isValidPosition(itemRow, itemCol) && differentColor) {
		val item = list.find {
			it.boardPosition.col == itemCol && it.boardPosition.row == itemRow
		}

		if (item != null) {
			if (item.cellState == state) {
//				println("The item at [$itemRow, $itemCol] has the same color of our current item: ${state.name}")
				differentColor = false
			} else {
//				println("The item at [$itemRow, $itemCol] has a different color than our current item. " +
//					"This color is ${item.cellState.name}")
				visited.add(item)
				itemCol += direction.colModifier
				itemRow += direction.rowModifier
			}
		} else {
//			println("There's not an item on position [$itemRow, $itemCol].")
			return emptyList()
		}
	}

	if (!isValidPosition(itemRow, itemCol)) return emptyList()

	// Log found pieces to be captured.
	//
	if (visited.isNotEmpty()) {
		visited.forEach {
//			println("visited: $it")
		}
	} else{
//		println("visited is empty!")
	}
	 //
	return visited
}

fun List<BoardCell>.checkBoardAll(
	player: CellState,
	current: BoardCell
): List<BoardCell> {
	println("------------------------- checkBoardAll")
	println("----- checkBoardAll -> current: $current")
	println("----- checkBoardAll -> list: $this")
	val visited = mutableListOf<BoardCell>()
	val directions = listOf(
		Directions.NORTH, Directions.NORTH_EAST, Directions.EAST, Directions.SOUTH_EAST,
		Directions.SOUTH, Directions.SOUTH_WEST, Directions.WEST, Directions.NORTH_WEST
	)
	this.forEach { item ->
		if (item.cellState != CellState.EMPTY) {
			directions.forEach { direction ->
				println("----- checkBoardAll -> direction: $direction")
				this.checkNeighbor(
					player = player,
					current = current,
					direction = getDirection(direction)
				)?.let {
					println("----- checkBoardAll -> visited: $it")
					visited.add(it)
				}
			}

		}
	}
	if (visited.isNotEmpty()) {
		visited.forEach {
			println("visited: $it")
		}
	} else{
		println("visited is empty!")
	}

	return visited
}

fun getDirection(direction: Directions): Direction {
	return when (direction) {
		Directions.NORTH -> Direction(-1, 0)
		Directions.NORTH_EAST -> Direction(-1, 1)
		Directions.EAST -> Direction(0, 1)
		Directions.SOUTH_EAST -> Direction(1, 1)
		Directions.SOUTH -> Direction(1, 0)
		Directions.SOUTH_WEST -> Direction(1, -1)
		Directions.WEST -> Direction(0, -1)
		Directions.NORTH_WEST -> Direction(-1, -1)
	}
}

/**
 * Helper function to check if a position is within the board boundaries.
 */
private fun isValidPosition(row: Int, col: Int): Boolean {
	return row in 0..<GRID_SIZE && col >= 0 && col < GRID_SIZE
}

/**
 * Check the neighbor pieces.
 */
private fun List<BoardCell>.checkNeighbor(
	player: CellState,
	current: BoardCell,
	direction: Direction
): BoardCell? {
	println("------------------------- checkNeighbor")
	val itemCol = current.boardPosition.col + direction.colModifier
	val itemRow = current.boardPosition.row + direction.rowModifier
	val neighbor = this.getCellByPosition(BoardPosition(row = itemRow, col = itemCol))

	if (neighbor != null) {
		println("----- checkNeighbor -> neighbor: $neighbor")
		if (current.cellState != player) return neighbor
		return null
	}
	return null
}

/**
 * Checks all 8 possibilities for eaten pieces.
 */
fun checkNorthDirection(current: BoardCell, list: List<BoardCell>): List<BoardCell> =
	checkBoardDirection(current, list, Direction(-1, 0))

fun checkNortheastDirection(current: BoardCell, list: List<BoardCell>): List<BoardCell> =
	checkBoardDirection(current, list, Direction(-1, 1))

fun checkEastDirection(current: BoardCell, list: List<BoardCell>): List<BoardCell> =
	checkBoardDirection(current, list, Direction(0, 1))

fun checkSoutheastDirection(current: BoardCell, list: List<BoardCell>): List<BoardCell> =
	checkBoardDirection(current, list, Direction(1, 1))

fun checkSouthDirection(current: BoardCell, list: List<BoardCell>): List<BoardCell> =
	checkBoardDirection(current, list, Direction(1, 0))

fun checkSouthwestDirection(current: BoardCell, list: List<BoardCell>): List<BoardCell> =
	checkBoardDirection(current, list, Direction(1, -1))

fun checkWestDirection(current: BoardCell, list: List<BoardCell>): List<BoardCell> =
	checkBoardDirection(current, list, Direction(0, -1))

fun checkNorthwestDirection(current: BoardCell, list: List<BoardCell>): List<BoardCell> =
	checkBoardDirection(current, list, Direction(-1, -1))