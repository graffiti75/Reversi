package br.android.cericatto.reversi.ui.board

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.android.cericatto.reversi.ObserveAsEvents
import br.android.cericatto.reversi.navigation.Route
import br.android.cericatto.reversi.ui.UiEvent
import br.android.cericatto.reversi.ui.theme.boardGreen
import br.android.cericatto.reversi.ui.theme.boardMustard
import br.android.cericatto.reversi.ui.theme.orange
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.cos

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
	state: BoardState
) {
	Scaffold { innerPadding ->
		BoardMainContent(
			modifier = Modifier
				.padding(innerPadding),
			onAction = onAction,
			state = state
		)
	}
}

@Composable
private fun BoardMainContent(
	modifier: Modifier,
	onAction: (BoardAction) -> Unit,
	state: BoardState
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
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.Center,
			modifier = Modifier.background(
					color = boardMustard,
					shape = RoundedCornerShape(20.dp)
				)
				.fillMaxWidth()
				.wrapContentHeight()
				.padding(vertical = 10.dp)
		) {
			PlayerText()
			ScoreText(
				text = state.score.black.toString(),
				backgroundColor = Color.Black,
				textColor = Color.White
			)
			Spacer(modifier = Modifier.size(20.dp))
			ScoreText(text = state.score.white.toString())
			PlayerText(
				text = "White",
				textColor = Color.White
			)
		}
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
			/*
			DiagonalCoinFlipEffect(
				canvasSize = width,
				firstColor = Color.Black,
				secondColor = Color.White,
				onAnimationComplete = {}
			)
			 */
			CircleDrawingCanvas(
				canvasSize = width,
				firstColor = Color.Black,
				secondColor = Color.White
			)
			/*
			GridCanvas(
				canvasSize = width,
				onAction = onAction,
				state = state
			)
			 */
		}
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
			println("[GridCanvas] position: ${item.boardPosition}, color: ${item.cellState.name}")
			if (item.cellState != CellState.EMPTY) {
				val center = centerPosition(
					cellSize = cellSize,
					row = item.boardPosition.row,
					col = item.boardPosition.col
				)
				drawCircle(
					color = if (item.cellState == CellState.BLACK) Color.Black else Color.White,
					radius = radius,
					center = center,
					style = Fill
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
				println("clickPosition: $pair")
				drawCircle(
					color = if (state.last.cellState == CellState.BLACK) Color.Black else Color.White,
					radius = radius,
					center = Offset(center.x, center.y),
					style = Fill
				)
				onAction(BoardAction.OnMovementPlayed(boardPosition = BoardPosition(pair.first, pair.second)))
			}
		}
	}
}

@Composable
private fun RowScope.PlayerText(
	text: String = "Black",
	textColor: Color = Color.Black
) {
	Text(
		text = text,
		style = TextStyle(
			fontSize = 24.sp,
			fontWeight = FontWeight.Bold,
			textAlign = TextAlign.Center,
			color = textColor
		),
		modifier = Modifier.weight(1f)
	)
}

@Composable
private fun ScoreText(
	text: String = " 0 ",
	textColor: Color = Color.Black,
	backgroundColor: Color = Color.White
) {
	val fixed = if (text.length < 2) " $text " else text
	Text(
		text = fixed,
		style = TextStyle(
			fontSize = 20.sp,
			fontWeight = FontWeight.Bold,
			color = textColor,
			textAlign = TextAlign.Center
		),
		modifier = Modifier.background(
			color = backgroundColor,
			shape = RoundedCornerShape(20.dp)
		)
		.padding(10.dp)
	)
}

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

@Preview
@Composable
private fun BoardMainContentPreview() {
	BoardMainContent(
		modifier = Modifier,
		onAction = {},
		state = BoardState()
	)
}

@Preview
@Composable
private fun ScoreTextPreview() {
	ScoreText(
		backgroundColor = orange,
		textColor = Color.Black
	)
}

@Preview
@Composable
private fun DiagonalCoinFlipEffectPreview() {
	DiagonalCoinFlipEffect(
		onAnimationComplete = {}
	)
}