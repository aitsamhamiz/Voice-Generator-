package com.example.data.local

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.audio.AudioProcessor
import com.example.data.model.SavedRecording
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

class RecordingRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("mimic_recordings_prefs", Context.MODE_PRIVATE)
    private val recordingsDir = File(context.filesDir, "recordings").apply { mkdirs() }

    private val _recordings = MutableStateFlow<List<SavedRecording>>(emptyList())
    val recordings: StateFlow<List<SavedRecording>> = _recordings.asStateFlow()

    init {
        loadRecordings()
    }

    private fun loadRecordings() {
        val jsonStr = prefs.getString("recordings_list", "[]") ?: "[]"
        val list = mutableListOf<SavedRecording>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val filePath = obj.getString("filePath")
                val file = File(filePath)
                if (file.exists()) {
                    list.add(
                        SavedRecording(
                            id = obj.getString("id"),
                            title = obj.getString("title"),
                            speakerName = obj.getString("speakerName"),
                            speakerEmoji = obj.optString("speakerEmoji", "🎙️"),
                            effectTitle = obj.optString("effectTitle", "Original"),
                            textOrScript = obj.optString("textOrScript", ""),
                            filePath = filePath,
                            durationMs = obj.optLong("durationMs", 0L),
                            timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                            isTtsGenerated = obj.optBoolean("isTtsGenerated", false)
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        _recordings.value = list.sortedByDescending { it.timestamp }
    }

    fun saveRecording(
        title: String,
        speakerName: String,
        speakerEmoji: String,
        effectTitle: String,
        textOrScript: String,
        pcmData: ShortArray,
        isTtsGenerated: Boolean
    ): SavedRecording {
        val id = UUID.randomUUID().toString()
        val fileName = "rec_${System.currentTimeMillis()}_$id.wav"
        val file = File(recordingsDir, fileName)

        AudioProcessor.saveWavFile(pcmData, file, AudioProcessor.SAMPLE_RATE)

        val durationMs = (pcmData.size.toDouble() / AudioProcessor.SAMPLE_RATE * 1000).toLong()

        val newRecording = SavedRecording(
            id = id,
            title = title.ifBlank { "Clip #${_recordings.value.size + 1}" },
            speakerName = speakerName,
            speakerEmoji = speakerEmoji,
            effectTitle = effectTitle,
            textOrScript = textOrScript,
            filePath = file.absolutePath,
            durationMs = durationMs,
            timestamp = System.currentTimeMillis(),
            isTtsGenerated = isTtsGenerated
        )

        val updated = listOf(newRecording) + _recordings.value
        _recordings.value = updated
        persistList(updated)
        return newRecording
    }

    fun deleteRecording(recordingId: String) {
        val current = _recordings.value
        val target = current.firstOrNull { it.id == recordingId }
        target?.let {
            val file = File(it.filePath)
            if (file.exists()) file.delete()
        }
        val updated = current.filterNot { it.id == recordingId }
        _recordings.value = updated
        persistList(updated)
    }

    private fun persistList(list: List<SavedRecording>) {
        val jsonArray = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("title", item.title)
                put("speakerName", item.speakerName)
                put("speakerEmoji", item.speakerEmoji)
                put("effectTitle", item.effectTitle)
                put("textOrScript", item.textOrScript)
                put("filePath", item.filePath)
                put("durationMs", item.durationMs)
                put("timestamp", item.timestamp)
                put("isTtsGenerated", item.isTtsGenerated)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString("recordings_list", jsonArray.toString()).apply()
    }

    fun createShareIntent(recording: SavedRecording): Intent? {
        val file = File(recording.filePath)
        if (!file.exists()) return null

        val authority = "${context.packageName}.provider"
        val contentUri: Uri = FileProvider.getUriForFile(context, authority, file)

        return Intent(Intent.ACTION_SEND).apply {
            type = "audio/wav"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_SUBJECT, recording.title)
            putExtra(Intent.EXTRA_TEXT, "Listen to this mimicry clip: ${recording.speakerName} - ${recording.title}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}
