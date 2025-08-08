package com.limbuserendipity.krocodile.route

import com.limbuserendipity.krocodile.GameServer
import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText

fun Application.gameRoutes() {
    routing {
        webSocket("/game") {
            try {
                for (frame in incoming) {
                    if (frame is Frame.Text){
                        GameServer.handleMessage(this, frame.readText())
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}