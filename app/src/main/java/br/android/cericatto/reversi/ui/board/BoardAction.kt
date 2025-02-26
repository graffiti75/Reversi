package br.android.cericatto.reversi.ui.board

import androidx.compose.ui.geometry.Offset

sealed interface BoardAction {
	data class OnMovementPlayed(val boardPosition: BoardPosition) : BoardAction
	data class OnUpdateClickedPosition(val offsetPosition: Offset) : BoardAction
	data object OnUndoButtonClicked : BoardAction
	data class OnUpdateAnimationStatus(val boardPosition: BoardPosition) : BoardAction
}