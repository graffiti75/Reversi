package br.android.cericatto.reversi.ui.board

sealed interface BoardAction {
	data class OnBoardClicked(val position: Position) : BoardAction
//	data object OnButtonClicked : BoardAction
}