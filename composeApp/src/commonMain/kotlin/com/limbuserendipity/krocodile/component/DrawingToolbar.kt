package com.limbuserendipity.krocodile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.Res
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.direction_up_2
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.pen
import com.limbuserendipity.krocodile.theme.*
import com.limbuserendipity.krocodile.util.Space
import org.jetbrains.compose.resources.painterResource


@Composable
fun DrawingToolbar(
    toolState: DrawingToolState,
    onColorSelected: (Color) -> Unit,
    onBrushSizeSelected: (Int) -> Unit,
    onClear: () -> Unit,
    onUndo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(16.dp)
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf(
                    ColorCharcoal,
                    ColorTeal,
                    ColorSunset,
                    ColorLavender,
                    ColorCoral,
                    ColorSunshine
                ).forEach { color ->
                    ColorDot(
                        color = color,
                        isSelected = toolState.selectedColor == color,
                        onClick = { onColorSelected(color) }
                    )
                }
            }

            4.dp.Space()
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf(2, 5, 10).forEach { size ->
                    SizeOption(
                        size = size,
                        isSelected = toolState.brushSize == size,
                        onClick = { onBrushSizeSelected(size) }
                    )
                }
            }

            4.dp.Space()
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ToolButton(
                    icon = { ClearIcon() },
                    onClick = onClear
                )
                ToolButton(
                    icon = { UndoIcon() },
                    onClick = onUndo,
                    primary = true
                )
            }
        }
    }
}

@Composable
private fun SizeOption(
    size: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) Color(0xFF3DAE8B) else MaterialTheme.colorScheme.surface,
                RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) Color.Transparent else Color(0xFFCCCCCC),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$size",
            fontSize = 14.sp,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ColorDot(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) Color(0xFF3DAE8B) else Color(0xFFCCCCCC),
                shape = CircleShape
            )
            .clickable(onClick = onClick)
    )
}

@Composable
private fun ToolButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    primary: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (primary) Color(0xFF3DAE8B) else MaterialTheme.colorScheme.surface,
                    CircleShape
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFFCCCCCC),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
    }
}

data class DrawingToolState(
    val selectedColor: Color = Color.Black,
    val brushSize: Int = 5
)

@Composable
fun ClearIcon() {
    Icon(painterResource(Res.drawable.direction_up_2), contentDescription = "Очистить")
}

@Composable
fun UndoIcon() {
    Icon(painterResource(Res.drawable.pen), contentDescription = "Отменить")
}