package br.android.cericatto.reversi.ui.board.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.android.cericatto.reversi.ui.board.BoardAction
import br.android.cericatto.reversi.ui.board.BoardState
import br.android.cericatto.reversi.ui.board.GridCanvas
import br.android.cericatto.reversi.ui.theme.boardGreen
import br.android.cericatto.reversi.ui.theme.boardMustard

@Composable
fun LandscapeContent(
	canvasSize: Dp = 300.dp,
	padding: Dp = 5.dp,
	onAction: (BoardAction) -> Unit,
	state: BoardState
) {
	val undoButtonTextSize = sp2Dp(24.sp)
	val undoButtonPadding = 10.dp
	val undoButtonHeight = undoButtonTextSize + undoButtonPadding
	val delta = undoButtonHeight + undoButtonHeight + undoButtonHeight
	val undoButtonTopPadding = (canvasSize / 2) - delta
//	println("----- canvasSize: $canvasSize")
//	println("----- undoButtonTextSize: $undoButtonTextSize")
//	println("----- undoButtonPadding: $undoButtonPadding")
//	println("----- undoButtonTopPadding: $undoButtonTopPadding")

	Row(
		horizontalArrangement = Arrangement.Center,
		verticalAlignment = Alignment.Top,
		modifier = Modifier.fillMaxSize()
	) {
		ScorePanelLeft(
			undoButtonTopPadding = undoButtonTopPadding,
			onAction = onAction,
			state = state
		)
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
			modifier = Modifier.fillMaxHeight()
		) {
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
		ScorePanelRight(
			state = state
		)
	}
}

@Composable
fun ScorePanelLeft(
	undoButtonTopPadding: Dp = 50.dp,
	onAction: (BoardAction) -> Unit,
	state: BoardState
) {
	Box(
		contentAlignment = Alignment.TopEnd,
		modifier = Modifier.padding(end = 10.dp)
	) {
		UndoText(
			modifier = Modifier.padding(top = undoButtonTopPadding),
			onAction = onAction,
			state = state
		)
		Column(
			horizontalAlignment = Alignment.End,
			verticalArrangement = Arrangement.Center,
			modifier = Modifier.fillMaxHeight()
		) {
			Row(
				horizontalArrangement = Arrangement.Center,
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier.background(
						color = boardMustard,
						shape = RoundedCornerShape(20.dp)
					)
					.padding(10.dp)
					.wrapContentWidth()
					.wrapContentHeight()
			) {
				PlayerText()
				HorizontalDivider(
					modifier = Modifier.size(10.dp),
					color = boardMustard
				)
				ScoreText(
					text = state.score.black.toString(),
					backgroundColor = Color.Black,
					textColor = Color.White
				)
			}
		}
	}
}

@Composable
fun ScorePanelRight(
	state: BoardState
) {
	Column(
		horizontalAlignment = Alignment.End,
		verticalArrangement = Arrangement.Center,
		modifier = Modifier.fillMaxHeight()
	) {
		Row(
			horizontalArrangement = Arrangement.Center,
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.padding(start = 10.dp)
				.background(
					color = boardMustard,
					shape = RoundedCornerShape(20.dp)
				)
				.padding(10.dp)
				.wrapContentWidth()
				.wrapContentHeight()
		) {
			ScoreText(text = state.score.white.toString())
			HorizontalDivider(
				modifier = Modifier.size(10.dp),
				color = boardMustard
			)
			PlayerText(
				text = "White",
				textColor = Color.White
			)
		}
	}
}

@Preview(
	name = "Landscape Mode",
	widthDp = 720,
	heightDp = 360
)
@Composable
private fun LandscapeContentPreview() {
	LandscapeContent(
		onAction = {},
		state = BoardState()
	)
}