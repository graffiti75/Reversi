package br.android.cericatto.reversi.ui.board

import android.content.Context
import androidx.lifecycle.ViewModel
import br.android.cericatto.reversi.ui.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor(
	private val context: Context
): ViewModel() {

	private val _events = Channel<UiEvent>()
	val events = _events.receiveAsFlow()

	private val _state = MutableStateFlow(BoardState())
	val state: StateFlow<BoardState> = _state.asStateFlow()

	fun onAction(action: BoardAction) {
		when (action) {
			is BoardAction.OnBoardClicked -> onBoardClicked(action.position)
		}
	}

	/**
	 * State Methods
	 */

	private fun onBoardClicked(position: Position) {
		val newBoardData = _state.value.boardData.toMutableList()
		val newData = BoardData(
			cellState = CellState.BLACK,
			position = position,
			filled = true
		)
		newBoardData += newData
		_state.update { state ->
			state.copy(
				boardData = newBoardData
			)
		}
	}
}