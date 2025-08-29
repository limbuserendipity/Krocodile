package com.limbuserendipity.krocodile.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin.Companion.Round
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.Res
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.baseline_arrow_back_ios_24
import org.jetbrains.compose.resources.painterResource

@Composable
fun DrawingCanvas(
    paddingValues: PaddingValues,
    currentPath: Path,
    completedPaths: List<Path>,
    usersPath: List<Path>,
    onDragStart: (Offset) -> Unit,
    onDrag: (PointerInputChange, Offset) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = modifier
            .padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .height(256.dp)
                .border(2.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .background(Color(0xFFF8F8F8)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (currentPath.isEmpty && completedPaths.isEmpty() && usersPath.isEmpty()) {
                    Icon(
                        painter = painterResource(Res.drawable.baseline_arrow_back_ios_24),
                        contentDescription = ""
                    )
                    Text(
                        text = "Здесь будет рисунок",
                        color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp
                    )
                }
            }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = onDragStart,
                            onDrag = onDrag,
                            onDragEnd = onDragEnd
                        )
                    }
            ) {

                drawPath(
                    path = currentPath,
                    color = Color.Blue,
                    style = Stroke(
                        width = 8f,
                        cap = StrokeCap.Round,
                        join = Round
                    )
                )

                completedPaths.forEach { path ->
                    drawPath(
                        path = path,
                        color = Color.Red,
                        style = Stroke(
                            width = 8f,
                            cap = StrokeCap.Round,
                            join = Round
                        )
                    )
                }

                usersPath.forEach { path ->
                    drawPath(
                        path = path,
                        color = Color.Green,
                        style = Stroke(
                            width = 8f,
                            cap = StrokeCap.Round,
                            join = Round
                        )
                    )
                }
            }
        }
    }
}