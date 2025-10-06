package com.limbuserendipity.krocodile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.Res
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.trash_fill
import com.limbuserendipity.krocodile.model.PlayerData
import com.limbuserendipity.krocodile.theme.KrocodileTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun GameEndDialog(
    winner: PlayerData,
    players: List<PlayerData>,
    onDismissRequest: () -> Unit
) {
    KrocodileDialog(
        onDismissRequest = onDismissRequest,
        time = 0f,
        title = "🏆 Игра завершена!"
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = winner.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Победитель с ${winner.score} очками!",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(players) { player ->

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (player == winner) {
                                Brush.linearGradient(
                                    listOf(MaterialTheme.colorScheme.primaryContainer, Color(0xFFFFE8DC))
                                ).let { brush ->
                                    MaterialTheme.colorScheme.primaryContainer
                                }
                            } else {
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.05f)
                            },
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            2.dp,
                            if (player == winner) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                        .then(if (player == winner) Modifier.scale(1.02f) else Modifier)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (player == winner) {
                            Icon(
                                painter = painterResource(Res.drawable.trash_fill),
                                contentDescription = "Победитель",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = player.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = player.score.toString(),
                        fontSize = if (player == winner) 18.sp else 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (player == winner) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewGameEndDialog() {

    KrocodileTheme {

        val winner = PlayerData(
            id = "1",
            name = "Alayah Bryant",
            score = 13,
            isArtist = false
        )

        GameEndDialog(
            onDismissRequest = {},
            winner = winner,
            players = listOf(
                winner,
                PlayerData(
                    id = "2",
                    name = "Jordan Acevedo",
                    score = 12,
                    isArtist = false
                ),
                PlayerData(
                    id = "3",
                    name = "Naomi Underwood",
                    score = 10,
                    isArtist = false
                ),
                PlayerData(
                    id = "4",
                    name = "Madeline Castillo",
                    score = 6,
                    isArtist = false
                ),
                PlayerData(
                    id = "5",
                    name = "Harper Yoder",
                    score = 4,
                    isArtist = false
                ),
                PlayerData(
                    id = "6",
                    name = "Belen O’Donnell",
                    score = 0,
                    isArtist = false
                )
            )
        )

    }

}