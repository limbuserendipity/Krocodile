package com.limbuserendipity.krocodile.component

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.limbuserendipity.krocodile.model.ToolType

@Composable
fun InputSection(
    isArtist: Boolean,
    onSendMessage: (String) -> Unit,
    onColorSelected: (Color) -> Unit,
    onToolSelected: (ToolType) -> Unit,
    onBrushSizeChanged: (Int) -> Unit
) {

    var selectedColor by remember {
        mutableStateOf(Color.Red)
    }

    var brushSize by remember {
        mutableStateOf(12)
    }

    if (isArtist) {
        ToolbarInput(
            selectedColor = selectedColor,
            onColorSelected = { color ->
                selectedColor = color
                onColorSelected(color)
            },
            onToolSelected = onToolSelected,
            brushSize = brushSize,
            onBrushSizeChanged = { size ->
                brushSize = size
                onBrushSizeChanged(size)
            }
        )
    } else {
        ChatInput(
            onSendMessage = onSendMessage
        )
    }

}