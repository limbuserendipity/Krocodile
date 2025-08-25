package com.limbuserendipity.krocodile.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin.Companion.Round
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun DrawingCanvas(
    paddingValues: PaddingValues,
    currentPath: Path,
    completedPaths: List<Path>,
    usersPath: List<Path>,
    onDragStart: (Offset) -> Unit,
    onDrag: (PointerInputChange, Offset) -> Unit,
    onDragEnd: () -> Unit
) {

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
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
                join = Round
            )
        )

        completedPaths.forEach { path ->
            drawPath(
                path = path,
                color = Color.Red,
                style = Stroke(
                    width = 8f,
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
                    join = Round
                )
            )
        }

    }
}