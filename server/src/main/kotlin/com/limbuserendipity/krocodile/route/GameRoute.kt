package com.limbuserendipity.krocodile.route

import com.limbuserendipity.krocodile.GameServer
import com.limbuserendipity.krocodile.generatePlayerId
import com.limbuserendipity.krocodile.generateUniqueRandom
import com.limbuserendipity.krocodile.model.GameMessage
import com.limbuserendipity.krocodile.model.PlayerEvent
import com.limbuserendipity.krocodile.model.Room
import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.serialization.json.Json

fun Application.gameRoutes() {
    routing {
        webSocket("/game") {
            GameServer.add(generatePlayerId(), this)
            try {
                for (frame in incoming) {
                    if (frame is Frame.Text){
                        val message = Json
                            .decodeFromString<GameMessage>(frame.readText())

                        GameServer.handleMessage(this, message)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}