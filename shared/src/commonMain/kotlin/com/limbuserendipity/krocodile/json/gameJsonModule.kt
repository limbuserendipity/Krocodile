package com.limbuserendipity.krocodile.json

import com.limbuserendipity.krocodile.model.GameMessage
import com.limbuserendipity.krocodile.model.GameState
import com.limbuserendipity.krocodile.model.PlayerEvent
import com.limbuserendipity.krocodile.model.ServerResult
import com.limbuserendipity.krocodile.model.ServerStatus
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val gameJsonModule = SerializersModule {
    polymorphic(GameMessage::class){
        subclass(GameMessage.PlayerMessage::class)
        subclass(GameMessage.ServerMessage::class)
        subclass(GameMessage.Drawing::class)
        subclass(GameMessage.Guess::class)
    }

    polymorphic(ServerStatus::class){
        subclass(ServerStatus.Success::class)
        subclass(ServerStatus.Error::class)
    }

    polymorphic(ServerResult::class){
        subclass(ServerResult.LobbyState::class)
        subclass(ServerResult.RoomState::class)
        subclass(ServerResult.PlayerState::class)
        subclass(ServerResult.Words::class)
    }

    polymorphic(PlayerEvent::class) {
        subclass(PlayerEvent.NewPlayer::class)
        subclass(PlayerEvent.NewRoom::class)
        subclass(PlayerEvent.LeaveRoom::class)
        subclass(PlayerEvent.EnterToRoom::class)
        subclass(PlayerEvent.Word::class)
        subclass(PlayerEvent.ChatMessage::class)
    }

    polymorphic(GameState::class) {
        subclass(GameState.Wait::class)
        subclass(GameState.Run::class)
    }

}