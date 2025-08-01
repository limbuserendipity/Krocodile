package com.limbuserendipity.krocodile

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform