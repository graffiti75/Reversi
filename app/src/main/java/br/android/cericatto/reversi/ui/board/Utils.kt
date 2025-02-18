package br.android.cericatto.reversi.ui.board

import androidx.compose.ui.geometry.Offset
import kotlin.math.absoluteValue

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

fun calculateScore(list: List<BoardData>) = Score(
	black = list.count { it.cellState == CellState.BLACK },
	white = list.count { it.cellState == CellState.WHITE }
)

/**
 * Represents a direction to check on the board using coordinate modifiers.
 *
 * @param rowModifier How the row changes in this direction (+1, 0, or -1)
 * @param colModifier How the column changes in this direction (+1, 0, or -1)
 */
data class Direction(val rowModifier: Int, val colModifier: Int)

/**
 * Unified method to check the board in any direction for pieces that can be captured.
 *
 * @param current The current board piece being checked
 * @param list The complete list of board pieces
 * @param direction The direction to check, defined by row and column modifiers
 *
 * @return List of pieces that can be captured in the specified direction
 */
fun checkBoard(
	current: BoardData,
	list: List<BoardData>,
	direction: Direction
): List<BoardData> {
	println("-------------------- checking direction: rowMod=${direction.rowModifier}, colMod=${direction.colModifier}")
	println("current: $current")
	println("list: $list")

	val startCol = current.boardPosition.col
	val startRow = current.boardPosition.row
	val state = current.cellState

	var itemCol = startCol + direction.colModifier
	var itemRow = startRow + direction.rowModifier

	var differentColor = true
	val visited = mutableListOf<BoardData>()

	// Continue while we're within board bounds and haven't found a same-colored piece.
	while (isValidPosition(itemRow, itemCol) && differentColor) {
		val item = list.find {
			it.boardPosition.col == itemCol && it.boardPosition.row == itemRow
		}

		if (item != null) {
			if (item.cellState == state) {
				println("The item at [$itemRow, $itemCol] has the same color of our current item: ${state.name}")
				differentColor = false
			} else {
				println("The item at [$itemRow, $itemCol] has a different color than our current item. " +
					"This color is ${item.cellState.name}")
				visited.add(item)
				itemCol += direction.colModifier
				itemRow += direction.rowModifier
			}
		} else {
			println("There's not an item on position [$itemRow, $itemCol].")
			return emptyList()
		}
	}

	if (!isValidPosition(itemRow, itemCol)) return emptyList()

	// Log found pieces to be captured.
	if (visited.isNotEmpty()) {
		visited.forEach {
			println("visited: $it")
		}
	} else{
		println("visited is empty!")
	}
	return visited
}

/**
 * Helper function to check if a position is within the board boundaries.
 */
private fun isValidPosition(row: Int, col: Int): Boolean {
	return row in 0..<GRID_SIZE && col >= 0 && col < GRID_SIZE
}

/**
 * Checks all 8 possibilities.
 */
fun checkNorth(current: BoardData, list: List<BoardData>): List<BoardData> =
	checkBoard(current, list, Direction(-1, 0))

fun checkNortheast(current: BoardData, list: List<BoardData>): List<BoardData> =
	checkBoard(current, list, Direction(-1, 1))

fun checkEast(current: BoardData, list: List<BoardData>): List<BoardData> =
	checkBoard(current, list, Direction(0, 1))

fun checkSoutheast(current: BoardData, list: List<BoardData>): List<BoardData> =
	checkBoard(current, list, Direction(1, 1))

fun checkSouth(current: BoardData, list: List<BoardData>): List<BoardData> =
	checkBoard(current, list, Direction(1, 0))

fun checkSouthwest(current: BoardData, list: List<BoardData>): List<BoardData> =
	checkBoard(current, list, Direction(1, -1))

fun checkWest(current: BoardData, list: List<BoardData>): List<BoardData> =
	checkBoard(current, list, Direction(0, -1))

fun checkNorthwest(current: BoardData, list: List<BoardData>): List<BoardData> =
	checkBoard(current, list, Direction(-1, -1))