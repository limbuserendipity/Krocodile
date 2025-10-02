package com.limbuserendipity.krocodile.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.limbuserendipity.krocodile.model.GameDifficulty
import com.limbuserendipity.krocodile.model.GameRoomSettings

@Composable
fun RoomSettingsDialog(
    isCreate: Boolean,
    roomName: String = "",
    roomSettings: GameRoomSettings,
    onDismissRequest: () -> Unit,
    onSettingsRoom: (String, Int, Int, Int, GameDifficulty) -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        RoomSettingsSurface(
            isCreate = isCreate,
            roomName = roomName,
            roomSettings = roomSettings,
            onSettingsRoom = onSettingsRoom,
            onDismiss = onDismissRequest,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomSettingsSurface(
    isCreate: Boolean,
    roomName: String,
    roomSettings: GameRoomSettings,
    onSettingsRoom: (String, Int, Int, Int, GameDifficulty) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }

    var title by remember { mutableStateOf(roomName) }
    var maxPlayers by remember { mutableStateOf(roomSettings.maxPlayers) }
    var maxPlayersExpanded by remember { mutableStateOf(false) }

    var roundCount by remember { mutableStateOf(roomSettings.maxRounds) }
    var roundTime by remember { mutableStateOf(roomSettings.maxTime) }
    var gameDifficulty by remember { mutableStateOf(roomSettings.difficulty) }

    var roundCountExpanded by remember { mutableStateOf(false) }
    var roundTimeExpanded by remember { mutableStateOf(false) }
    var difficultyExpanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 12.dp,
        modifier = Modifier
            .widthIn(max = 480.dp)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .then(modifier)
        ) {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isCreate) "Создать комнату" else "Настройки комнаты",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(8.dp)
                ) {
                    CloseIcon()
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedTab])
                            .fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                divider = { }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = "Комната",
                        color = if (selectedTab == 0) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (selectedTab == 0) FontWeight.Medium else FontWeight.Normal
                    )
                }
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = "Игра",
                        color = if (selectedTab == 1) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (selectedTab == 1) FontWeight.Medium else FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> RoomSettingsTab(
                    title = title,
                    onTitleChange = { title = it },
                    maxPlayers = maxPlayers,
                    onMaxPlayersChange = { maxPlayers = it },
                    maxPlayersExpanded = maxPlayersExpanded,
                    onMaxPlayersExpandedChange = { maxPlayersExpanded = it }
                )

                1 -> GameSettingsTab(
                    roundCount = roundCount,
                    onRoundCountChange = { roundCount = it },
                    roundTime = roundTime,
                    onRoundTimeChange = { roundTime = it },
                    gameDifficulty = gameDifficulty,
                    onGameDifficultyChange = { gameDifficulty = it },
                    roundCountExpanded = roundCountExpanded,
                    onRoundCountExpandedChange = { roundCountExpanded = it },
                    roundTimeExpanded = roundTimeExpanded,
                    onRoundTimeExpandedChange = { roundTimeExpanded = it },
                    difficultyExpanded = difficultyExpanded,
                    onDifficultyExpandedChange = { difficultyExpanded = it }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Отмена", fontSize = 14.sp)
                }

                Button(
                    onClick = {
                        onSettingsRoom(title, maxPlayers, roundCount, roundTime, gameDifficulty)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (isCreate) "Создать" else "Сохранить",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomSettingsTab(
    title: String,
    onTitleChange: (String) -> Unit,
    maxPlayers: Int,
    onMaxPlayersChange: (Int) -> Unit,
    maxPlayersExpanded: Boolean,
    onMaxPlayersExpandedChange: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Название комнаты") },
            placeholder = { Text("Введите название") },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth()
        )

        ExposedDropdownMenuBox(
            expanded = maxPlayersExpanded,
            onExpandedChange = onMaxPlayersExpandedChange
        ) {
            OutlinedTextField(
                readOnly = true,
                value = "${maxPlayers} игроков",
                onValueChange = {},
                label = { Text("Макс. количество игроков") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = maxPlayersExpanded) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
            )

            ExposedDropdownMenu(
                expanded = maxPlayersExpanded,
                onDismissRequest = { onMaxPlayersExpandedChange(false) }
            ) {
                listOf(2, 4, 6, 8).forEach { count ->
                    DropdownMenuItem(
                        text = { Text("${count} игроков") },
                        onClick = {
                            onMaxPlayersChange(count)
                            onMaxPlayersExpandedChange(false)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameSettingsTab(
    roundCount: Int,
    onRoundCountChange: (Int) -> Unit,
    roundTime: Int,
    onRoundTimeChange: (Int) -> Unit,
    gameDifficulty: GameDifficulty,
    onGameDifficultyChange: (GameDifficulty) -> Unit,
    roundCountExpanded: Boolean,
    onRoundCountExpandedChange: (Boolean) -> Unit,
    roundTimeExpanded: Boolean,
    onRoundTimeExpandedChange: (Boolean) -> Unit,
    difficultyExpanded: Boolean,
    onDifficultyExpandedChange: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ExposedDropdownMenuBox(
            expanded = roundCountExpanded,
            onExpandedChange = onRoundCountExpandedChange
        ) {
            OutlinedTextField(
                readOnly = true,
                value = "${roundCount} раундов",
                onValueChange = {},
                label = { Text("Количество раундов") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roundCountExpanded) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
            )

            ExposedDropdownMenu(
                expanded = roundCountExpanded,
                onDismissRequest = { onRoundCountExpandedChange(false) }
            ) {
                listOf(3, 5, 7, 10).forEach { count ->
                    DropdownMenuItem(
                        text = { Text("${count} раундов") },
                        onClick = {
                            onRoundCountChange(count)
                            onRoundCountExpandedChange(false)
                        }
                    )
                }
            }
        }

        ExposedDropdownMenuBox(
            expanded = roundTimeExpanded,
            onExpandedChange = onRoundTimeExpandedChange
        ) {
            OutlinedTextField(
                readOnly = true,
                value = "${roundTime} секунд",
                onValueChange = {},
                label = { Text("Время на раунд (секунды)") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roundTimeExpanded) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
            )

            ExposedDropdownMenu(
                expanded = roundTimeExpanded,
                onDismissRequest = { onRoundTimeExpandedChange(false) }
            ) {
                listOf(30, 60, 90, 120).forEach { seconds ->
                    DropdownMenuItem(
                        text = { Text("$seconds секунд") },
                        onClick = {
                            onRoundTimeChange(seconds)
                            onRoundTimeExpandedChange(false)
                        }
                    )
                }
            }
        }

        ExposedDropdownMenuBox(
            expanded = difficultyExpanded,
            onExpandedChange = onDifficultyExpandedChange
        ) {
            OutlinedTextField(
                readOnly = true,
                value = gameDifficulty.displayName,
                onValueChange = {},
                label = { Text("Сложность игры") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = difficultyExpanded) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
            )

            ExposedDropdownMenu(
                expanded = difficultyExpanded,
                onDismissRequest = { onDifficultyExpandedChange(false) }
            ) {
                GameDifficulty.values().forEach { difficulty ->
                    DropdownMenuItem(
                        text = { Text(difficulty.displayName) },
                        onClick = {
                            onGameDifficultyChange(difficulty)
                            onDifficultyExpandedChange(false)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CloseIcon() {
    val color = MaterialTheme.colorScheme.onSurfaceVariant
    Canvas(modifier = Modifier.size(20.dp)) {
        drawLine(
            color = color,
            start = Offset(4f, 4f),
            end = Offset(20f, 20f),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(20f, 4f),
            end = Offset(4f, 20f),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )
    }
}