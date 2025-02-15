package br.android.cericatto.reversi.ui.board

sealed interface BoardAction {
	data class OnPerformAnimationToggled(val performAnimation: Boolean) : BoardAction
	data class OnIsEnabledToggled(val enabled: Boolean) : BoardAction
	data class OnShouldShowLogoutToggled(val show: Boolean) : BoardAction
	data class OnBoardDigitClicked(val clicked : Boolean) : BoardAction
}