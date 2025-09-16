package com.limbuserendipity.krocodile.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.Res
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.chat
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.chat_circle_close
import com.limbuserendipity.krocodile.model.ChatMessageData
import org.jetbrains.compose.resources.painterResource

@Composable
fun ChatBubble(
    messageData: ChatMessageData,
    isSentByUser: Boolean = false,
    onFireMessage: (ChatMessageData) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSentByUser) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }

    val textColor = if (isSentByUser) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSecondaryContainer
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalAlignment = if (isSentByUser) Alignment.End else Alignment.Start
    ) {
        Text(
            text = messageData.playerName,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = backgroundColor,
            shadowElevation = 1.dp,
            onClick = {
                onFireMessage(messageData)
            }
        ) {
            Row {
                Text(
                    text = messageData.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor,
                    modifier = Modifier.padding(12.dp)
                )
                if (messageData.fire > 0) {
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
}

@Composable
fun ChatPreviewBubble(
    messages: List<ChatMessageData>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(200.dp)
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(8.dp)
    ) {
        messages.take(3).forEach { message ->
            Text(
                text = "${message.playerName}: ${message.message}",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
    }
}

@Composable
fun ExpandableChatFab(
    messages: List<ChatMessageData>,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onFireMessage: (ChatMessageData) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.End,
        modifier = modifier.padding(24.dp)
    ) {
        AnimatedVisibility(visible = isExpanded) {
            Column {
                LazyColumn(
                    reverseLayout = true,
                    modifier = Modifier
                        .heightIn(max = 300.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = { onToggle() }
                            )
                        }
                ) {
                    items(messages.take(10).reversed()) { message ->
                        ChatBubble(
                            messageData = message,
                            isSentByUser = message.playerName == "You",
                            onFireMessage = onFireMessage,
                            modifier = Modifier
                                .align(Alignment.End)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnimatedVisibility(visible = !isExpanded && messages.isNotEmpty()) {
                ChatPreviewBubble(messages = messages.takeLast(3))
            }

            FloatingActionButton(
                onClick = onToggle,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                val url = if (isExpanded) Res.drawable.chat_circle_close else Res.drawable.chat
                Icon(
                    painter = painterResource(url),
                    contentDescription = if (isExpanded) "Закрыть чат" else "Открыть чат"
                )
            }
        }
    }
}