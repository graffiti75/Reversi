package br.android.cericatto.reversi.ui.board.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.cos

@Composable
fun CircleWithCoinFlipEffect(
	canvasSize: Dp = 300.dp,
	radius: Float = 90f,
	firstColor: Color = Color.Black,
	secondColor: Color = Color.White,
	center: Offset = Offset(150f, 150f),
	animationDuration: Int = 500,
	angle: Float = 45f,
	triggerAnimation: Boolean = true,
	onAnimationComplete: () -> Unit = {}
) {
	// Side state: 0 for firstColor (black), 1 for secondColor (white).
	var side by remember { mutableIntStateOf(0) }
	val flipProgress = remember { Animatable(0f) }
	val coroutineScope = rememberCoroutineScope()

	// Determine current color based on animation state.
	val currentColor = if (flipProgress.isRunning) {
		val flips = (flipProgress.value / 180f).toInt()
		if ((side + flips) % 2 == 0) firstColor else secondColor
	} else {
		if (side == 0) firstColor else secondColor
	}

	// Trigger animation when triggerAnimation becomes true
	LaunchedEffect(triggerAnimation) {
		if (triggerAnimation && !flipProgress.isRunning) {
			coroutineScope.launch {
				// Animate two flips (0 to 360 degrees).
				flipProgress.animateTo(
					targetValue = 360f,
					animationSpec = tween(
						durationMillis = animationDuration,
						easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1.0f)
					)
				)
				// Toggle side once to end on the other side.
				side = (side + 1) % 2
				// Reset progress instantly.
				flipProgress.snapTo(0f)
				onAnimationComplete()
			}
		}
	}

	Canvas(
		modifier = Modifier
			.width(canvasSize)
			.aspectRatio(1f)
	) {
		// ScaleX for full flip effect without absolute value.
		val scaleX = cos(Math.toRadians(flipProgress.value.toDouble())).toFloat()

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
				center = center,
				style = Fill
			)
		}
	}
}

@Preview
@Composable
private fun CircleAnimationPreview() {
	CircleWithCoinFlipEffect(
		onAnimationComplete = {}
	)
}