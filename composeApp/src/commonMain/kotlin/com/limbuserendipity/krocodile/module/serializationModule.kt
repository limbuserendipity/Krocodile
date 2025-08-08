package com.limbuserendipity.krocodile.module

import com.limbuserendipity.krocodile.json.gameJsonModule
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val serializationModule = module {
    single {
        Json {
            serializersModule = gameJsonModule
            classDiscriminator = "type"
        }
    }
}