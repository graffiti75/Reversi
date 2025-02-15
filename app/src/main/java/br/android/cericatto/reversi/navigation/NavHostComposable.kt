package br.android.cericatto.reversi.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.android.cericatto.reversi.ui.board.BoardScreenRoot

@Composable
fun NavHostComposable() {
	val navController = rememberNavController()
	NavHost(
		navController = navController,
		startDestination = Route.PinScreen
	) {
		composable<Route.PinScreen> {
			BoardScreenRoot(
				onNavigate = { navController.navigate(it) },
				onNavigateUp = { navController.navigateUp() }
			)
		}
	}
}