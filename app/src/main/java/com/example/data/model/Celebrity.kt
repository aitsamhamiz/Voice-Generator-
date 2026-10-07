package com.example.data.model

enum class CelebrityCategory {
    POLITICAL,
    BOLLYWOOD
}

data class VoiceProfile(
    val timbre: String,
    val pitchLabel: String,
    val cadenceSpeedLabel: String,
    val resonance: String,
    val mannerisms: List<String>,
    val samplePreviewPhrase: String
)

data class SignaturePhrase(
    val title: String,
    val romanUrdu: String,
    val translation: String,
    val emotion: EmotionType = EmotionType.RALLY_FIERCE
)

data class Celebrity(
    val id: String,
    val name: String,
    val urduName: String,
    val title: String,
    val category: CelebrityCategory,
    val basePitch: Float,        // TTS pitch multiplier (e.g. 0.75 - 1.35)
    val baseSpeed: Float,        // TTS speech rate (e.g. 0.85 - 1.30)
    val pitchShiftSemitones: Int, // PCM DSP pitch shift in semitones (-6 to +6)
    val accentColorHex: Long,
    val secondaryColorHex: Long,
    val avatarEmoji: String,
    val tagLine: String,
    val description: String,
    val voiceProfile: VoiceProfile,
    val defaultEmotion: EmotionType,
    val signaturePhrases: List<SignaturePhrase>
)

object CelebrityCatalog {
    val celebrities: List<Celebrity> = listOf(
        Celebrity(
            id = "imran_khan",
            name = "Imran Khan",
            urduName = "عمران خان",
            title = "Former PM & Cricket Legend",
            category = CelebrityCategory.POLITICAL,
            basePitch = 0.92f,
            baseSpeed = 1.05f,
            pitchShiftSemitones = -1,
            accentColorHex = 0xFF007A3D, // PTI Green
            secondaryColorHex = 0xFFD32F2F, // Red
            avatarEmoji = "🏏",
            tagLine = "Ghabrana Nahi Hai! Absolute Not!",
            description = "Passionate, fiery rally speeches with dramatic pauses, resolute tone, and charismatic appeal.",
            voiceProfile = VoiceProfile(
                timbre = "Fiery Rally Baritone",
                pitchLabel = "Mid-Low (-1 semitone)",
                cadenceSpeedLabel = "Dynamic 1.05x",
                resonance = "Stadium Jalsa Reverb",
                mannerisms = listOf("Dramatic suspense pauses", "Passionate finger pointing", "Direct rhetorical questions"),
                samplePreviewPhrase = "Aap ne sab se pehle ghabrana bilkul nahi hai! Absolutely Not!"
            ),
            defaultEmotion = EmotionType.RALLY_FIERCE,
            signaturePhrases = listOf(
                SignaturePhrase(
                    title = "Ghabrana Nahi Hai",
                    romanUrdu = "Aap ne sab se pehle ghabrana bilkul nahi hai! Main inko rulaunga!",
                    translation = "First of all, you must not panic at all! I will make them weep!",
                    emotion = EmotionType.RALLY_FIERCE
                ),
                SignaturePhrase(
                    title = "Absolutely Not!",
                    romanUrdu = "Absolutely Not! Hum koi ghulam hain jo aapka har hukum manenge?",
                    translation = "Absolutely Not! Are we slaves to obey your every command?",
                    emotion = EmotionType.RALLY_FIERCE
                ),
                SignaturePhrase(
                    title = "Tabdeeli Aa Gayi Hai",
                    romanUrdu = "Tabdeeli aa nahi rahi, naya Pakistan ban chuka hai, tabdeeli aa gayi hai!",
                    translation = "Change is not coming, it has already arrived!",
                    emotion = EmotionType.RALLY_FIERCE
                ),
                SignaturePhrase(
                    title = "Umpire Ki Ungli",
                    romanUrdu = "Awam tayyar ho jaye, umpire ki ungli khari hone wali hai!",
                    translation = "Nation get ready, the umpire's finger is about to go up!",
                    emotion = EmotionType.SARCASTIC_ROAST
                ),
                SignaturePhrase(
                    title = "Haqeeqi Azadi",
                    romanUrdu = "Yeh jang haqeeqi azadi ki jang hai aur qaum pichhe nahi hategi!",
                    translation = "This is the war for true freedom and the nation won't back down!",
                    emotion = EmotionType.RALLY_FIERCE
                )
            )
        ),
        Celebrity(
            id = "maryam_nawaz",
            name = "Maryam Nawaz Sharif",
            urduName = "مریم نواز شریف",
            title = "CM Punjab & PML-N Leader",
            category = CelebrityCategory.POLITICAL,
            basePitch = 1.32f,
            baseSpeed = 1.08f,
            pitchShiftSemitones = 4,
            accentColorHex = 0xFF0A7E32, // Emerald green
            secondaryColorHex = 0xFFFFD700, // Gold
            avatarEmoji = "🦁",
            tagLine = "Vote Ko Izzat Do! Sher Aaya!",
            description = "High-pitched, sharp, fiery cadenced orator with fearless defiance and energetic political slogans.",
            voiceProfile = VoiceProfile(
                timbre = "Sharp Staccato Orator",
                pitchLabel = "High Treble (+4 semitones)",
                cadenceSpeedLabel = "Punchy 1.08x",
                resonance = "Megaphone Loudspeaker PA",
                mannerisms = listOf("Defiant high pitch spikes", "Crowd chorus slogans", "Rhythmic clapping cadence"),
                samplePreviewPhrase = "Mian de naare vajjan ge! Vote ko izzat do, sher aaya maidan vich!"
            ),
            defaultEmotion = EmotionType.RALLY_FIERCE,
            signaturePhrases = listOf(
                SignaturePhrase(
                    title = "Mian De Naare",
                    romanUrdu = "Mian de naare vajjan ge! Sher aik wari fer maidan vich aa chuka hai!",
                    translation = "The slogans for Mian will echo! The lion has entered the field once again!",
                    emotion = EmotionType.RALLY_FIERCE
                ),
                SignaturePhrase(
                    title = "Vote Ko Izzat Do",
                    romanUrdu = "Vote ko izzat do! Awam ke haq par daka dalne walo ka hisaab hoga!",
                    translation = "Respect the vote! Those who stole public mandates will be held accountable!",
                    emotion = EmotionType.RALLY_FIERCE
                ),
                SignaturePhrase(
                    title = "Awam Ka Faisla",
                    romanUrdu = "Awam ka faisla aa chuka hai, ab koi sazish kamyab nahi hogi!",
                    translation = "The verdict of the public is in, no conspiracy will succeed now!",
                    emotion = EmotionType.RALLY_FIERCE
                ),
                SignaturePhrase(
                    title = "Khidmat Ki Raah",
                    romanUrdu = "Punjab ki awam janti hai ke khidmat kisay kehte hain, kaam bolta hai!",
                    translation = "The people of Punjab know what true service is, work speaks!",
                    emotion = EmotionType.PRESS_CONFERENCE
                )
            )
        ),
        Celebrity(
            id = "shehbaz_sharif",
            name = "Shehbaz Sharif",
            urduName = "شہباز شریف",
            title = "Prime Minister of Pakistan",
            category = CelebrityCategory.POLITICAL,
            basePitch = 1.05f,
            baseSpeed = 1.32f,
            pitchShiftSemitones = 1,
            accentColorHex = 0xFF1565C0, // Electric Blue
            secondaryColorHex = 0xFFFF9800, // Orange
            avatarEmoji = "⚡",
            tagLine = "Punjab Speed! Kashkol Tod Denge!",
            description = "Rapid-fire speed, energetic dramatic urgency, poetic Urdu recitations, and vigorous speech cadence.",
            voiceProfile = VoiceProfile(
                timbre = "Rapid High-Speed Cadence",
                pitchLabel = "Urgent Mid (+1 semitone)",
                cadenceSpeedLabel = "Lightning 1.32x Speed",
                resonance = "Urgent Podium Microphone",
                mannerisms = listOf("Dramatic mic-shaking hand waving", "Urgent breath intakes", "Passionate Habib Jalib poetry recitations"),
                samplePreviewPhrase = "Khadim-e-Aala speed se kaam hoga! Yeh kashkol hum tod kar phenk denge!"
            ),
            defaultEmotion = EmotionType.RALLY_FIERCE,
            signaturePhrases = listOf(
                SignaturePhrase(
                    title = "Khadim-e-Aala Speed",
                    romanUrdu = "Khadim-e-Aala speed se kaam hoga! Din raat aik kar denge, aadhi aadhi raat ko inspection karenge!",
                    translation = "Work will proceed at Khadim-e-Aala speed! We will work day and night!",
                    emotion = EmotionType.RALLY_FIERCE
                ),
                SignaturePhrase(
                    title = "Kashkol Tod Denge",
                    romanUrdu = "Yeh kashkol hum tod kar phenk denge! Bheekh nahi mangenge, qurbani denge!",
                    translation = "We will break and throw away this begging bowl! We will work, not beg!",
                    emotion = EmotionType.RALLY_FIERCE
                ),
                SignaturePhrase(
                    title = "Habib Jalib Poetry",
                    romanUrdu = "Aise dastoor ko, subh-e-be-noor ko, main nahi maanta, main nahi jaanta!",
                    translation = "Such a constitution, such a lightless dawn, I do not accept, I do not recognize!",
                    emotion = EmotionType.DRAMATIC_EMOTIONAL
                ),
                SignaturePhrase(
                    title = "Pakistan Speed",
                    romanUrdu = "Punjab speed ab Pakistan speed banegi! Har shehri ko bijli, sadak aur rozgar milega!",
                    translation = "Punjab speed will now become Pakistan speed! Every citizen will prosper!",
                    emotion = EmotionType.RALLY_FIERCE
                )
            )
        ),
        Celebrity(
            id = "nawaz_sharif",
            name = "Nawaz Sharif",
            urduName = "نواز شریف",
            title = "3-Time Prime Minister",
            category = CelebrityCategory.POLITICAL,
            basePitch = 0.78f,
            baseSpeed = 0.82f,
            pitchShiftSemitones = -4,
            accentColorHex = 0xFF2E7D32, // Forest Green
            secondaryColorHex = 0xFF8D6E63, // Warm Earth
            avatarEmoji = "👑",
            tagLine = "Mujhe Kyun Nikala? Sher Aik Wari Fer!",
            description = "Deep baritone, slow deliberate pauses, solemn cadence, authoritative statesman style.",
            voiceProfile = VoiceProfile(
                timbre = "Solemn Dignified Bass",
                pitchLabel = "Deep Heavy Bass (-4 semitones)",
                cadenceSpeedLabel = "Deliberate 0.82x",
                resonance = "Chamber Statesman Acoustics",
                mannerisms = listOf("Prolonged reflective pauses", "Grave deep tone", "Repetitive emotional inquiries"),
                samplePreviewPhrase = "Mujhe kyun nikala? Mera jurm kya tha? Main awam ki adalat mein ja raha hoon."
            ),
            defaultEmotion = EmotionType.PRESS_CONFERENCE,
            signaturePhrases = listOf(
                SignaturePhrase(
                    title = "Mujhe Kyun Nikala?",
                    romanUrdu = "Mujhe kyun nikala? Mera jurm kya tha? Kya Pakistan ko nuclear power banana jurm tha?",
                    translation = "Why was I disqualified? What was my crime? Was making Pakistan a nuclear power a crime?",
                    emotion = EmotionType.DRAMATIC_EMOTIONAL
                ),
                SignaturePhrase(
                    title = "Motorway Aur Taraqqi",
                    romanUrdu = "Motorway hum ne banayi, load shedding hum ne khatam ki, mulk taraqqi kar raha tha!",
                    translation = "We built the motorways, ended power outages, the country was progressing rapidly!",
                    emotion = EmotionType.PRESS_CONFERENCE
                ),
                SignaturePhrase(
                    title = "Awam Ki Adalat",
                    romanUrdu = "Main yeh muqadma awam ki sab se bari adalat mein pesh karne ja raha hoon!",
                    translation = "I am taking this case to the highest court of all: the people's court!",
                    emotion = EmotionType.DRAMATIC_EMOTIONAL
                ),
                SignaturePhrase(
                    title = "Sher Wapis Aaya",
                    romanUrdu = "Sher wapis aaya hai, aur ab qaum ki kismet badal kar dam lenge!",
                    translation = "The lion has returned, and we will not rest until the nation's destiny is transformed!",
                    emotion = EmotionType.RALLY_FIERCE
                )
            )
        ),
        Celebrity(
            id = "shah_rukh_khan",
            name = "Shah Rukh Khan",
            urduName = "شاہ رخ خان",
            title = "King of Bollywood & Romance",
            category = CelebrityCategory.BOLLYWOOD,
            basePitch = 0.95f,
            baseSpeed = 0.96f,
            pitchShiftSemitones = 0,
            accentColorHex = 0xFF8E24AA, // Royal Purple
            secondaryColorHex = 0xFFFF4081, // Romantic Pink
            avatarEmoji = "✨",
            tagLine = "Bade Bade Deshon Mein... K..K..Kiran!",
            description = "Expressive vocal fry, dramatic romantic stammer, heartfelt sighs, charismatic cinematic intensity.",
            voiceProfile = VoiceProfile(
                timbre = "Romantic Breathy Vocal Fry",
                pitchLabel = "Expressive Mid (0 semitone)",
                cadenceSpeedLabel = "Cinematic 0.96x",
                resonance = "Bollywood Film Reverb & Strings",
                mannerisms = listOf("Iconic heartfelt sigh", "Romantic dramatic stammer (K..k..kiran)", "Deep dimpled smile inflections"),
                samplePreviewPhrase = "Bade bade deshon mein aisi chhoti chhoti baatein hoti rehti hain, Senorita!"
            ),
            defaultEmotion = EmotionType.ROMANTIC_HUSKY,
            signaturePhrases = listOf(
                SignaturePhrase(
                    title = "Senorita Dialogue",
                    romanUrdu = "Bade bade deshon mein aisi chhoti chhoti baatein hoti rehti hain, Senorita!",
                    translation = "In large countries, such little things keep happening, Senorita!",
                    emotion = EmotionType.ROMANTIC_HUSKY
                ),
                SignaturePhrase(
                    title = "K...k...k...Kiran!",
                    romanUrdu = "K... k... k... Kiran! Tum sirf meri ho, kisi aur ki nahi ho sakti!",
                    translation = "K... k... k... Kiran! You are only mine, you cannot belong to anyone else!",
                    emotion = EmotionType.DRAMATIC_EMOTIONAL
                ),
                SignaturePhrase(
                    title = "Rahul Naam Toh Suna Hoga",
                    romanUrdu = "Rahul... naam toh suna hoga? Mohabbat bhi zindagi ki tarah hoti hai, har mod aasan nahi hota.",
                    translation = "Rahul... you must have heard the name? Love is like life, not every turn is easy.",
                    emotion = EmotionType.ROMANTIC_HUSKY
                ),
                SignaturePhrase(
                    title = "Picture Abhi Baaki Hai",
                    romanUrdu = "Picture abhi baaki hai mere dost! Agar ant bura ho, toh yeh the end nahi hai!",
                    translation = "The movie isn't over yet my friend! If the ending is sad, then it's not the end!",
                    emotion = EmotionType.DRAMATIC_EMOTIONAL
                ),
                SignaturePhrase(
                    title = "Don Ka Khauf",
                    romanUrdu = "Don ko pakadna mushkil hi nahi, naamumkin hai!",
                    translation = "Catching Don is not just difficult, it's impossible!",
                    emotion = EmotionType.MACHO_SWAGGER
                )
            )
        ),
        Celebrity(
            id = "salman_khan",
            name = "Salman Khan",
            urduName = "سلمان خان",
            title = "Bhaijaan of Bollywood",
            category = CelebrityCategory.BOLLYWOOD,
            basePitch = 0.82f,
            baseSpeed = 0.92f,
            pitchShiftSemitones = -3,
            accentColorHex = 0xFFC2185B, // Deep Crimson / Magenta
            secondaryColorHex = 0xFF212121, // Charcoal
            avatarEmoji = "🕶️",
            tagLine = "Ek Baar Jo Maine Commitment Kar Di!",
            description = "Deep chest swagger, nonchalant swagger, bass-heavy nonchalance, iconic punchy one-liners.",
            voiceProfile = VoiceProfile(
                timbre = "Chest Bass Macho Swagger",
                pitchLabel = "Subwoofer Bass (-3 semitones)",
                cadenceSpeedLabel = "Nonchalant 0.92x",
                resonance = "Booming Sub-Bass Theater",
                mannerisms = listOf("Casual slow drawl", "Confident chuckles", "Unapologetic swagger punchlines"),
                samplePreviewPhrase = "Ek baar jo maine commitment kar di, uske baad toh main apne aap ki bhi nahi sunta!"
            ),
            defaultEmotion = EmotionType.MACHO_SWAGGER,
            signaturePhrases = listOf(
                SignaturePhrase(
                    title = "Commitment Dialogue",
                    romanUrdu = "Ek baar jo maine commitment kar di, uske baad toh main apne aap ki bhi nahi sunta!",
                    translation = "Once I make a commitment, after that I don't even listen to myself!",
                    emotion = EmotionType.MACHO_SWAGGER
                ),
                SignaturePhrase(
                    title = "Underestimate Mat Karna",
                    romanUrdu = "Zindagi mein teen cheezein kabhi underestimate mat karna: I, Me, and Myself!",
                    translation = "Never underestimate three things in life: I, Me, and Myself!",
                    emotion = EmotionType.MACHO_SWAGGER
                ),
                SignaturePhrase(
                    title = "Swag Se Swagat",
                    romanUrdu = "Swag se karenge sabka swagat! Dosti mein no sorry, no thank you!",
                    translation = "We will welcome everyone with swag! In friendship: no sorry, no thank you!",
                    emotion = EmotionType.MACHO_SWAGGER
                ),
                SignaturePhrase(
                    title = "Robin Hood Pandey",
                    romanUrdu = "Hum yahan ke Robin Hood hain, Robin Hood Pandey! Thappad se darr nahi lagta sahab, pyaar se lagta hai!",
                    translation = "I am the Robin Hood here! I don't fear a slap, I fear love!",
                    emotion = EmotionType.MACHO_SWAGGER
                )
            )
        ),
        Celebrity(
            id = "ajay_devgn",
            name = "Ajay Devgn",
            urduName = "اجے دیوگن",
            title = "Action Hero & Gritty Legend",
            category = CelebrityCategory.BOLLYWOOD,
            basePitch = 0.74f,
            baseSpeed = 0.84f,
            pitchShiftSemitones = -5,
            accentColorHex = 0xFFD84315, // Burnt Sienna / Fire
            secondaryColorHex = 0xFF37474F, // Dark Slate
            avatarEmoji = "🔥",
            tagLine = "Aata Maajhi Satakli! Singham!",
            description = "Intense, grave gravel-baritone, smoldering cinematic punchlines, steely cold glare in audio form.",
            voiceProfile = VoiceProfile(
                timbre = "Gravel Bass Smolder",
                pitchLabel = "Ultra Deep (-5 semitones)",
                cadenceSpeedLabel = "Cold & Slow 0.84x",
                resonance = "Action Cinematic Impact",
                mannerisms = listOf("Steely slow pacing", "Gravelly throat vocal vibration", "Sudden roaring crescendo"),
                samplePreviewPhrase = "Aata maajhi satakli! Jismein hai dum, toh fakhat Bajirao Singham!"
            ),
            defaultEmotion = EmotionType.MACHO_SWAGGER,
            signaturePhrases = listOf(
                SignaturePhrase(
                    title = "Aata Maajhi Satakli",
                    romanUrdu = "Aata maajhi satakli! Jismein hai dum, toh fakhat Bajirao Singham!",
                    translation = "Now I have lost my temper! The only one with courage is Bajirao Singham!",
                    emotion = EmotionType.MACHO_SWAGGER
                ),
                SignaturePhrase(
                    title = "Dua Mein Yaad Rakhna",
                    romanUrdu = "Dua mein yaad rakhna, hum wahan se shuru karte hain jahan sab haar maan lete hain!",
                    translation = "Remember me in prayers, we start right where everyone else gives up!",
                    emotion = EmotionType.DRAMATIC_EMOTIONAL
                ),
                SignaturePhrase(
                    title = "Sheeshe Ke Ghar",
                    romanUrdu = "Jinke ghar sheeshe ke hote hain, wo basement mein kapde badalte hain!",
                    translation = "Those living in glass houses change clothes in the basement!",
                    emotion = EmotionType.SARCASTIC_ROAST
                ),
                SignaturePhrase(
                    title = "Goli Wahi Lagegi",
                    romanUrdu = "Kanoon andha hota hai, main nahi! Goli wahi lagegi jahan dard zyada ho!",
                    translation = "The law may be blind, but I am not! The bullet will hit right where it hurts most!",
                    emotion = EmotionType.MACHO_SWAGGER
                )
            )
        )
    )

    fun getById(id: String): Celebrity =
        celebrities.firstOrNull { it.id == id } ?: celebrities.first()
}
