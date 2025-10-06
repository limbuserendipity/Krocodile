package com.limbuserendipity.krocodile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.limbuserendipity.krocodile.theme.CanvasSurface
import com.limbuserendipity.krocodile.theme.KrocodileTheme
import com.limbuserendipity.krocodile.vm.PathInfo
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun WinnerGuessedDialog(
    guessedWord: String,
    winnerName: String,
    time: Float,
    paths: List<PathInfo>,
    onDismissRequest: () -> Unit
) {

    KrocodileDialog(
        time = time,
        onDismissRequest = onDismissRequest,
        title = "🎉 Поздравляем!"
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .padding(bottom = 8.dp)
        ) {
            Text(
                text = winnerName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Первым угадал слово!",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

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

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Загаданное слово:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = guessedWord,
                    fontSize = 20.sp,
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
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                ScaledCanvasPreview(
                    paths = paths,
                    modifier = Modifier
                        .border(2.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                        .background(CanvasSurface)
                        .size(128.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewWinnerGuessedDialog() {
    KrocodileTheme {

        WinnerGuessedDialog(
            guessedWord = "Улитка",
            winnerName = "Player1",
            time = 5f,
            paths = listOf(),
            onDismissRequest = {

            }
        )

    }
}