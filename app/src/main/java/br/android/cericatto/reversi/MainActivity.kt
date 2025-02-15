package br.android.cericatto.reversi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.android.cericatto.reversi.navigation.NavHostComposable
import br.android.cericatto.reversi.ui.theme.ReversiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			ReversiTheme {
				NavHostComposable()
			}
		}
	}
}