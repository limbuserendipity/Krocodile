package com.limbuserendipity.krocodile.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.Res
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.baseline_add_24
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.play_circle
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.settings
import com.limbuserendipity.krocodile.model.GameState
import com.limbuserendipity.krocodile.util.Space
import org.jetbrains.compose.resources.painterResource

@Composable
fun GameRoomHeader(
    roomName: String,
    playerCount: Int,
    maxPlayers: Int,
    gameStatus: GameState,
    onPlayers : () -> Unit,
    onShowSettings: () -> Unit,
    onStartGame: () -> Unit,
    canStart: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {

                }
                Column {
                    Text(
                        text = roomName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Row {
                        Box(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clickable{
                                    onPlayers()
                                }
                        ){
                            Text(
                                text = "$playerCount/$maxPlayers игроков",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    if (gameStatus == GameState.Run) Color(0xFFFF6B35) else MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = when(gameStatus){
                                    GameState.Run -> "Идет"
                                    GameState.Wait -> "Ожидание"
                                    GameState.Starting -> "Подготовка"
                                },
                                color = if (gameStatus == GameState.Run) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Row {
                IconButton(onClick = onShowSettings) {
                    Icon(
                        painter = painterResource(Res.drawable.settings),
                        contentDescription = "Settings"
                    )
                }
                if (gameStatus == GameState.Wait) {
                    Button(
                        onClick = onStartGame,
                        enabled = canStart,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.play_circle),
                            contentDescription = "Settings"
                        )
                        6.dp.Space()
                        Text("Начать игру", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}