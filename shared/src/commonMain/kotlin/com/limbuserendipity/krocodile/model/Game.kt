package com.limbuserendipity.krocodile.model

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@Polymorphic
sealed class GameMessage {

    @Serializable
    @SerialName("player_message")
    data class PlayerMessage(val playerEvent: PlayerEvent) : GameMessage()

    @Serializable
    @SerialName("server_message")
    data class ServerMessage(val serverStatus: ServerStatus) : GameMessage()

}

@Serializable
@Polymorphic
sealed class GameState {
    @Serializable
    @SerialName("wait")
    object Wait : GameState()

    @Serializable
    @SerialName("starting")
    data class Starting(
        val time: Int
    ) : GameState()

    @Serializable
    @SerialName("run")
    data class Run(
        val time: Int
    ) : GameState()

    @Serializable
    @Polymorphic
    @SerialName("end_type")
    data class End(
        val endVariant: EndVariant
    ) : GameState()
}

@Serializable
@Polymorphic
@SerialName("end_variant")
sealed class EndVariant {

    @Serializable
    @SerialName("guessed_word")
    data class GuessedWord(
        val winnerName: String,
        val guessedWord: String,
        val time: Int
    ) : EndVariant()

    @Serializable
    @SerialName("failed_word")
    data class FailedWord(
        val guessedWord: String,
        val time: Int
    ) : EndVariant()

    @Serializable
    @SerialName("game_end")
    data class GameEnd(
        val winnerName: String
    ) : EndVariant()

}