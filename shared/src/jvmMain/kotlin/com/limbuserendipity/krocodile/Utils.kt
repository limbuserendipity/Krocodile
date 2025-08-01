package com.limbuserendipity.krocodile

import java.security.SecureRandom


fun generateUniqueRandom(): Long {
    return (System.nanoTime() shl 16) or
            (SecureRandom().nextInt(65536).toLong())
}