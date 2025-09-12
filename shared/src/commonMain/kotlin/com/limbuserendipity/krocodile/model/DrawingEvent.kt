package com.limbuserendipity.krocodile.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("drawing_event")
sealed class DrawingEvent{

    @Serializable
    @SerialName("tool_select")
    data class ToolSelect(
        val toolType: ToolType
    ) : DrawingEvent()

    @Serializable
    @SerialName("draw_path")
    data class DrawPath(
        val pathData: PathData
    ) : DrawingEvent()

}

@Serializable
@SerialName("tool_type")
sealed class ToolType {
    @Serializable
    @SerialName("eraser")
    object Eraser : ToolType()
    @Serializable
    @SerialName("undo")
    data class Undo(
        var count : Int = 0
    ): ToolType()
    @Serializable
    @SerialName("clear")
    object Clear : ToolType()
}