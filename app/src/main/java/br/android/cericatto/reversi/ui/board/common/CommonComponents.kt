package br.android.cericatto.reversi.ui.board.common

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.android.cericatto.reversi.ui.board.BoardAction
import br.android.cericatto.reversi.ui.board.BoardState
import br.android.cericatto.reversi.ui.theme.boardMustard
import br.android.cericatto.reversi.ui.theme.orange

@Composable
fun UndoText(
	textSize: TextUnit = 24.sp,
	padding: Dp = 10.dp,
	modifier: Modifier = Modifier,
	onAction: (BoardAction) -> Unit,
	state: BoardState
) {
	Text(
		text = "Undo",
		style = TextStyle(
			fontSize = textSize,
			fontWeight = FontWeight.Bold,
			textAlign = TextAlign.Center,
			color = if (state.history.size > 1) {
				Color.Black
			} else {
				Color.Black.copy(alpha = 0.2f)
			}
		),
		modifier = modifier.wrapContentWidth()
			.background(
				HexagonBackground(
					color = if (state.history.size > 1) {
						boardMustard
					} else {
						boardMustard.copy(alpha = 0.2f)
					}
				)
			)
			.padding(padding)
			.then(
				if (state.history.size > 1) {
					Modifier.clickable {
						onAction(BoardAction.OnUndoButtonClicked)
					}
				} else {
					Modifier
				}
			)
	)
}

@Composable
fun PlayerText(
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
		)
	)
}

@Composable
fun ScoreText(
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
fun sp2Dp(textSize: TextUnit): Dp {
	val density = LocalDensity.current
	return with(density) {
		textSize.toDp()
	}
}

@Composable
fun getCanvasSize(): Dp {
	val configuration = LocalConfiguration.current
	return if (isLandscapeOrientation()) {
		configuration.screenHeightDp.dp
	} else {
		configuration.screenWidthDp.dp
	}
}

@Composable
fun isLandscapeOrientation(): Boolean {
	val context = LocalContext.current
	val orientation = context.resources.configuration.orientation
	return when (orientation) {
		Configuration.ORIENTATION_LANDSCAPE -> true
		else -> false
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
private fun ScoreTextPreview() {
	ScoreText(
		backgroundColor = orange,
		textColor = Color.Black
	)
}