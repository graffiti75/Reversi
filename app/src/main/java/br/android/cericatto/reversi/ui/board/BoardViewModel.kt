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
				cellState = CellState.WHITE,
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
		// North.
		val north = checkMovement(Movement.NORTH)
		if (north.isNotEmpty()) {
			_state.update { state ->
				state.copy(
					boardData = north
				)
			}
		}

		// South.
		val south = checkMovement(Movement.SOUTH)
		if (south.isNotEmpty()) {
			_state.update { state ->
				state.copy(
					boardData = south
				)
			}
		}

	}

	private fun checkMovement(movement: Movement): List<BoardData> {
		val last = _state.value.last!!
		val list = _state.value.boardData
		val data = when (movement) {
			Movement.NORTH -> checkNorth(last, list)
			Movement.SOUTH -> checkSouth(last, list)
		}

		var updatedList: List<BoardData> = emptyList()
		if (data.isNotEmpty()) {
			val set = data.map { it.position }.toSet()
			updatedList = _state.value.boardData.map { item ->
				if (item.position in set) {
					item.copy(cellState = _state.value.currenPlayer)
				} else {
					item
				}
			}
		}
		return updatedList
	}

	private fun checkDistances() {
		_state.value.last?.let { last ->
			_state.value.boardData.forEach { item ->
				val distance = differenceBetweenTwoPoints(
					last.position, item.position
				)
				println("----- ($item) distance: $distance")
			}
		}
	}
}