package br.android.cericatto.reversi.navigation

import androidx.navigation.NavController
import br.android.cericatto.reversi.ui.UiEvent

fun NavController.navigate(event: UiEvent.Navigate) {
    this.navigate(event.route)
}