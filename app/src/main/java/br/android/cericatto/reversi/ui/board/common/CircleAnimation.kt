package br.android.cericatto.reversi.ui.board.common

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import br.android.cericatto.reversi.ui.board.BoardAction
import br.android.cericatto.reversi.ui.board.BoardCell
import br.android.cericatto.reversi.ui.board.CellState
import kotlin.math.cos

// Non-composable drawing function inside DrawScope,
fun DrawScope.CoinFlip(
	cell: BoardCell,
	center: Offset,
	radius: Float,
	startAnimation: Boolean,
	animationProgress: Float, // 0f to 360f
	angle: Float = 45f,
	onAction: (BoardAction) -> Unit
) {
	if (startAnimation && animationProgress > 0f) {
		// Animated coin flip logic.
		// Determine color based on animation progress (flip effect).
		val currentColor = if (animationProgress < 180f) {
			if (cell.cellState == CellState.BLACK) Color.Black else Color.White
		} else {
			if (cell.cellState == CellState.BLACK) Color.White else Color.Black
		}

		// Calculate scale for flip animation (horizontal scaling).
		val scaleX = cos(Math.toRadians(animationProgress.toDouble())).toFloat()

		// Apply transformations for animation.
		withTransform({
			rotate(
				degrees = angle,
				pivot = center
			)
			scale(
				scaleX = scaleX,
				scaleY = 1f,
				pivot = center
			)
		}) {
			drawCircle(
				color = currentColor,
				radius = radius,
				center = center
			)
		}

		// Call finishedAnimation when animation is complete (close to 360f).
		if (animationProgress >= 350f) {
			onAction(BoardAction.OnUpdateAnimationStatus(cell.boardPosition))
		}
	} else {
		// Static circle drawing.
		drawCircle(
			color = if (cell.cellState == CellState.BLACK) Color.Black else Color.White,
			radius = radius,
			center = center
		)
	}
}