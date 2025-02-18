package br.android.cericatto.reversi.ui.board

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import br.android.cericatto.reversi.ui.theme.teal
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
			Text(
				text = "Black",
				style = TextStyle(
					fontSize = 24.sp,
					fontWeight = FontWeight.Bold,
					textAlign = TextAlign.Center
				),
				modifier = Modifier.weight(1f)
			)
			ScoreText(
				text = state.score.black.toString(),
				backgroundColor = Color.Black,
				textColor = Color.White
			)
			Spacer(modifier = Modifier.size(20.dp))
			ScoreText(text = state.score.white.toString())
			Text(
				text = "White",
				style = TextStyle(
					fontSize = 24.sp,
					fontWeight = FontWeight.Bold,
					textAlign = TextAlign.Center,
					color = Color.White
				),
				modifier = Modifier.weight(1f)
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
			GridCanvas(
				canvasSize = width,
				onAction = onAction,
				state = state
			)
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
	var clickPosition by remember { mutableStateOf<Offset?>(null) }
	var radius by remember { mutableFloatStateOf(0f) }
	var cellSize by remember { mutableFloatStateOf(0f) }

	// We create a squared canvas with equal width and height.
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
//		println("canvasWidth: $canvasWidth")
//		println("canvasHeight: $canvasHeight")

		// Calculate the cell size based on the canvas dimensions.
		cellSize = canvasWidth / gridSize
//		println("cellSize: $cellSize")

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

		// Draw circles based on the board state.
		state.boardData.forEach { item ->
			println("[GridCanvas] position: ${item.position}, color: ${item.cellState.name}")
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
				val pair = boardPosition(
					cellSize = cellSize,
					position = position
				)
				println("clickPosition: $pair")
				drawCircle(
					color = if (state.currentPlayer == CellState.BLACK) Color.Black else Color.White,
					radius = radius,
					center = Offset(center.x, center.y),
					style = Fill
				)
				onAction(BoardAction.OnBoardClicked(position = Position(pair.first, pair.second)))
			}
		}
	}
}

@Composable
private fun ScoreText(
	text: String = " 0 ",
	textColor: Color = Color.Black,
	backgroundColor: Color = Color.White,
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