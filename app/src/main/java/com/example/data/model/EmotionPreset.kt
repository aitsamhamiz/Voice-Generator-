package com.example.data.model

enum class EmotionType(
    val displayName: String,
    val description: String,
    val iconEmoji: String,
    val pitchMultiplier: Float, // Multiplied with base pitch
    val speedMultiplier: Float, // Multiplied with base speed
    val recommendedEffect: AudioEffectType
) {
    RALLY_FIERCE(
        displayName = "Jalsa / Rally Fierce",
        description = "High energy, stadium roar, crowd echo, fiery political urgency",
        iconEmoji = "📢",
        pitchMultiplier = 1.12f,
        speedMultiplier = 1.18f,
        recommendedEffect = AudioEffectType.JALSA_ECHO
    ),
    DRAMATIC_EMOTIONAL(
        displayName = "Dramatic & Filmi",
        description = "Intense Bollywood theater reverb, deliberate emotional pauses",
        iconEmoji = "🎭",
        pitchMultiplier = 0.94f,
        speedMultiplier = 0.88f,
        recommendedEffect = AudioEffectType.CINEMA_REVERB
    ),
    ROMANTIC_HUSKY(
        displayName = "Romantic & Husky",
        description = "Breathless, soft emotional warmth, intimate cinema presence",
        iconEmoji = "🌹",
        pitchMultiplier = 0.96f,
        speedMultiplier = 0.92f,
        recommendedEffect = AudioEffectType.ROMANTIC_TREMOLO
    ),
    MACHO_SWAGGER(
        displayName = "Macho Swagger",
        description = "Deep chest resonance, heavy bass punch, fearless attitude",
        iconEmoji = "💪",
        pitchMultiplier = 0.85f,
        speedMultiplier = 0.90f,
        recommendedEffect = AudioEffectType.DEEP_BARITONE
    ),
    SARCASTIC_ROAST(
        displayName = "Sarcastic & Witty",
        description = "Sharp ironic inflections, mocking cadence, satirical speech",
        iconEmoji = "😏",
        pitchMultiplier = 1.08f,
        speedMultiplier = 1.05f,
        recommendedEffect = AudioEffectType.MEGAPHONE
    ),
    PRESS_CONFERENCE(
        displayName = "Formal Press Meet",
        description = "Crisp, solemn, media room acoustics, clear broadcast tone",
        iconEmoji = "🎙️",
        pitchMultiplier = 1.00f,
        speedMultiplier = 1.00f,
        recommendedEffect = AudioEffectType.STUDIO_CLEAN
    )
}
