package br.android.cericatto.reversi.ui.board.common

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.android.cericatto.reversi.ui.board.BoardAction
import br.android.cericatto.reversi.ui.board.BoardState
import br.android.cericatto.reversi.ui.board.GridCanvas
import br.android.cericatto.reversi.ui.theme.boardGreen
import br.android.cericatto.reversi.ui.theme.boardMustard

@Composable
fun PortraitContent(
	canvasSize: Dp = 300.dp,
	padding: Dp = 5.dp,
	onAction: (BoardAction) -> Unit,
	state: BoardState
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
		modifier = Modifier.fillMaxSize()
			.size(canvasSize)
			.padding(padding)
	) {
		ScoreBoard(
			onAction = onAction,
			state = state
		)
		Box(
			contentAlignment = Alignment.Center,
			modifier = Modifier
				.size(canvasSize)
				.aspectRatio(1f)
				.background(
					color = boardMustard,
					shape = RoundedCornerShape(5.dp)
				)
				.padding(padding)
				.background(boardGreen)
		) {
			GridCanvas(
				canvasSize = canvasSize,
				onAction = onAction,
				state = state
			)
		}
	}
}

@Composable
fun ScoreBoard(
	onAction: (BoardAction) -> Unit,
	state: BoardState
) {
	UndoText(
		onAction = onAction,
		state = state
	)
	Spacer(modifier = Modifier.size(10.dp))
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceEvenly,
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
		ScoreText(text = state.score.white.toString())
		PlayerText(
			text = "White",
			textColor = Color.White
		)
	}
}

@Preview
@Composable
private fun ScoreBoardPreview() {
	ScoreBoard(
		onAction = {},
		state = BoardState()
	)
}

@Preview
@Composable
private fun PortraitContentPreview() {
	PortraitContent(
		onAction = {},
		state = BoardState()
	)
}