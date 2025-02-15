package br.android.cericatto.reversi.ui.board

sealed interface BoardAction {
	data class OnPerformAnimationToggled(val performAnimation: Boolean) : BoardAction
}