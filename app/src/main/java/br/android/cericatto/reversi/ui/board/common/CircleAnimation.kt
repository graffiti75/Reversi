package br.android.cericatto.reversi.ui.board.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.cos

@Composable
fun CircleDrawingCanvas(
	canvasSize: Dp = 300.dp,
	radius: Float = 90f,
	firstColor: Color = Color(0xFF2196F3),
	secondColor: Color = Color(0xFFF44336),
	animationDuration: Int = 500,
) {
	var circlePosition by remember { mutableStateOf<Offset?>(null) }
	var isAnimating by remember { mutableStateOf(false) }
	val color by animateColorAsState(
		targetValue = if (isAnimating) firstColor else secondColor,
		animationSpec = tween(
			durationMillis = animationDuration,
//			easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1.0f),
		),
		finishedListener = {
			isAnimating = false
		},
		label = "Circle Color Animation"
	)

	val coroutineScope = rememberCoroutineScope()

	Canvas(
		modifier = Modifier.width(canvasSize)
			.aspectRatio(1f)
			.pointerInput(Unit) {
				detectTapGestures { offset ->
					circlePosition = offset
					isAnimating = false  // Reset color to black
					coroutineScope.launch {
						isAnimating = true  // Start animation
					}
				}
			}
	) {
		circlePosition?.let { pos ->
			drawCircle(
				color = color,
				center = pos,
				radius = radius
			)
		}
	}
}

@Composable
fun DiagonalCoinFlipEffect(
	canvasSize: Dp = 300.dp,
	radius: Float = 90f,
	firstColor: Color = Color(0xFF2196F3),
	secondColor: Color = Color(0xFFF44336),
	center: Offset = Offset(150f, 150f),
	animationDuration: Int = 500,
	angle: Float = 225f,
	onAnimationComplete: () -> Unit = {}
) {
	var isFlipping by remember { mutableStateOf(false) }

	// This animation now represents the progress of our diagonal flip
	val flipProgress by animateFloatAsState(
		targetValue = if (isFlipping) angle else 0f,
		animationSpec = tween(
			durationMillis = animationDuration,
			// Using a custom easing for natural flip motion
			easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1.0f)
		),
		finishedListener = {
			isFlipping = false
			onAnimationComplete()
		},
		label = "diagonal_flip"
	)

	val currentColor by animateColorAsState(
		targetValue = if (isFlipping) firstColor else secondColor,
		animationSpec = tween(durationMillis = animationDuration),
		label = "color_transition"
	)

	// Calculate the ellipse transformation for our diagonal flip
	// We adjust the angle calculation to match our desired 135° to 45° orientation
	val normalizedAngle = flipProgress
	val scaleX = cos(Math.toRadians(normalizedAngle.toDouble())).absoluteValue.toFloat()

	Canvas(
		modifier = Modifier.width(canvasSize)
			.aspectRatio(1f)
			.pointerInput(Unit) {
				detectTapGestures {
					if (!isFlipping) {
						isFlipping = true
					}
				}
			}
	) {
		withTransform({
			// First rotate to our starting diagonal orientation (135°)
			rotate(
				degrees = angle,
				pivot = center
			)
			// Then apply the scaling transformation for the flip effect
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
private fun DiagonalCoinFlipEffectPreview() {
	DiagonalCoinFlipEffect(
		onAnimationComplete = {}
	)
}