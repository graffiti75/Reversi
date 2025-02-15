package br.android.cericatto.reversi.ui.board

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ClickableCanvas() {
	// State to store the current click position
	var clickPosition by remember { mutableStateOf<Offset?>(null) }

	// State to store the current drag position
	var dragPosition by remember { mutableStateOf<Offset?>(null) }

	// State to store a list of all click positions if you want to track multiple clicks
	var clickHistory by remember { mutableStateOf(listOf<Offset>()) }

	Canvas(
		modifier = Modifier
			.fillMaxSize()
			// Handle tap gestures
			.pointerInput(Unit) {
				detectTapGestures { offset ->
					// Update the click position when a tap is detected
					clickPosition = offset
					// Add the click to history
					clickHistory = clickHistory + offset
				}
			}
			// Handle drag gestures
			.pointerInput(Unit) {
				detectDragGestures { change, dragAmount ->
					// Update the drag position as the user drags
					dragPosition = change.position
				}
			}
	) {
		// Draw a visual indicator for the current click position
		clickPosition?.let { position ->
			drawCircle(
				color = Color.Red,
				radius = 20f,
				center = position
			)
		}

		// Draw a different visual indicator for the drag position
		dragPosition?.let { position ->
			drawCircle(
				color = Color.Blue,
				radius = 20f,
				center = position
			)
		}

		// Draw smaller dots for historical clicks
		clickHistory.forEach { position ->
			drawCircle(
				color = Color.Gray,
				radius = 5f,
				center = position
			)
		}
	}
}

@Preview
@Composable
private fun ClickableCanvasPreview() {
	ClickableCanvas()
}