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

fun boardPositionFromOffset(
	position: Offset,
	cellSize: Float
): Pair<Int, Int> {
	val col = (position.x / cellSize).toInt()
	val row = (position.y / cellSize).toInt()
	return Pair(row, col)
}

/**
 * Checks if a given board position satisfies a specific condition.
 *
 * @param position The offset representing the position on the board.
 * @param cellSize The size of each cell on the board.
 * @param condition A lambda function that defines the condition to check for a BoardCell.
 * @return True if the board position satisfies the condition, false otherwise.
 */
private fun checkBoardPositionWithCriteria(
	position: Offset,
	cellSize: Float,
	condition: (BoardCell) -> Boolean
): Boolean {
	val (row, col) = boardPositionFromOffset(position, cellSize)
//	println("------------------------- checkBoardPositionWithCriteria -> (row, col): ($row, $col)")
	return sampleBoardState.any {
		it.boardPosition.row == row && it.boardPosition.col == col && condition(it)
	}
}

/**
 * Determines if a given board position, derived from an offset and cell size, is filled.
 *
 * @param position The offset representing the position on the board.
 * @param cellSize The size of each cell on the board.
 * @return True if the board position is filled, false otherwise.
 */
fun boardPositionIsFilled(position: Offset, cellSize: Float): Boolean {
	return checkBoardPositionWithCriteria(position, cellSize) { it.filled }
}

private fun boardPositionIsFilled(row: Int, col: Int) = sampleBoardState.any {
	it.boardPosition.row == row && it.boardPosition.col == col && it.filled
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
	val (row, col) = boardPositionFromOffset(position, cellSize)
	return centerPosition(
		cellSize = cellSize,
		row = row,
		col = col
	)
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
private fun checkBoardDirection(
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

fun List<BoardCell>.checkBoardNeighbors(
	playerState: CellState,
	current: BoardCell
): List<BoardCell> {
//	println("------------------------- checkBoardNeighbors")
//	println("----- checkBoardNeighbors -> list: $this")
	val visited = mutableListOf<BoardCell>()
	val directions = listOf(
		Directions.NORTH, Directions.NORTH_EAST, Directions.EAST, Directions.SOUTH_EAST,
		Directions.SOUTH, Directions.SOUTH_WEST, Directions.WEST, Directions.NORTH_WEST
	)
	this.forEach { item ->
		if (item.cellState != CellState.EMPTY) {
			directions.forEach { direction ->
//				println("----- checkBoardNeighbors -> this.checkNeighbor(\ncurrent = $current, \ndirection = $direction, \ndifferentState = $different)")
				this.checkNeighbor(
					playerState = playerState,
					current = current,
					direction = getDirection(direction)
				)?.let {
//					println("----- checkBoardNeighbors -> 'visited' for $direction: $it")
					val set = this.map { it.boardPosition }.toSet()
					if (it.boardPosition !in set) {
						visited.add(it)
					}
				}
			}

		}
	}
	if (visited.isNotEmpty()) {
		visited.forEach {
//			println("visited: $it")
		}
	} else{
//		println("visited is empty!")
	}

	return visited
}

private fun getDirection(direction: Directions): Direction {
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

private fun distanceBetweenCells(
	currentRow: Int,
	currentCol: Int,
	nextRow: Int,
	nextCol: Int,
): Int {
	val x = ((currentCol - nextCol).absoluteValue).toDouble()
	val y = ((currentRow - nextRow).absoluteValue).toDouble()
	return Math.max(x, y).toInt()
}

/**
 * Check the neighbor Cell in a given direction.
 */
private fun List<BoardCell>.checkNeighbor(
	playerState: CellState,
	current: BoardCell,
	direction: Direction
): BoardCell? {
//	println("\t------------------------- checkNeighbor -> playerState: $playerState")

	// Check bounds.
	val (neighborRow, neighborCol) = Pair(current.boardPosition.row + direction.rowModifier, current.boardPosition.col + direction.colModifier)
	val (invertedRow, invertedCol) = Pair(current.boardPosition.row + -direction.rowModifier, current.boardPosition.col + -direction.colModifier)
	if (!isValidPosition(neighborRow, neighborCol) && !isValidPosition(invertedRow, invertedCol)) return null
//	println("\t----- checkNeighbor -> neighbor: ($neighborRow, $neighborCol)")
//	println("\t----- checkNeighbor -> inverted: ($invertedRow, $invertedCol)")
//	println("\t----- checkNeighbor -> isValidPosition(neighbor): ${isValidPosition(neighborRow, neighborCol)}")
//	println("\t----- checkNeighbor -> isValidPosition(inverted): ${isValidPosition(invertedRow, invertedCol)}")

	// Check if Inverted has a Filled Cell.
	if (!boardPositionIsFilled(invertedRow, invertedCol)) return null

	// Check if Neighbor has a different CellState from the current Cell, and if Inverted has the same CellState from the Neighbor cell.
	val invertedCellState = this.first { it.boardPosition.row == invertedRow && it.boardPosition.col == invertedCol }.cellState
	val differentStateBetweenNeighborAndCurrent = playerState != current.cellState
	val sameStateBetweenNeighborAndInverted = playerState == invertedCellState

	// Calculate the distance between the Neighbor Cell and the Inverted Cell.
	val differenceBetweenNeighbors = distanceBetweenCells(
		currentRow = neighborRow, currentCol = neighborCol, nextRow = invertedRow, nextCol = invertedCol
	)

	val neighborCell = this.filter {
		it.boardPosition.row == neighborRow && it.boardPosition.col == neighborCol
	}
	val neighbor = if (neighborCell.isNotEmpty()) {
		// Check if the Neighbor Cell is already placed into the Board.
		println("\t----- checkNeighbor -> neighborCell.isNotEmpty(): ${neighborCell.first()}")
		neighborCell.first()
	} else {
		// The Else condition is: we need to have an unfilled Cell to be able to have a possible Movement to eat a Cell.
		// This "Eat" Movement must be done between the Neighbor and the Inverted Cell, and in the middle we must have the Current Cell.
//		println("\t----- checkNeighbor -> differentStateBetweenNeighborAndCurrent: $differentStateBetweenNeighborAndCurrent")
//		println("\t----- checkNeighbor -> sameStateBetweenNeighborAndInverted: $sameStateBetweenNeighborAndInverted")
//		println("\t----- checkNeighbor -> differenceBetweenNeighbors: $differenceBetweenNeighbors")
		if (differentStateBetweenNeighborAndCurrent && sameStateBetweenNeighborAndInverted && differenceBetweenNeighbors == 2) {
			BoardCell(
				cellState = CellState.HINT,
				boardPosition = BoardPosition(row = neighborRow, col = neighborCol),
				filled = false
			)
		} else null
	}
//	println("\t----- checkNeighbor -> neighbor: $neighbor")
	return neighbor
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