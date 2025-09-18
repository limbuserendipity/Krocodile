package com.limbuserendipity.krocodile.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


@Serializable
@SerialName("notification_message")
data class NotificationMessage @OptIn(ExperimentalTime::class) constructor(
    val message: String,
    val type: NotificationType,
)

enum class NotificationType {
    INFO, WARNING, ERROR, SUCCESS
}