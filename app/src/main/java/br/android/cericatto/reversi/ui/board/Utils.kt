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
		it.position.col == col && it.position.row == row
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

fun checkNorth(
	current: BoardData,
	list: List<BoardData>
) {
	val col = current.position.col
	val row = current.position.row
	val state = current.cellState
	var itemCol = col - 1
	var differentColor = true
	var visited = mutableListOf<BoardData>()
	while (itemCol >= 0 && differentColor) {
		val item = list.find {
			it.position.col == itemCol && it.position.row == row
		}
		if (item != null) {
			if (item.cellState == state) {
				println("The item at [$itemCol, $row] has the same color of our current item: ${state.name}")
				differentColor = false
			} else {
				println("The item at [$itemCol, $row] has a different color than our current item. " +
					"This color is ${item.cellState.name}")
				visited.add(item)
				itemCol--
			}
		} else {
			println("There's not item on position [$itemCol, $row].")
			break
		}
	}

	// Found pieces to be eaten
	if (visited.size > 0) {

	}
}

fun isDiagonal(
	current: Position,
	next: Position
): Boolean {
	val x = (next.col - current.col).absoluteValue
	val y = (next.row - current.row).absoluteValue
	return x == y
}

fun differenceBetweenTwoPoints(
	current: Position,
	next: Position
): Int {
	val x = ((next.col - current.col).absoluteValue).toDouble()
	val y = ((next.row - current.row).absoluteValue).toDouble()
	return x.coerceAtMost(y).toInt()
}