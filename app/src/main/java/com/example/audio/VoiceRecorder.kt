package com.example.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.abs
import kotlin.math.sqrt

class VoiceRecorder {

    private var audioRecord: AudioRecord? = null
    private var recordJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _amplitude = MutableStateFlow(0f)
    val amplitude: StateFlow<Float> = _amplitude.asStateFlow()

    private val _recordingDurationMs = MutableStateFlow(0L)
    val recordingDurationMs: StateFlow<Long> = _recordingDurationMs.asStateFlow()

    private val pcmOutputStream = ByteArrayOutputStream()

    @SuppressLint("MissingPermission")
    fun startRecording(): Boolean {
        if (_isRecording.value) return true

        val sampleRate = AudioProcessor.SAMPLE_RATE
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT

        val minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        val bufferSize = (minBufSize * 2).coerceAtLeast(4096)

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                audioRecord?.release()
                audioRecord = null
                return false
            }

            pcmOutputStream.reset()
            audioRecord?.startRecording()
            _isRecording.value = true
            _recordingDurationMs.value = 0L

            recordJob = scope.launch {
                val readBuffer = ShortArray(1024)
                val byteBuffer = ByteBuffer.allocate(readBuffer.size * 2).order(ByteOrder.LITTLE_ENDIAN)
                val startTime = System.currentTimeMillis()

                while (isActive && _isRecording.value) {
                    val readShorts = audioRecord?.read(readBuffer, 0, readBuffer.size) ?: -1
                    if (readShorts > 0) {
                        byteBuffer.clear()
                        var sumSquares = 0.0
                        for (i in 0 until readShorts) {
                            val sample = readBuffer[i]
                            byteBuffer.putShort(sample)
                            sumSquares += sample.toDouble() * sample.toDouble()
                        }
                        synchronized(pcmOutputStream) {
                            pcmOutputStream.write(byteBuffer.array(), 0, readShorts * 2)
                        }

                        // Calculate RMS for visualizer
                        val rms = sqrt(sumSquares / readShorts) / 32768.0
                        val normalizedAmp = (rms * 3.5).toFloat().coerceIn(0.05f, 1.0f)
                        _amplitude.value = normalizedAmp
                        _recordingDurationMs.value = System.currentTimeMillis() - startTime
                    }
                }
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            stopRecording()
            return false
        }
    }

    fun stopRecording(): ShortArray {
        _isRecording.value = false
        recordJob?.cancel()
        recordJob = null

        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        audioRecord = null
        _amplitude.value = 0f

        val recordedBytes: ByteArray
        synchronized(pcmOutputStream) {
            recordedBytes = pcmOutputStream.toByteArray()
            pcmOutputStream.reset()
        }

        if (recordedBytes.isEmpty()) return ShortArray(0)

        val shortCount = recordedBytes.size / 2
        val shorts = ShortArray(shortCount)
        val bb = ByteBuffer.wrap(recordedBytes).order(ByteOrder.LITTLE_ENDIAN)
        for (i in 0 until shortCount) {
            shorts[i] = bb.short
        }
        return shorts
    }
}
