package com.limbuserendipity.krocodile

import java.security.SecureRandom
import java.util.UUID


fun generateUniqueRandom(): Long {
    return (System.nanoTime() shl 16) or
            (SecureRandom().nextInt(65536).toLong())
}

fun generatePlayerId(): String {
    return UUID.randomUUID().toString()
}