package com.limbuserendipity.krocodile.util

val drawingWords = listOf(
    "Elephant",
    "Guitar",
    "Pizza",
    "Butterfly",
    "Castle",
    "Dolphin",
    "Rainbow",
    "Camera",
    "Rocket",
    "Penguin",
    "Balloon",
    "Dragon",
    "Telescope",
    "Ice Cream",
    "Football",
    "Spaceship",
    "Giraffe",
    "Microphone",
    "Apple",
    "Bicycle",
    "Flower",
    "Robot",
    "Beach",
    "Mountain",
    "Coffee",
    "School",
    "Garden",
    "Winter",
    "Doctor",
    "Summer"
)

fun getRandomDrawingWords(count: Int = 6): List<String> {
    return drawingWords.shuffled().take(count)
}