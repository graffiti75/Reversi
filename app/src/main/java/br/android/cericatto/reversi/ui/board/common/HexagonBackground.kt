package br.android.cericatto.reversi.ui.board.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.tooling.preview.Preview
import br.android.cericatto.reversi.ui.theme.boardMustard

fun HexagonBackground(
	color: Color = boardMustard
): Brush {
	return object : ShaderBrush() {
		override fun createShader(size: Size): Shader {
			val path = createHexagonPath(size)
			val bitmap = ImageBitmap(size.width.toInt(), size.height.toInt())
			val canvas = androidx.compose.ui.graphics.Canvas(bitmap)

			// Fill the hexagonal path with the desired color
			canvas.drawPath(path, Paint().apply { this.color = color })
			return ImageShader(bitmap, TileMode.Clamp)
		}
	}
}

private fun createHexagonPath(size: Size): Path {
	val width = size.width
	val height = size.height
	val path = Path()

	val quarterWidth = width / 4
	val threeQuarterWidth = 3 * quarterWidth
	val quarterHeight = height / 4
	val threeQuarterHeight = 3 * quarterHeight

	path.moveTo(quarterWidth, 0f)
	path.lineTo(threeQuarterWidth, 0f)
	path.lineTo(width, quarterHeight)
	path.lineTo(width, threeQuarterHeight)

	path.lineTo(threeQuarterWidth, height)
	path.lineTo(threeQuarterWidth, height)
	path.lineTo(quarterWidth, height)
	path.lineTo(0f, threeQuarterHeight)
	path.lineTo(0f, quarterHeight)

	path.close()

	return path
}

@Preview
@Composable
private fun HexagonalBackgroundPreview() {
	HexagonBackground()
}