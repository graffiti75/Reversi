package br.android.cericatto.reversi.ui.board

import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
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
			is BoardAction.OnMovementPlayed -> onMovementPlayed(action.boardPosition)
			is BoardAction.OnUpdateClickedPosition -> onUpdateClickedPosition(action.offsetPosition)
			is BoardAction.OnUndoButtonClicked -> onUndoButtonClicked()
			is BoardAction.OnUpdateAnimationStatus -> onUpdateAnimationStatus(action.boardPosition)
		}
	}

	/**
	 * Action Methods
	 */

	private fun onMovementPlayed(boardPosition: BoardPosition) {
		viewModelScope.launch {
			val dataAlreadyInList = _state.value.boardData.find { it.boardPosition == boardPosition }
			if (dataAlreadyInList == null) {
				val newBoardData = _state.value.boardData.toMutableList()
				val round = _state.value.round + 1
				val clicked = BoardCell(
					cellState = if (round % 2 == 1) CellState.BLACK else CellState.WHITE,
					boardPosition = boardPosition,
					filled = true
				)
				newBoardData += clicked
				_state.update { state ->
					state.copy(
						boardData = newBoardData,
						last = clicked
					)
				}
				checkAllMovements()
				updateGameScore()
				updateRound(round)
				onUpdateClickedPosition(null)
				updateGameHistory()
			}
		}
	}

	private fun checkAllMovements() {
		println("==================================================\n")
		println("==================================================\n")
		addMovementPieces(Directions.NORTH)
		addMovementPieces(Directions.NORTH_EAST)
		addMovementPieces(Directions.EAST)
		addMovementPieces(Directions.SOUTH_EAST)
		addMovementPieces(Directions.SOUTH)
		addMovementPieces(Directions.SOUTH_WEST)
		addMovementPieces(Directions.WEST)
		addMovementPieces(Directions.NORTH_WEST)
	}

	private fun addMovementPieces(movement: Directions) {
		val list = checkMovement(movement)
		if (list.isNotEmpty()) {
			_state.update { state ->
				state.copy(
					boardData = list
				)
			}
		}
	}

	private fun checkMovement(movement: Directions): List<BoardCell> {
		val last = _state.value.last!!
		val list = _state.value.boardData
		val eatenPieces = when (movement) {
			Directions.NORTH -> checkNorthDirection(last, list)
			Directions.NORTH_EAST -> checkNortheastDirection(last, list)
			Directions.EAST -> checkEastDirection(last, list)
			Directions.SOUTH_EAST -> checkSoutheastDirection(last, list)
			Directions.SOUTH -> checkSouthDirection(last, list)
			Directions.SOUTH_WEST -> checkSouthwestDirection(last, list)
			Directions.WEST -> checkWestDirection(last, list)
			Directions.NORTH_WEST -> checkNorthwestDirection(last, list)
		}

		var updatedList: List<BoardCell> = emptyList()
		if (eatenPieces.isNotEmpty()) {
			val set = eatenPieces.map { it.boardPosition }.toSet()
			updatedList = _state.value.boardData.map { item ->
				if (item.boardPosition in set) {
					item.copy(
						cellState = last.cellState,
						shouldAnimate = true
					)
				} else {
					item.copy(
						shouldAnimate = false
					)
				}
			}
		}
		return updatedList
	}

	private fun onUpdateClickedPosition(position: Offset?) {
		_state.update { state ->
			state.copy(
				clickedPosition = position,
			)
		}
	}

	private fun onUndoButtonClicked() {
		val round = _state.value.round - 1
		val history = _state.value.history
		val undoHistory = history.slice(0 .. round)
		_state.update { state ->
			state.copy(
				round = round,
				history = undoHistory,
				boardData = history[round].snapshot
			)
		}
		updateGameScore()
	}

	private fun onUpdateAnimationStatus(boardPosition: BoardPosition) {
		_state.update { state ->
			state.copy(
				boardData = state.boardData.map { item ->
					if (item.boardPosition == boardPosition) {
						item.copy(shouldAnimate = false)
					} else {
						item
					}
				},
				animationProgress = 0f
			)
		}
	}

	/**
	 * State Methods
	 */

	private fun updateGameHistory() {
		val boardData = _state.value.boardData
		val history = _state.value.history
		val newSnapshot = Snapshot(boardData)
		_state.update { state ->
			state.copy(
				history = history + newSnapshot
			)
		}
	}

	private fun updateRound(newRound: Int) {
		_state.update { state ->
			state.copy(
				round = newRound
			)
		}
	}

	private fun updateGameScore() {
		val updated = calculateScore(_state.value.boardData)
		_state.update { state ->
			state.copy(
				score = updated
			)
		}
	}
}