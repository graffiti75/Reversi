package br.android.cericatto.reversi.ui.board

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.android.cericatto.reversi.ObserveAsEvents
import br.android.cericatto.reversi.navigation.Route
import br.android.cericatto.reversi.ui.UiEvent
import br.android.cericatto.reversi.ui.board.common.CoinFlip
import br.android.cericatto.reversi.ui.board.common.LandscapeContent
import br.android.cericatto.reversi.ui.board.common.PortraitContent
import br.android.cericatto.reversi.ui.board.common.getCanvasSize
import br.android.cericatto.reversi.ui.board.common.isLandscapeOrientation
import kotlinx.coroutines.launch

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

@Composable
private fun BoardScreen(
	onAction: (BoardAction) -> Unit,
	state: BoardState
) {
	Scaffold { innerPadding ->
		MainContent(
			modifier = Modifier
				.padding(innerPadding),
			onAction = onAction,
			state = state
		)
	}
}

@Composable
private fun MainContent(
	modifier: Modifier,
	onAction: (BoardAction) -> Unit,
	state: BoardState
) {
	val canvasSize = getCanvasSize()
	if (isLandscapeOrientation()) {
		LandscapeContent(
			canvasSize = canvasSize,
			onAction = onAction,
			state = state
		)
	} else {
		PortraitContent(
			canvasSize = canvasSize,
			onAction = onAction,
			state = state
		)
	}
}

@Composable
fun GridCanvas(
	modifier: Modifier = Modifier,
	onAction: (BoardAction) -> Unit,
	state: BoardState,
	canvasSize: Dp = 300.dp,
	gridSize: Int = GRID_SIZE,
	lineColor: Color = Color.Black,
	lineThickness: Float = 5f
) {
	var radius by remember { mutableFloatStateOf(0f) }
	var cellSize by remember { mutableFloatStateOf(0f) }

	// Check if any item has startAnimation = true
	val shouldAnimate = state.boardData.any { it.shouldAnimate }
	val animationProgress = remember(shouldAnimate) { Animatable(0f) }

	// Trigger animation if shouldAnimate is true.
	LaunchedEffect(shouldAnimate) {
		if (shouldAnimate) {
			// Animate from 0 to 360 degrees over 1000ms
			animationProgress.animateTo(
				targetValue = 360f,
				animationSpec = tween(durationMillis = 1000)
			)
			// Reset after animation completes
			animationProgress.snapTo(0f)
		}
	}

	// Squared Canvas with equal width and height.
	Canvas(
		modifier = modifier.width(canvasSize)
			.aspectRatio(1f)
			.pointerInput(Unit) {
				detectTapGestures { offset ->
					onAction(BoardAction.OnUpdateClickedPosition(offset))
				}
			}
	) {
		val canvasWidth = size.width
		val canvasHeight = size.height
		// Calculate the cell size based on the canvas dimensions.
		cellSize = canvasWidth / gridSize

		// Draw vertical lines.
		for (i in 0..gridSize) {
			val xPosition = i * cellSize
			drawLine(
				color = lineColor,
				start = Offset(xPosition, 0f),
				end = Offset(xPosition, canvasHeight),
				strokeWidth = lineThickness
			)
		}

		// Draw horizontal lines.
		for (i in 0..gridSize) {
			val yPosition = i * cellSize
			drawLine(
				color = lineColor,
				start = Offset(0f, yPosition),
				end = Offset(canvasWidth, yPosition),
				strokeWidth = lineThickness
			)
		}

		// Calculate the radius (slightly smaller than the cell).
		radius = cellSize * 0.45f

		// Draw Circles based on the board state.
		state.boardData.forEach { item ->
			if (item.cellState != CellState.EMPTY) {
				val center = centerPosition(
					cellSize = cellSize,
					row = item.boardPosition.row,
					col = item.boardPosition.col
				)
				CoinFlip(
					radius = radius,
					center = center,
					startAnimation = item.shouldAnimate,
					cell = item,
					animationProgress = if (item.shouldAnimate) animationProgress.value else 0f,
					onAction = onAction
				)
			}
		}

		// Draw a Circle for the current clicked position.
		state.clickedPosition?.let { position ->
			val center = calculateCenterClickedPosition(
				cellSize = cellSize,
				position = position
			)
			val filled = boardPositionIsFilled(
				cellSize = cellSize,
				position = position
			)
			if (!filled) {
				val pair = boardPosition(
					cellSize = cellSize,
					position = position
				)
				drawCircle(
					color = Color.Transparent,
					radius = radius,
					center = Offset(center.x, center.y),
					style = Fill
				)
				onAction(
					BoardAction.OnMovementPlayed(
						boardPosition = BoardPosition(pair.first, pair.second)
					)
				)
			}
		}
	}
}

@Preview
@Composable
fun GridScreenPreview() {
	GridCanvas(
		modifier = Modifier,
		onAction = {},
		state = BoardState(),
		gridSize = GRID_SIZE,
		lineColor = Color.Black,
		lineThickness = 2f
	)
}

@Preview
@Composable
private fun BoardScreenPreview() {
	BoardScreen(
		onAction = {},
		state = BoardState()
	)
}
