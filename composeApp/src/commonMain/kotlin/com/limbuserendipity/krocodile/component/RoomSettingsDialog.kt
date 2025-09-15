package com.limbuserendipity.krocodile.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import com.limbuserendipity.krocodile.theme.SurfaceVariantDark

@Composable
fun RoomSettingsDialog(
    isCreate: Boolean,
    roomName : String = "",
    onDismissRequest: () -> Unit,
    onSettingsRoom: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {

    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        CreateRoomSurface(
            isCreate = isCreate,
            roomName = roomName,
            onSettingsRoom = onSettingsRoom,
            onDismiss = onDismissRequest,
            modifier = modifier
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoomSurface(
    isCreate: Boolean,
    roomName : String,
    onSettingsRoom: (String, Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {

    var title by remember {
        mutableStateOf(roomName)
    }

    var maxPlayers by remember {
        mutableStateOf(2)
    }

    var maxPlayersExpanded by remember {
        mutableStateOf(false)
    }

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
                    text = if(isCreate) "Создать комнату" else "Изменить настройки",
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

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
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
                    onExpandedChange = { maxPlayersExpanded = it }
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
                        onDismissRequest = {
                            maxPlayersExpanded = false
                        }
                    ) {
                        listOf(2, 4, 6, 8).forEach { count ->
                            DropdownMenuItem(
                                text = { Text("${count} игроков") },
                                onClick = {
                                    maxPlayers = count
                                    maxPlayersExpanded = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = { }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = "${60} секунд",
                        onValueChange = {},
                        label = { Text("Время раунда (секунды)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = false,
                        onDismissRequest = { }
                    ) {
                        listOf(30, 60, 90, 120).forEach { seconds ->
                            DropdownMenuItem(
                                text = { Text("$seconds секунд") },
                                onClick = {

                                }
                            )
                        }
                    }
                }
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
                    onClick = { onSettingsRoom(title, maxPlayers) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (isCreate) "Создать" else "Изменить",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun CloseIcon() {
    Canvas(modifier = Modifier.size(20.dp)) {
        drawLine(
            color = SurfaceVariantDark,
            start = Offset(4f, 4f),
            end = Offset(20f, 20f),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = SurfaceVariantDark,
            start = Offset(20f, 4f),
            end = Offset(4f, 20f),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )
    }
}