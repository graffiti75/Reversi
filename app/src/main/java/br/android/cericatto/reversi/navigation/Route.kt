package br.android.cericatto.reversi.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
	@Serializable
	data object PinScreen: Route

	@Serializable
	data object RegistrationScreen: Route
}