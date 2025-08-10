package com.limbuserendipity.krocodile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.limbuserendipity.krocodile.model.RoomData

@Composable
fun RoomList(
    rooms: List<RoomData>
){

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        items(rooms){ room ->
            RoomItem(
                title = room.title,
                playersCount = room.playerCount,
                maxCount = room.maxCount
            )
        }
    }

}