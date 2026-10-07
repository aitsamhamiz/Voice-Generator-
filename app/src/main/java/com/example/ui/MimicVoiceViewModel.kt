package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioProcessor
import com.example.audio.TtsMimicryEngine
import com.example.audio.VoiceRecorder
import com.example.data.generator.ScriptPersonaGenerator
import com.example.data.local.RecordingRepository
import com.example.data.model.AudioEffectType
import com.example.data.model.Celebrity
import com.example.data.model.CelebrityCatalog
import com.example.data.model.CelebrityCategory
import com.example.data.model.EmotionType
import com.example.data.model.SavedRecording
import com.example.data.model.SignaturePhrase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MimicVoiceViewModel(application: Application) : AndroidViewModel(application) {

    private val ttsEngine = TtsMimicryEngine(application)
    private val voiceRecorder = VoiceRecorder()
    private val recordingRepo = RecordingRepository(application)

    // Celebrity & Persona State
    private val _selectedCelebrity = MutableStateFlow<Celebrity>(CelebrityCatalog.celebrities.first())
    val selectedCelebrity: StateFlow<Celebrity> = _selectedCelebrity.asStateFlow()

    private val _categoryFilter = MutableStateFlow<CelebrityCategory?>(null)
    val categoryFilter: StateFlow<CelebrityCategory?> = _categoryFilter.asStateFlow()

    private val _selectedEmotion = MutableStateFlow<EmotionType>(EmotionType.RALLY_FIERCE)
    val selectedEmotion: StateFlow<EmotionType> = _selectedEmotion.asStateFlow()

    private val _selectedEffect = MutableStateFlow<AudioEffectType>(AudioEffectType.JALSA_ECHO)
    val selectedEffect: StateFlow<AudioEffectType> = _selectedEffect.asStateFlow()

    // Real-time Pitch & Speed Tuning Sliders
    private val _pitchOffsetSemitones = MutableStateFlow(0)
    val pitchOffsetSemitones: StateFlow<Int> = _pitchOffsetSemitones.asStateFlow()

    private val _speedMultiplier = MutableStateFlow(1.0f)
    val speedMultiplier: StateFlow<Float> = _speedMultiplier.asStateFlow()

    private val _ambianceVolume = MutableStateFlow(0.25f)
    val ambianceVolume: StateFlow<Float> = _ambianceVolume.asStateFlow()

    // Text & Script State
    private val _inputText = MutableStateFlow(
        "Aap ne sab se pehle ghabrana bilkul nahi hai! Main inko rulaunga!"
    )
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _generatedParodyScript = MutableStateFlow("")
    val generatedParodyScript: StateFlow<String> = _generatedParodyScript.asStateFlow()

    // Audio Buffer in Memory (Last generated or recorded)
    private val _currentPcmBuffer = MutableStateFlow<ShortArray?>(null)
    val currentPcmBuffer: StateFlow<ShortArray?> = _currentPcmBuffer.asStateFlow()

    private val _rawMicPcm = MutableStateFlow<ShortArray?>(null)
    val rawMicPcm: StateFlow<ShortArray?> = _rawMicPcm.asStateFlow()

    // Status & Error
    private val _statusMessage = MutableStateFlow("Ready to mimic celebrity voices")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    private val _previewPlayingCelebrityId = MutableStateFlow<String?>(null)
    val previewPlayingCelebrityId: StateFlow<String?> = _previewPlayingCelebrityId.asStateFlow()

    // Exposed Flows from Subsystems
    val isTtsReady: StateFlow<Boolean> = ttsEngine.isTtsReady
    val isProcessing: StateFlow<Boolean> = ttsEngine.isProcessing
    val isPlaying: StateFlow<Boolean> = ttsEngine.isPlaying
    val playbackAmplitude: StateFlow<Float> = ttsEngine.playbackAmplitude

    val isRecording: StateFlow<Boolean> = voiceRecorder.isRecording
    val recordingAmplitude: StateFlow<Float> = voiceRecorder.amplitude
    val recordingDurationMs: StateFlow<Long> = voiceRecorder.recordingDurationMs

    val savedRecordings: StateFlow<List<SavedRecording>> = recordingRepo.recordings

    fun selectCelebrity(celebrity: Celebrity) {
        _selectedCelebrity.value = celebrity
        _selectedEmotion.value = celebrity.defaultEmotion
        _selectedEffect.value = celebrity.defaultEmotion.recommendedEffect
        _pitchOffsetSemitones.value = 0
        _speedMultiplier.value = 1.0f
        if (celebrity.signaturePhrases.isNotEmpty()) {
            _inputText.value = celebrity.signaturePhrases.first().romanUrdu
        }
        _statusMessage.value = "Selected ${celebrity.name}"
    }

    fun setCategoryFilter(category: CelebrityCategory?) {
        _categoryFilter.value = category
    }

    fun selectEmotion(emotion: EmotionType) {
        _selectedEmotion.value = emotion
        _selectedEffect.value = emotion.recommendedEffect
        _statusMessage.value = "Applied emotion: ${emotion.displayName}"
    }

    fun selectEffect(effect: AudioEffectType) {
        _selectedEffect.value = effect
        _statusMessage.value = "Applied audio effect: ${effect.title}"

        // If we have recorded mic PCM or existing speech PCM, reprocess live!
        val raw = _rawMicPcm.value
        if (raw != null && raw.isNotEmpty()) {
            reprocessMicAudio(raw)
        }
    }

    fun setPitchOffset(semitones: Int) {
        _pitchOffsetSemitones.value = semitones.coerceIn(-12, 12)
        val raw = _rawMicPcm.value
        if (raw != null && raw.isNotEmpty()) {
            reprocessMicAudio(raw)
        }
    }

    fun setSpeedMultiplier(speed: Float) {
        _speedMultiplier.value = speed.coerceIn(0.5f, 2.0f)
        val raw = _rawMicPcm.value
        if (raw != null && raw.isNotEmpty()) {
            reprocessMicAudio(raw)
        }
    }

    fun setAmbianceVolume(vol: Float) {
        _ambianceVolume.value = vol.coerceIn(0f, 1f)
    }

    fun updateInputText(text: String) {
        _inputText.value = text
    }

    fun selectSignaturePhrase(phrase: SignaturePhrase) {
        _inputText.value = phrase.romanUrdu
        _selectedEmotion.value = phrase.emotion
        _selectedEffect.value = phrase.emotion.recommendedEffect
        synthesizeAndPlay()
    }

    fun previewVoice(celebrity: Celebrity) {
        if (isPlaying.value && _previewPlayingCelebrityId.value == celebrity.id) {
            stopAudio()
            _previewPlayingCelebrityId.value = null
            return
        }

        stopAudio()
        selectCelebrity(celebrity)
        _previewPlayingCelebrityId.value = celebrity.id
        _inputText.value = celebrity.voiceProfile.samplePreviewPhrase
        synthesizeAndPlay()
    }

    fun generateParodyScript(rawText: String) {
        if (rawText.isBlank()) return
        val stylized = ScriptPersonaGenerator.stylizeText(rawText, _selectedCelebrity.value)
        _generatedParodyScript.value = stylized
        _inputText.value = stylized
        _statusMessage.value = "Generated script for ${_selectedCelebrity.value.name}"
    }

    /**
     * Synthesizes input text with celebrity voice parameters and DSP audio effects.
     */
    fun synthesizeAndPlay() {
        val text = _inputText.value.trim()
        if (text.isEmpty()) {
            _statusMessage.value = "Please enter some text to speak"
            return
        }

        stopAudio()
        _statusMessage.value = "Synthesizing voice with ${_selectedEffect.value.title}..."

        viewModelScope.launch {
            val celeb = _selectedCelebrity.value
            val emotion = _selectedEmotion.value
            val effect = _selectedEffect.value
            val pitchOffset = _pitchOffsetSemitones.value.toFloat() * 0.05f
            val speedOffset = (_speedMultiplier.value - 1.0f) * 0.3f

            val success = ttsEngine.synthesizeAndProcess(
                text = text,
                celebrity = celeb,
                emotion = emotion,
                effect = effect,
                customPitchOffset = pitchOffset,
                customSpeedOffset = speedOffset,
                backgroundAmbianceVolume = _ambianceVolume.value
            ) { pcm, _ ->
                _currentPcmBuffer.value = pcm
                _statusMessage.value = "Playing mimicry: ${celeb.name}"
                ttsEngine.playPcm(pcm) {
                    _statusMessage.value = "Finished playing"
                }
            }

            if (!success) {
                _statusMessage.value = "TTS Synthesis failed or language not available"
            }
        }
    }

    /**
     * Start live microphone recording for voice changer mode.
     */
    fun startMicRecording(): Boolean {
        stopAudio()
        val started = voiceRecorder.startRecording()
        if (started) {
            _statusMessage.value = "Recording mic... Speak now!"
        } else {
            _statusMessage.value = "Mic permission or hardware not available"
        }
        return started
    }

    /**
     * Stop mic recording, apply real-time pitch shift and selected audio effect.
     */
    fun stopMicRecording() {
        val pcm = voiceRecorder.stopRecording()
        if (pcm.isEmpty()) {
            _statusMessage.value = "Recording was too short or empty"
            return
        }
        _rawMicPcm.value = pcm
        _statusMessage.value = "Recorded ${(pcm.size.toDouble() / AudioProcessor.SAMPLE_RATE).format(1)}s audio. Processing effects..."
        reprocessMicAudio(pcm)
    }

    private fun reprocessMicAudio(rawPcm: ShortArray) {
        viewModelScope.launch(Dispatchers.Default) {
            val celeb = _selectedCelebrity.value
            val effect = _selectedEffect.value
            val emotion = _selectedEmotion.value

            val totalSemitones = celeb.pitchShiftSemitones + effect.pitchShiftSemitones + _pitchOffsetSemitones.value
            val totalTempo = (effect.tempoFactor * _speedMultiplier.value).coerceIn(0.5f, 2.0f)

            val ambianceType = when (emotion) {
                EmotionType.RALLY_FIERCE -> AudioProcessor.AmbianceType.JALSA_CROWD
                EmotionType.DRAMATIC_EMOTIONAL, EmotionType.ROMANTIC_HUSKY -> AudioProcessor.AmbianceType.CINEMA_STRINGS
                EmotionType.PRESS_CONFERENCE -> AudioProcessor.AmbianceType.PRESS_SHUTTERS
                else -> AudioProcessor.AmbianceType.NONE
            }

            val dspParams = AudioProcessor.ProcessingParams(
                pitchShiftSemitones = totalSemitones,
                tempoMultiplier = totalTempo,
                delayMs = effect.delayMs,
                delayFeedback = effect.delayFeedback,
                reverbAmount = effect.reverbAmount,
                megaphoneIntensity = effect.megaphoneIntensity,
                tremoloDepth = effect.tremoloDepth,
                bassBoost = effect.bassBoost,
                ringModFreq = effect.ringModFreq,
                backgroundAmbianceVolume = _ambianceVolume.value,
                backgroundAmbianceType = ambianceType
            )

            val processed = AudioProcessor.processPcm(rawPcm, dspParams, AudioProcessor.SAMPLE_RATE)
            _currentPcmBuffer.value = processed

            withContext(Dispatchers.Main) {
                _statusMessage.value = "Applied ${effect.title} (Pitch: ${totalSemitones}st, Speed: ${totalTempo}x)"
            }
        }
    }

    fun playCurrentBuffer() {
        val pcm = _currentPcmBuffer.value ?: return
        stopAudio()
        _statusMessage.value = "Playing audio..."
        ttsEngine.playPcm(pcm) {
            _statusMessage.value = "Finished playing"
        }
    }

    fun stopAudio() {
        ttsEngine.stopPlayback()
    }

    fun saveCurrentClip(customTitle: String? = null) {
        val pcm = _currentPcmBuffer.value
        if (pcm == null || pcm.isEmpty()) {
            _statusMessage.value = "No audio available to save"
            return
        }

        val celeb = _selectedCelebrity.value
        val title = customTitle?.ifBlank { null }
            ?: "${celeb.name} - ${_selectedEffect.value.title}"

        val saved = recordingRepo.saveRecording(
            title = title,
            speakerName = celeb.name,
            speakerEmoji = celeb.avatarEmoji,
            effectTitle = _selectedEffect.value.title,
            textOrScript = _inputText.value.take(120),
            pcmData = pcm,
            isTtsGenerated = _rawMicPcm.value == null
        )

        _statusMessage.value = "Saved '${saved.title}' to library!"
    }

    fun playSavedRecording(recording: SavedRecording) {
        stopAudio()
        viewModelScope.launch(Dispatchers.IO) {
            val file = File(recording.filePath)
            val pcm = AudioProcessor.readWavFile(file)
            if (pcm.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    _statusMessage.value = "Playing: ${recording.title}"
                    ttsEngine.playPcm(pcm) {
                        _statusMessage.value = "Finished playing"
                    }
                }
            }
        }
    }

    fun deleteSavedRecording(id: String) {
        recordingRepo.deleteRecording(id)
        _statusMessage.value = "Deleted recording"
    }

    fun getShareIntent(recording: SavedRecording) = recordingRepo.createShareIntent(recording)

    override fun onCleared() {
        super.onCleared()
        ttsEngine.release()
    }

    private fun Double.format(digits: Int) = "%.${digits}f".format(this)
}
