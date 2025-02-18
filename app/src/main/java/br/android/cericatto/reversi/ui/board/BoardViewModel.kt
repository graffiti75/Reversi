package br.android.cericatto.reversi.ui.board

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.android.cericatto.reversi.ui.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
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
//			is BoardAction.OnButtonClicked -> onButtonClicked()
		}
	}

	/**
	 * State Methods
	 */

	private fun onBoardClicked(position: Position) {
		viewModelScope.launch {
			val dataAlreadyInList = _state.value.boardData.find { it.position == position }
			if (dataAlreadyInList == null) {
				val newBoardData = _state.value.boardData.toMutableList()
				val newData = BoardData(
					cellState = _state.value.currentPlayer,
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
				delay(100)
				checkAllMovements()
				updateGameScore()
			}
		}
	}

	private fun checkAllMovements() {
		println("==================================================\n")
		println("==================================================\n")
		addMovementPieces(Movement.NORTH)
		addMovementPieces(Movement.NORTHEAST)
		addMovementPieces(Movement.EAST)
		addMovementPieces(Movement.SOUTHEAST)
		addMovementPieces(Movement.SOUTH)
		addMovementPieces(Movement.SOUTHWEST)
		addMovementPieces(Movement.WEST)
		addMovementPieces(Movement.NORTHWEST)
	}

	private fun addMovementPieces(movement: Movement) {
		val list = checkMovement(movement)
		if (list.isNotEmpty()) {
			_state.update { state ->
				state.copy(
					boardData = list
				)
			}
		}
	}

	private fun checkMovement(movement: Movement): List<BoardData> {
		val last = _state.value.last!!
		val list = _state.value.boardData
		val data = when (movement) {
			Movement.NORTH -> checkNorth(last, list)
			Movement.NORTHEAST -> checkNortheast(last, list)
			Movement.EAST -> checkEast(last, list)
			Movement.SOUTHEAST -> checkSoutheast(last, list)
			Movement.SOUTH -> checkSouth(last, list)
			Movement.SOUTHWEST -> checkSouthwest(last, list)
			Movement.WEST -> checkWest(last, list)
			Movement.NORTHWEST -> checkNorthwest(last, list)
		}

		var updatedList: List<BoardData> = emptyList()
		if (data.isNotEmpty()) {
			val set = data.map { it.position }.toSet()
			updatedList = _state.value.boardData.map { item ->
				if (item.position in set) {
					item.copy(cellState = _state.value.currentPlayer)
				} else {
					item
				}
			}
		}
		return updatedList
	}

	private fun updateGameScore() {
		val updated = calculateScore(_state.value.boardData)
		_state.update { state ->
			state.copy(
				score = updated
			)
		}
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