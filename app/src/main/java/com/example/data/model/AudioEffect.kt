package com.example.data.model

enum class AudioEffectType(
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val pitchShiftSemitones: Int, // Shift in semitones (-12 to +12)
    val tempoFactor: Float,       // Speed multiplier (0.5 to 2.0)
    val delayMs: Int,             // Echo delay time (0 to 600 ms)
    val delayFeedback: Float,     // Echo decay (0.0 to 0.8)
    val reverbAmount: Float,      // Reverb wetness (0.0 to 1.0)
    val megaphoneIntensity: Float, // Overdrive/Bandpass (0.0 to 1.0)
    val tremoloDepth: Float,      // Vibrato/Tremolo (0.0 to 1.0)
    val bassBoost: Float,         // Low frequency boost (0.0 to 2.0)
    val ringModFreq: Float        // Robot modulator Hz (0 = off)
) {
    STUDIO_CLEAN(
        title = "Studio Clean",
        subtitle = "Neutral broadcast voice",
        iconEmoji = "🎙️",
        pitchShiftSemitones = 0,
        tempoFactor = 1.0f,
        delayMs = 0,
        delayFeedback = 0.0f,
        reverbAmount = 0.05f,
        megaphoneIntensity = 0.0f,
        tremoloDepth = 0.0f,
        bassBoost = 1.1f,
        ringModFreq = 0f
    ),
    JALSA_ECHO(
        title = "Jalsa Rally Echo",
        subtitle = "Massive political stadium echo",
        iconEmoji = "📢",
        pitchShiftSemitones = 0,
        tempoFactor = 1.0f,
        delayMs = 280,
        delayFeedback = 0.45f,
        reverbAmount = 0.55f,
        megaphoneIntensity = 0.25f,
        tremoloDepth = 0.0f,
        bassBoost = 1.3f,
        ringModFreq = 0f
    ),
    CINEMA_REVERB(
        title = "Bollywood Cinema",
        subtitle = "Lush theatrical acoustic space",
        iconEmoji = "🎬",
        pitchShiftSemitones = 0,
        tempoFactor = 1.0f,
        delayMs = 120,
        delayFeedback = 0.25f,
        reverbAmount = 0.70f,
        megaphoneIntensity = 0.0f,
        tremoloDepth = 0.0f,
        bassBoost = 1.25f,
        ringModFreq = 0f
    ),
    DEEP_BARITONE(
        title = "Deep Bhaijaan",
        subtitle = "Subwoofer chest bass (-4 semitones)",
        iconEmoji = "🕶️",
        pitchShiftSemitones = -4,
        tempoFactor = 0.95f,
        delayMs = 0,
        delayFeedback = 0.0f,
        reverbAmount = 0.20f,
        megaphoneIntensity = 0.0f,
        tremoloDepth = 0.0f,
        bassBoost = 1.8f,
        ringModFreq = 0f
    ),
    SHARP_HIGH(
        title = "Fiery Orator",
        subtitle = "High-pitched sharp tone (+4 semitones)",
        iconEmoji = "⚡",
        pitchShiftSemitones = 4,
        tempoFactor = 1.05f,
        delayMs = 60,
        delayFeedback = 0.2f,
        reverbAmount = 0.25f,
        megaphoneIntensity = 0.1f,
        tremoloDepth = 0.0f,
        bassBoost = 0.8f,
        ringModFreq = 0f
    ),
    MEGAPHONE(
        title = "Rally Megaphone",
        subtitle = "Bandpass filtered PA loudspeaker",
        iconEmoji = "📣",
        pitchShiftSemitones = 1,
        tempoFactor = 1.0f,
        delayMs = 80,
        delayFeedback = 0.35f,
        reverbAmount = 0.15f,
        megaphoneIntensity = 0.85f,
        tremoloDepth = 0.0f,
        bassBoost = 0.5f,
        ringModFreq = 0f
    ),
    ROMANTIC_TREMOLO(
        title = "SRK Romance",
        subtitle = "Breathless emotional tremble",
        iconEmoji = "🌹",
        pitchShiftSemitones = 0,
        tempoFactor = 0.95f,
        delayMs = 90,
        delayFeedback = 0.2f,
        reverbAmount = 0.45f,
        megaphoneIntensity = 0.0f,
        tremoloDepth = 0.40f,
        bassBoost = 1.1f,
        ringModFreq = 0f
    ),
    ROBOT_SYNTH(
        title = "Cyber Synth",
        subtitle = "Futuristic robot modulator",
        iconEmoji = "🤖",
        pitchShiftSemitones = -2,
        tempoFactor = 1.0f,
        delayMs = 0,
        delayFeedback = 0.0f,
        reverbAmount = 0.3f,
        megaphoneIntensity = 0.0f,
        tremoloDepth = 0.0f,
        bassBoost = 1.0f,
        ringModFreq = 65f
    ),
    CHIPMUNK(
        title = "Hyper Chipmunk",
        subtitle = "Fun high-speed cartoon pitch (+8 st)",
        iconEmoji = "🐿️",
        pitchShiftSemitones = 8,
        tempoFactor = 1.30f,
        delayMs = 0,
        delayFeedback = 0.0f,
        reverbAmount = 0.1f,
        megaphoneIntensity = 0.0f,
        tremoloDepth = 0.0f,
        bassBoost = 0.6f,
        ringModFreq = 0f
    )
}
