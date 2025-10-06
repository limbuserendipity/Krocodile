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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin.Companion.Round
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.Res
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.pen
import com.limbuserendipity.krocodile.theme.CanvasSurface
import com.limbuserendipity.krocodile.vm.PathInfo
import org.jetbrains.compose.resources.painterResource

@Composable
fun DrawingCanvas(
    paddingValues: PaddingValues,
    isArtist: Boolean,
    currentPath: PathInfo,
    completedPaths: List<PathInfo>,
    usersPath: List<PathInfo>,
    onDragStart: (Offset) -> Unit,
    onDrag: (PointerInputChange, Offset) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    var canvasSize by remember { mutableStateOf(Size.Zero) }

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
                .background(CanvasSurface),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (currentPath.points.isEmpty() && completedPaths.isEmpty() && usersPath.isEmpty()) {
                    Icon(
                        painter = painterResource(Res.drawable.pen),
                        contentDescription = ""
                    )
                    Text(
                        text = "Здесь будет рисунок",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp
                    )
                }
            }

            val drawingModifier = if (isArtist) Modifier
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val normalized = offset / canvasSize
                            onDragStart(normalized)
                        },
                        onDrag = { change, offset ->
                            val normalized = Offset(change.position.x, change.position.y) / canvasSize
                            onDrag(change, normalized)
                        },
                        onDragEnd = onDragEnd
                    )
                }
            else Modifier

            Canvas(
                modifier = drawingModifier.fillMaxSize()
                    .onSizeChanged {
                        canvasSize = Size(it.width.toFloat(), it.height.toFloat())
                    }
            ) {
                val actualCanvasSize = size

                completedPaths.forEach { pathInfo ->
                    val actualSize = pathInfo.getActualSize(actualCanvasSize)
                    drawPath(
                        path = pathInfo.toScaledPath(actualCanvasSize),
                        color = pathInfo.color,
                        style = Stroke(
                            width = actualSize,
                            cap = StrokeCap.Round,
                            join = Round
                        )
                    )
                }

                val currentActualSize = currentPath.getActualSize(actualCanvasSize)
                drawPath(
                    path = currentPath.toScaledPath(actualCanvasSize),
                    color = currentPath.color,
                    style = Stroke(
                        width = currentActualSize,
                        cap = StrokeCap.Round,
                        join = Round
                    )
                )

                usersPath.forEach { pathInfo ->
                    val actualSize = pathInfo.getActualSize(actualCanvasSize)
                    drawPath(
                        path = pathInfo.toScaledPath(actualCanvasSize),
                        color = pathInfo.color,
                        style = Stroke(
                            width = actualSize,
                            cap = StrokeCap.Round,
                            join = Round
                        )
                    )
                }
            }
        }
    }
}

operator fun Offset.div(size: Size): Offset {
    return Offset(this.x / size.width, this.y / size.height)
}