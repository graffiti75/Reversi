package br.android.cericatto.reversi.ui.board

import androidx.compose.ui.graphics.Color
import br.android.cericatto.reversi.ui.theme.circleBackground

const val MAX_PIN_TYPED = 5

data class PinState(
	val isLoading : Boolean = false,
	val isEnabled : Boolean = false,
	val shouldShowLogout : Boolean = false,
	val performAnimation : Boolean = false,
	val username : String = "JohnDoe",
	val title : String = "Hello, $username!",
	val subtitle : String = "Enter your PIN",
	val pinCircleStepsColors : List<Color> = listOf(
		circleBackground, circleBackground, circleBackground, circleBackground, circleBackground
	),
	val currentCircleStep : Int = 0
)