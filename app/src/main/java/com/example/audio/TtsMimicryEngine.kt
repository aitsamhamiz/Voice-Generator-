package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.data.model.AudioEffectType
import com.example.data.model.Celebrity
import com.example.data.model.EmotionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale
import java.util.UUID

class TtsMimicryEngine(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private val _isTtsReady = MutableStateFlow(false)
    val isTtsReady: StateFlow<Boolean> = _isTtsReady.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _playbackAmplitude = MutableStateFlow(0f)
    val playbackAmplitude: StateFlow<Float> = _playbackAmplitude.asStateFlow()

    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            configureLocale()
            _isTtsReady.value = true
        }
    }

    private fun configureLocale() {
        val ttsInstance = tts ?: return
        // Try Urdu, then Hindi, then US English as standard fallback
        val urduLocale = Locale("ur", "PK")
        val hindiLocale = Locale("hi", "IN")
        val usLocale = Locale.US

        val urduResult = ttsInstance.isLanguageAvailable(urduLocale)
        if (urduResult == TextToSpeech.LANG_AVAILABLE || urduResult == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
            ttsInstance.language = urduLocale
            return
        }

        val hindiResult = ttsInstance.isLanguageAvailable(hindiLocale)
        if (hindiResult == TextToSpeech.LANG_AVAILABLE || hindiResult == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
            ttsInstance.language = hindiLocale
            return
        }

        ttsInstance.language = usLocale
    }

    /**
     * Synthesizes text with character voice attributes, applies AudioProcessor DSP effects
     * (Echo, Reverb, Megaphone, Bass, Pitch Shift), and returns the generated PCM data & temp file.
     */
    suspend fun synthesizeAndProcess(
        text: String,
        celebrity: Celebrity,
        emotion: EmotionType,
        effect: AudioEffectType,
        customPitchOffset: Float = 0f,
        customSpeedOffset: Float = 0f,
        backgroundAmbianceVolume: Float = 0.25f,
        onPcmReady: (ShortArray, File) -> Unit
    ): Boolean = withContext(Dispatchers.IO) {
        val ttsInstance = tts ?: return@withContext false
        if (!_isTtsReady.value) return@withContext false

        _isProcessing.value = true

        try {
            // Calculate effective TTS pitch and speech rate
            val finalPitch = ((celebrity.basePitch * emotion.pitchMultiplier) + customPitchOffset)
                .coerceIn(0.5f, 2.0f)
            val finalSpeed = ((celebrity.baseSpeed * emotion.speedMultiplier) + customSpeedOffset)
                .coerceIn(0.5f, 2.0f)

            ttsInstance.setPitch(finalPitch)
            ttsInstance.setSpeechRate(finalSpeed)

            // Prepare temporary audio output file
            val tempFile = File(context.cacheDir, "tts_synth_${UUID.randomUUID()}.wav")
            val utteranceId = "synth_${System.currentTimeMillis()}"

            val params = Bundle().apply {
                putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
            }

            val synthDeferred = kotlinx.coroutines.CompletableDeferred<Boolean>()

            ttsInstance.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(id: String?) {}
                override fun onDone(id: String?) {
                    if (id == utteranceId) {
                        synthDeferred.complete(true)
                    }
                }
                override fun onError(id: String?) {
                    if (id == utteranceId) {
                        synthDeferred.complete(false)
                    }
                }
            })

            val result = ttsInstance.synthesizeToFile(text, params, tempFile, utteranceId)
            if (result != TextToSpeech.SUCCESS) {
                _isProcessing.value = false
                return@withContext false
            }

            // Wait for file to complete synthesis
            val success = synthDeferred.await()
            if (!success || !tempFile.exists() || tempFile.length() < 100) {
                _isProcessing.value = false
                return@withContext false
            }

            // Read generated PCM
            val rawPcm = AudioProcessor.readWavFile(tempFile)
            tempFile.delete()

            // Setup DSP Processing parameters
            val ambianceType = when (emotion) {
                EmotionType.RALLY_FIERCE -> AudioProcessor.AmbianceType.JALSA_CROWD
                EmotionType.DRAMATIC_EMOTIONAL, EmotionType.ROMANTIC_HUSKY -> AudioProcessor.AmbianceType.CINEMA_STRINGS
                EmotionType.PRESS_CONFERENCE -> AudioProcessor.AmbianceType.PRESS_SHUTTERS
                else -> AudioProcessor.AmbianceType.NONE
            }

            val dspParams = AudioProcessor.ProcessingParams(
                pitchShiftSemitones = celebrity.pitchShiftSemitones + effect.pitchShiftSemitones,
                tempoMultiplier = effect.tempoFactor,
                delayMs = effect.delayMs,
                delayFeedback = effect.delayFeedback,
                reverbAmount = effect.reverbAmount,
                megaphoneIntensity = effect.megaphoneIntensity,
                tremoloDepth = effect.tremoloDepth,
                bassBoost = effect.bassBoost,
                ringModFreq = effect.ringModFreq,
                backgroundAmbianceVolume = backgroundAmbianceVolume,
                backgroundAmbianceType = ambianceType
            )

            // Process through AudioProcessor DSP
            val processedPcm = AudioProcessor.processPcm(rawPcm, dspParams, AudioProcessor.SAMPLE_RATE)

            // Save final processed WAV file
            val processedWavFile = File(context.cacheDir, "processed_${UUID.randomUUID()}.wav")
            AudioProcessor.saveWavFile(processedPcm, processedWavFile, AudioProcessor.SAMPLE_RATE)

            _isProcessing.value = false
            onPcmReady(processedPcm, processedWavFile)
            return@withContext true
        } catch (e: Exception) {
            e.printStackTrace()
            _isProcessing.value = false
            return@withContext false
        }
    }

    /**
     * Plays raw 16-bit PCM samples through AudioTrack with amplitude tracking for visualizer.
     */
    fun playPcm(pcm: ShortArray, onComplete: () -> Unit = {}) {
        stopPlayback()
        if (pcm.isEmpty()) return

        val sampleRate = AudioProcessor.SAMPLE_RATE
        val minBuf = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(minBuf.coerceAtLeast(4096))
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.play()
        _isPlaying.value = true

        playbackJob = scope.launch {
            val chunkSize = 2048
            var offset = 0

            while (isActive && offset < pcm.size && _isPlaying.value) {
                val length = (pcm.size - offset).coerceAtMost(chunkSize)
                audioTrack?.write(pcm, offset, length)

                // Calculate amplitude for visualizer
                var sumSq = 0.0
                for (i in offset until (offset + length)) {
                    val sample = pcm[i].toDouble()
                    sumSq += sample * sample
                }
                val rms = kotlin.math.sqrt(sumSq / length) / 32768.0
                _playbackAmplitude.value = (rms * 3.0).toFloat().coerceIn(0.05f, 1.0f)

                offset += length
            }

            _playbackAmplitude.value = 0f
            _isPlaying.value = false
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun stopPlayback() {
        _isPlaying.value = false
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        audioTrack = null
        _playbackAmplitude.value = 0f
    }

    fun release() {
        stopPlayback()
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
