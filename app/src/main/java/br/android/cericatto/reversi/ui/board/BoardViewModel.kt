package br.android.cericatto.reversi.ui.board

import android.content.Context
import androidx.lifecycle.ViewModel
import br.android.cericatto.reversi.ui.UiEvent
import br.android.cericatto.reversi.ui.theme.circleBackgroundFilled
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

	private val _state = MutableStateFlow(PinState())
	val state: StateFlow<PinState> = _state.asStateFlow()

	init {
		_state.update { state ->
			state.copy(
				isLoading = false
			)
		}
	}

	fun onAction(action: BoardAction) {
		when (action) {
			is BoardAction.OnPerformAnimationToggled -> onPerformAnimationToggled(action.performAnimation)
			is BoardAction.OnIsEnabledToggled -> onIsEnabledToggled(action.enabled)
			is BoardAction.OnShouldShowLogoutToggled -> onShouldShowLogoutToggled(action.show)
			is BoardAction.OnBoardDigitClicked -> onPinDigitClicked(action.clicked)
		}
	}

	/**
	 * State Methods
	 */

	private fun onPerformAnimationToggled(performAnimation: Boolean) {
		_state.update { state ->
			state.copy(
				performAnimation = performAnimation
			)
		}
	}

	private fun onIsEnabledToggled(enabled: Boolean) {
		_state.update { state ->
			state.copy(
				isEnabled = enabled
			)
		}
	}

	private fun onShouldShowLogoutToggled(show: Boolean) {
		_state.update { state ->
			state.copy(
				shouldShowLogout = show
			)
		}
	}

	private fun onPinDigitClicked(clicked: Boolean) {
		// Update current step number.
		var currentStep = _state.value.currentCircleStep
		if (clicked) {
			if (currentStep < MAX_PIN_TYPED)
			currentStep++
		}

		// Update PIN colors.
		var newColors = _state.value.pinCircleStepsColors.toMutableList()
		newColors[currentStep - 1] = circleBackgroundFilled

		// Update states.
		_state.update { state ->
			state.copy(
				currentCircleStep = currentStep,
				pinCircleStepsColors = newColors
			)

		}
	}
}