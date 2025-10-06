package com.limbuserendipity.krocodile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.limbuserendipity.krocodile.util.Space

@Composable
fun WordSelectionDialog(
    time: Float,
    words: List<String>,
    onWordSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {

    KrocodileDialog(
        time = time,
        onDismissRequest = onDismiss,
        title = "\uD83E\uDD14 Выберите слово для рисования",
        content = {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                4.dp.Space()

                Text(
                    text = "У вас 60 секунд на рисунок",
                    fontSize = 14.sp,
                    color = Color(0xFF454545),
                )

                4.dp.Space()

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    items(words) { word ->
                        WordButton(
                            word = word,
                            onClick = {
                                onWordSelected(word)
                                onDismiss()
                            }
                        )
                    }
                }

                Text(
                    text = "Выберите одно из слов и начните рисовать",
                    fontSize = 12.sp,
                    color = Color(0xFF454545),
                    modifier = Modifier
                        .padding(top = 24.dp)
                )
            }

        }
    )
}

@Composable
fun WordButton(
    word: String,
    onClick: () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isHovered) Color(0xFF3DAE8B).copy(alpha = 0.1f)
                else Color(0xFFF2FFFF)
            )
            .border(
                width = 2.dp,
                color = if (isHovered) Color(0xFF3DAE8B) else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }

            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.type == PointerEventType.Enter) {
                            isHovered = true
                        }
                        if (event.type == PointerEventType.Exit) {
                            isHovered = false
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = word,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1A1A1A)
        )
    }
}