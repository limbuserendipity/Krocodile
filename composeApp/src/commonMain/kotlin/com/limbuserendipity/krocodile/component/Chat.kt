package com.limbuserendipity.krocodile.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.limbuserendipity.krocodile.model.ChatMessageData

@Composable
fun Chat(
    messages: List<ChatMessageData>
) {
    val state = rememberLazyListState()

    LazyColumn(
        state = state,
        reverseLayout = true,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxSize(0.4f)
    ) {
        items(messages.reversed()) { message ->
            val itemIndex = messages.indexOf(message)
            val visibility = calculateVisibility(state, itemIndex)
            ChatMessageItem(
                name = message.playerName,
                message = message.message,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .graphicsLayer { alpha = visibility }
            )
        }
    }

}

@Composable
fun ChatMessageItem(
    name: String,
    message: String,
    modifier: Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = modifier
    ) {
        Text(
            text = "$name: "
        )
        Text(
            text = message
        )
    }
}

@Composable
private fun calculateVisibility(
    listState: LazyListState,
    itemIndex: Int
): Float {
    val firstVisibleIndex = listState.firstVisibleItemIndex
    val offset = listState.firstVisibleItemScrollOffset

    val distance = itemIndex - firstVisibleIndex

    return maxOf(1f - distance * 0.2f, 0.1f)
}