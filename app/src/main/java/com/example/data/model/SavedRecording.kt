package com.example.data.model

data class SavedRecording(
    val id: String,
    val title: String,
    val speakerName: String,
    val speakerEmoji: String,
    val effectTitle: String,
    val textOrScript: String,
    val filePath: String,
    val durationMs: Long,
    val timestamp: Long,
    val isTtsGenerated: Boolean
)
