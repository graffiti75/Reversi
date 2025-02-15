package br.android.cericatto.reversi.ui.board

import androidx.compose.ui.geometry.Offset

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