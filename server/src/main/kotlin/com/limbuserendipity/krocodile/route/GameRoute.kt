package com.limbuserendipity.krocodile.route

import GameServer
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

fun Application.gameRoutes() {
    routing {
        webSocket("/game") {
            try {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        GameServer.handleMessage(this, frame.readText())
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}