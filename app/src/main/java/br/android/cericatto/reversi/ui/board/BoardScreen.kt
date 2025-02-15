package br.android.cericatto.reversi.ui.board

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.android.cericatto.reversi.ObserveAsEvents
import br.android.cericatto.reversi.navigation.Route
import br.android.cericatto.reversi.ui.UiEvent
import br.android.cericatto.reversi.ui.theme.boardGreen
import br.android.cericatto.reversi.ui.theme.boardMustard
import br.android.cericatto.reversi.ui.theme.teal
import kotlinx.coroutines.launch

@Composable
fun BoardScreenRoot(
	onNavigate: (Route) -> Unit,
	onNavigateUp: () -> Unit,
	viewModel: BoardViewModel = hiltViewModel()
) {
	val scope = rememberCoroutineScope()
	val snackbarHostState = remember { SnackbarHostState() }
	val context = LocalContext.current
	ObserveAsEvents(viewModel.events) { event ->
		when (event) {
			is UiEvent.ShowSnackbar -> {
				scope.launch {
					snackbarHostState.showSnackbar(
						message = event.message.asString(context)
					)
				}
			}
			is UiEvent.Navigate -> onNavigate(event.route)
			is UiEvent.NavigateUp -> onNavigateUp()
			else -> Unit
		}
	}
	BoardScreen(
		onAction = viewModel::onAction
	)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BoardScreen(
	onAction: (BoardAction) -> Unit
) {
	Scaffold { innerPadding ->
		BoardMainContent(
			onAction = onAction,
			modifier = Modifier
				.padding(innerPadding)
		)
	}
}

@Composable
private fun BoardMainContent(
	onAction: (BoardAction) -> Unit,
	modifier: Modifier
) {
	val configuration = LocalConfiguration.current
	val padding = 5.dp
	val width = configuration.screenWidthDp.dp

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
		modifier = Modifier.fillMaxSize()
			.size(width)
			.padding(padding)
	) {
		Box(
			contentAlignment = Alignment.Center,
			modifier = Modifier
				.size(width)
				.aspectRatio(1f)
				.background(
					color = boardMustard,
					shape = RoundedCornerShape(5.dp)
				)
				.padding(padding)
				.background(boardGreen)
		) {
			GridCanvas(canvasSize = width)
//			ClickableCanvas()
		}
	}
}

@Composable
fun GridCanvas(
	modifier: Modifier = Modifier,
	canvasSize: Dp = 300.dp,
	gridSize: Int = 8,
	lineColor: Color = Color.Black,
	lineThickness: Float = 5f
) {
	var clickPosition by remember { mutableStateOf<Offset?>(null) }
	var radius by remember { mutableFloatStateOf(0f) }
	var cellSize by remember { mutableFloatStateOf(0f) }

	// We create a square canvas with equal width and height
	Canvas(
		modifier = modifier.width(canvasSize)
			.aspectRatio(1f)
			.pointerInput(Unit) {
				detectTapGestures { offset ->
					clickPosition = offset
				}
			}
	) {
		val canvasWidth = size.width
		val canvasHeight = size.height
		println("canvasWidth: $canvasWidth")
		println("canvasHeight: $canvasHeight")

		// Calculate the cell size based on the canvas dimensions
		cellSize = canvasWidth / gridSize
		println("cellSize: $cellSize")

		// Draw vertical lines
		for (i in 0..gridSize) {
			val xPosition = i * cellSize
			drawLine(
				color = lineColor,
				start = Offset(xPosition, 0f),
				end = Offset(xPosition, canvasHeight),
				strokeWidth = lineThickness
			)
		}

		// Draw horizontal lines
		for (i in 0..gridSize) {
			val yPosition = i * cellSize
			drawLine(
				color = lineColor,
				start = Offset(0f, yPosition),
				end = Offset(canvasWidth, yPosition),
				strokeWidth = lineThickness
			)
		}

		// Calculate the radius (slightly smaller than the cell)
		radius = cellSize * 0.45f

		// Draw circles based on the board state
		sampleBoardState.forEach { item ->
			if (item.cellState != CellState.EMPTY) {
				val center = centerPosition(
					cellSize = cellSize,
					row = item.position.row,
					col = item.position.col
				)
				drawCircle(
					color = if (item.cellState == CellState.BLACK) Color.Black else Color.White,
					radius = radius,
					center = center,
					style = Fill
				)
			}
		}

		// Draw a visual indicator for the current click position
		clickPosition?.let { position ->
			val center = calculateCenterClickedPosition(
				cellSize = cellSize,
				position = position
			)
			val filled = boardPositionIsFilled(
				cellSize = cellSize,
				position = position
			)
			if (!filled) {
				drawCircle(
					color = teal,
					radius = radius,
					center = Offset(center.x, center.y),
					style = Fill
				)
			}
		}
	}
}

/*
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
 */

@Preview
@Composable
fun GridScreenPreview() {
	GridCanvas(
		modifier = Modifier,
		gridSize = 8,
		lineColor = Color.Black,
		lineThickness = 2f
	)
}

/*
@Preview
@Composable
private fun ClickableCanvasPreview() {
	ClickableCanvas()
}
 */

@Preview
@Composable
private fun BoardScreenPreview() {
	BoardScreen(
		onAction = {}
	)
}

@Preview
@Composable
private fun BoardMainContentPreview() {
	BoardMainContent(
		onAction = {},
		modifier = Modifier
	)
}