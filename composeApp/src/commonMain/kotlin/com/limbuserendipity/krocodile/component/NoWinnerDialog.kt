package com.limbuserendipity.krocodile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.limbuserendipity.krocodile.model.ChatMessageData
import com.limbuserendipity.krocodile.theme.CanvasSurface
import com.limbuserendipity.krocodile.theme.KrocodileTheme
import com.limbuserendipity.krocodile.util.Space
import com.limbuserendipity.krocodile.vm.PathInfo
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun NoWinnerDialog(
    time: Float,
    guessedWord: String,
    closeAnswers: List<ChatMessageData>,
    paths: List<PathInfo>,
    onDismissRequest: () -> Unit,
) {
    KrocodileDialog(
        time = time,
        onDismissRequest = onDismissRequest,
        title = "🤔 Время вышло!"
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {

                4.dp.Space()

                Text(
                    text = "Загаданное слово:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = guessedWord,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                ScaledCanvasPreview(
                    paths = paths,
                    modifier = Modifier
                        .background(CanvasSurface)
                        .size(128.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            Text(
                text = "Ближайшие ответы:",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(closeAnswers) { messageData ->
                    CloseAnswerItem(messageData)
                }
            }
        }
    }
}

@Composable
fun CloseAnswerItem(messageData: ChatMessageData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(start = 4.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)

        ) {
            Text(
                text = messageData.message,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
            )

            if (messageData.fire > 0) {

                Spacer(Modifier.weight(1f))

                Text(
                    text = "${messageData.fire} \uD83D\uDD25",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp)
                )

            }
        }
    }
}


@Preview
@Composable
fun PreviewNoWinnerDialog() {
    KrocodileTheme {
        NoWinnerDialog(
            time = 10f,
            guessedWord = "ХуЙ",
            closeAnswers = listOf(
                ChatMessageData(
                    playerName = "Lox",
                    message = "Пенис",
                    fire = 7
                ),
                ChatMessageData(
                    playerName = "Lox",
                    message = "Член",
                    fire = 10
                ),
                ChatMessageData(
                    playerName = "Lox",
                    message = "Огурец",
                    fire = 0
                )
            ),
            paths = listOf(),
            onDismissRequest = {

            }
        )
    }
}