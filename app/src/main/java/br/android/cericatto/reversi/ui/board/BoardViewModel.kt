package br.android.cericatto.reversi.ui.board

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.viewModelScope
import br.android.cericatto.reversi.ui.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
			is BoardAction.OnButtonClicked -> onButtonClicked()
		}
	}

	/**
	 * State Methods
	 */

	private fun onBoardClicked(position: Position) {
		val dataAlreadyInList = _state.value.boardData.find { it.position == position }
		if (dataAlreadyInList == null) {
			val newBoardData = _state.value.boardData.toMutableList()
			val newData = BoardData(
				cellState = CellState.BLACK,
				position = position,
				filled = true
			)
			newBoardData += newData
			_state.update { state ->
				state.copy(
					boardData = newBoardData,
					last = newData
				)
			}
		}
	}

	private fun onButtonClicked() {
		/*
		_state.value.last?.let { last ->
			_state.value.boardData.forEach { item ->
				val distance = differenceBetweenTwoPoints(
					last.position, item.position
				)
				println("----- ($item) distance: $distance")
			}
		}
		*/
		checkNorth(
			_state.value.last!!,
			_state.value.boardData
		)
	}
}