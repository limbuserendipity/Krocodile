import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.Res
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.crown_24_filled
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.emergency_exit_solid
import com.limbuserendipity.krocodile.model.PlayerData
import org.jetbrains.compose.resources.painterResource

@Composable
fun PlayersListDialog(
    players: List<PlayerData>,
    currentUserId: String,
    ownerId: String,
    onTransferHost: (String) -> Unit,
    onKickPlayer: (String) -> Unit,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
            ) {
                Text(
                    text = "Игроки",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(players) { player ->
                        PlayerItem(
                            player = player,
                            isHost = player.id == ownerId,
                            isCurrentUserHost = currentUserId == ownerId,
                            isCurrentUser = player.id == currentUserId,
                            onTransferHost = onTransferHost,
                            onKickPlayer = onKickPlayer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerItem(
    player: PlayerData,
    isHost: Boolean,
    isCurrentUserHost: Boolean,
    isCurrentUser: Boolean,
    onTransferHost: (String) -> Unit,
    onKickPlayer: (String) -> Unit
) {
    val isDrawing = player.isArtist

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isDrawing) Color(0xFF3DAE8B).copy(alpha = 0.1f)
                else Color(0xFFF2FFFF)
            )
            .border(
                width = if (isDrawing) 1.dp else 0.dp,
                color = if (isDrawing) Color(0xFF3DAE8B) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (isDrawing) Color(0xFF3DAE8B)
                            else Color(0xFFA3D8BD)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = player.name.firstOrNull()?.toString() ?: "",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDrawing) Color.White else Color(0xFF531000)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = player.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1A1A1A)
                        )

                        if (isHost) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF3DAE8B))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Хост",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    if (isDrawing) {
                        Text(
                            text = "Рисует",
                            fontSize = 12.sp,
                            color = Color(0xFF3DAE8B)
                        )
                    }
                }

                if (isCurrentUserHost && !isCurrentUser) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        IconButton(
                            onClick = { onTransferHost(player.id) },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = Color(0xFF3DAE8B)
                            )
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.crown_24_filled),
                                contentDescription = "Передать хост",
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(
                            onClick = { onKickPlayer(player.id) },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = Color(0xFFFF5252)
                            )
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.emergency_exit_solid),
                                contentDescription = "Выгнать",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

            }

            Text(
                text = player.score.toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
        }
    }
}