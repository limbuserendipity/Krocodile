package com.limbuserendipity.krocodile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.limbuserendipity.krocodile.model.ChatMessageData
import com.limbuserendipity.krocodile.util.Space

@Composable
fun ChatSection(
    messages: List<ChatMessageData>,
    modifier: Modifier
) {

    LazyColumn(
        reverseLayout = true,
        modifier = modifier
    ) {
        items(messages.reversed()) { message ->
            ChatMessageItem(
                name = message.playerName,
                message = message.message
            )
            8.dp.Space()
        }
    }

}

@Composable
fun ChatMessageItem(
    name: String,
    message: String
) {

    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(8.dp)
    ) {

        Column {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .offset(y = (-4).dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = CircleShape
                        )
                        .padding(2.dp)

                ) {
                    Text(
                        text = name.first().uppercase(),
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                4.dp.Space()

                Text(
                    text = name,
                    color = MaterialTheme.colorScheme.scrim,
                    fontSize = 10.sp,
                )

            }

            Text(
                text = message,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp
            )

        }

    }

}