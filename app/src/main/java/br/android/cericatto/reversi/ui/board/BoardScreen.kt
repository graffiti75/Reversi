package br.android.cericatto.reversi.ui.board

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.android.cericatto.reversi.ObserveAsEvents
import br.android.cericatto.reversi.navigation.Route
import br.android.cericatto.reversi.ui.UiEvent
import br.android.cericatto.reversi.ui.theme.boardGreen
import br.android.cericatto.reversi.ui.theme.boardMustard
import br.android.cericatto.reversi.ui.theme.orange
import kotlinx.coroutines.launch

/**
 * Enum to represent the possible states of each cell.
 */
enum class CellState {
	EMPTY,
	BLACK,
	WHITE
}

/**
 * Data class to represent a position on the board.
 */
data class Position(val row: Int, val col: Int)

/**
 * Data class to control the state of each Board Piece
 */
data class BoardState(
	val cellState: CellState = CellState.EMPTY,
	val position: Position = Position(0, 0),
	val filled : Boolean = false
)

@Composable
fun BoardScreenRoot(
	onNavigate: (Route) -> Unit,
	onNavigateUp: () -> Unit,
	viewModel: BoardViewModel = hiltViewModel()
) {
	val state by viewModel.state.collectAsStateWithLifecycle()
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
		onAction = viewModel::onAction,
		state = state
	)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BoardScreen(
	onAction: (BoardAction) -> Unit,
	state: PinState
) {
	if (state.isLoading) {
		Box(
			modifier = Modifier
				.padding(vertical = 20.dp)
				.fillMaxSize()
				.background(Color.White),
			contentAlignment = Alignment.Center
		) {
			CircularProgressIndicator(
				color = MaterialTheme.colorScheme.primary,
				strokeWidth = 4.dp,
				modifier = Modifier.size(64.dp)
			)
		}
	} else {
		Scaffold { innerPadding ->
			BoardMainContent(
				onAction = onAction,
				modifier = Modifier
					.padding(innerPadding),
				state = state
			)
		}
	}
}

@Composable
private fun BoardMainContent(
	onAction: (BoardAction) -> Unit,
	modifier: Modifier,
	state: PinState
) {
	val configuration = LocalConfiguration.current
	val padding = 5.dp
	val width = configuration.screenWidthDp.dp

	val sampleBoardState = mapOf(
		Position(0, 0) to CellState.BLACK,
		Position(0, 1) to CellState.WHITE,
		Position(3, 4) to CellState.BLACK,
		Position(3, 3) to CellState.WHITE,
		Position(5, 2) to CellState.WHITE,
		Position(5, 5) to CellState.WHITE,
		Position(7, 1) to CellState.BLACK,
		Position(7, 6) to CellState.BLACK
	)

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
			//
			GridCanvas(
				canvasSize = width,
				boardState = sampleBoardState
			)
			 //
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
	lineThickness: Float = 5f,
	// Map to store the state of each position on the board
	boardState: Map<Position, CellState> = emptyMap()
) {
	var clickPosition by remember { mutableStateOf<Offset?>(null) }
	var radius by remember { mutableFloatStateOf(0f) }

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
		val cellSize = canvasWidth / gridSize
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
		boardState.forEach { (position, state) ->
			if (state != CellState.EMPTY) {
				val center = calculateCenterPosition(
					cellSize = cellSize,
					row = position.row,
					col = position.col
				)
				drawCircle(
					color = if (state == CellState.BLACK) Color.Black else Color.White,
					radius = radius,
					center = center,
					style = Fill
				)
			}
		}

		// Draw a visual indicator for the current click position
		clickPosition?.let { position ->
			var center = calculateCenterClickedPosition(
				canvasSize = canvasSize.toPx(),
				position = position
			)
			drawCircle(
				color = orange,
//				radius = 10f,
				radius = radius,
				center = Offset(center.x, center.y),
				style = Fill
			)
		}
	}
}

fun calculateCenterPosition(
	cellSize: Float,
	row: Int,
	col: Int
): Offset {
	val centerX = (col * cellSize) + (cellSize / 2)
	val centerY = (row * cellSize) + (cellSize / 2)
	return Offset(centerX, centerY)
}

fun calculateCenterClickedPosition(
	canvasSize: Float,
	gridSize: Int = 8,
	position: Offset,
	lineThickness: Float = 5f
): Offset {
	val totalLineThickness = (gridSize + 1) * lineThickness
	val availableSpace = canvasSize - totalLineThickness
	val cellSize = availableSpace / gridSize
	val cellSizeWithLine = cellSize + lineThickness

	println("canvasSize: $canvasSize")
	println("position: $position")
	println("-----")
	println("totalLineThickness: $totalLineThickness")
	println("availableSpace: $availableSpace")
	println("cellSize: $cellSize")
	println("cellSizeWithLine: $cellSizeWithLine")
	println("-----")

	val col = (position.x / cellSize).toInt()
	val row = (position.y / cellSize).toInt()
	println("col: $col")
	println("row: $row")

	val shiftX = lineThickness + (col * cellSizeWithLine)
	val shiftY = lineThickness + (row * cellSizeWithLine)
	val centerX = lineThickness + (col * cellSizeWithLine) + (cellSize / 2)
	val centerY = lineThickness + (row * cellSizeWithLine) + (cellSize / 2)
//	val centerX = shiftX
//	val centerY = shiftY
	println("centerX: $centerX")
	println("centerY: $centerY\n\n")

//	return Offset(centerX, centerY)
	return calculateCenterPosition(
		cellSize = cellSize,
		row = row,
		col = col
	)
}

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
fun GridScreenPreview() {
	/*
	val sampleBoardState = listOf(
		BoardState(
			cellState = CellState.BLACK,
			position = Position(0, 0),
			gridPosition = calculateGridPosition(Position(0, 0))
		),
		BoardState(
			cellState = CellState.WHITE,
			position = Position(0, 1),
			gridPosition = calculateGridPosition(Position(0, 1))
		)
	)
	 */
	val sampleBoardState = mapOf(
		Position(0, 0) to CellState.BLACK,
		Position(0, 1) to CellState.WHITE,
		Position(3, 4) to CellState.BLACK,
		Position(3, 3) to CellState.WHITE,
		Position(5, 2) to CellState.WHITE,
		Position(5, 5) to CellState.WHITE,
		Position(7, 1) to CellState.BLACK,
		Position(7, 6) to CellState.BLACK
	)
	GridCanvas(
		modifier = Modifier,
		gridSize = 8,
		lineColor = Color.Black,
		lineThickness = 2f,
		boardState = sampleBoardState
	)
}

@Preview
@Composable
private fun ClickableCanvasPreview() {
	ClickableCanvas()
}

@Preview
@Composable
private fun BoardScreenPreview() {
	BoardScreen(
		onAction = {},
		state = PinState()
	)
}

@Preview
@Composable
private fun BoardMainContentPreview() {
	BoardMainContent(
		onAction = {},
		modifier = Modifier,
		state = PinState()
	)
}