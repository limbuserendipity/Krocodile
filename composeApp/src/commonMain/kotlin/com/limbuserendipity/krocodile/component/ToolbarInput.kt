package com.limbuserendipity.krocodile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.Res
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.eraser
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.l_arrow_up_left
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.trash_fill
import com.limbuserendipity.krocodile.model.ToolType
import com.limbuserendipity.krocodile.util.Space
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolbarInput(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    onToolSelected: (ToolType) -> Unit,
    brushSize: Int,
    onBrushSizeChanged: (Int) -> Unit
) {
    val colors = listOf(
        Color.Black,
        Color(0xFFFF5252),
        Color(0xFF4CAF50),
        Color(0xFF2196F3),
        Color(0xFFFF9800),
        Color(0xFF9C27B0)
    )
    val brushSizes = listOf(8, 16, 24, 32, 64)
    var expanded by remember { mutableStateOf(false) }

    var undoCount by remember {
        mutableStateOf(0)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .padding(24.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
                .padding(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
            ) {
                colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (color == selectedColor) 3.dp else 2.dp,
                                color = if (color == selectedColor) Color(0xFF212121) else Color(0xFFE0E0E0),
                                shape = CircleShape
                            )
                            .clickable { onColorSelected(color) }
                    )
                }

                8.dp.Space()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                .clickable { expanded = true }
                                .menuAnchor(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = brushSize.toString(),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontSize = 14.sp
                            )
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expanded,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                            )
                        }
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            brushSizes.forEach { size ->
                                DropdownMenuItem(
                                    text = { Text(size.toString()) },
                                    onClick = {
                                        onBrushSizeChanged(size)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

            }

        }

        8.dp.Space()

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            ToolButton(
                painter = painterResource(Res.drawable.eraser),
                onClick = { onToolSelected(ToolType.Eraser) },
                tooltip = "Ластик"
            )
            4.dp.Space()
            ToolButton(
                painter = painterResource(Res.drawable.l_arrow_up_left),
                onClick = {
                    onToolSelected(ToolType.Undo().apply {
                        undoCount++
                        count = undoCount
                    })
                },
                tooltip = "Назад"
            )
            4.dp.Space()
            ToolButton(
                painter = painterResource(Res.drawable.trash_fill),
                onClick = { onToolSelected(ToolType.Clear) },
                backgroundColor = Color(0xFFFF5252),
                contentColor = Color.White,
                tooltip = "Очистить всё"
            )
        }

        4.dp.Space()

    }
}

@Composable
fun ToolButton(
    painter: Painter,
    onClick: () -> Unit,
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    tooltip: String
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(34.dp)
            .shadow(4.dp, RoundedCornerShape(8.dp))
            .background(backgroundColor, RoundedCornerShape(8.dp))
    ) {
        Icon(
            painter = painter,
            contentDescription = tooltip,
            tint = contentColor
        )
    }
}

