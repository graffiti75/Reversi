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
	println("current: $current")
	println("list: $list")
	val col = current.position.col
	val row = current.position.row
	val state = current.cellState

	var itemRow = row - 1
	var differentColor = true
	val visited = mutableListOf<BoardData>()
	while (itemRow >= 0 && differentColor) {
		val item = list.find {
			it.position.col == col && it.position.row == itemRow
		}
		if (item != null) {
			if (item.cellState == state) {
				println("The item at [$itemRow, $col] has the same color of our current item: ${state.name}")
				differentColor = false
			} else {
				println("The item at [$itemRow, $col] has a different color than our current item. " +
					"This color is ${item.cellState.name}")
				visited.add(item)
				itemRow--
			}
		} else {
			println("There's not an item on position [$itemRow, $col].")
			break
		}
	}

	// Found pieces to be eaten
	if (visited.size > 0) {
		visited.forEach {
			println("visited: $it")
		}
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