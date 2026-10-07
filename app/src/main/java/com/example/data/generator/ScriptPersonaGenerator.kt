package com.example.data.generator

import com.example.data.model.Celebrity

object ScriptPersonaGenerator {

    /**
     * Transforms any input user text into the authentic speech mannerisms,
     * vocabulary, slogans, and cadence of the selected public figure.
     */
    fun stylizeText(input: String, celebrity: Celebrity): String {
        val cleanInput = input.trim()
        if (cleanInput.isEmpty()) return ""

        return when (celebrity.id) {
            "imran_khan" -> {
                "Aap ne sab se pehle ghabrana bilkul nahi hai! Main aapko bata doon ke $cleanInput! Hum inko rula ke rahenge, qaum jaag chuki hai, Absolutely Not!"
            }
            "maryam_nawaz" -> {
                "Awam sun le! Mian Nawaz Sharif ka aur mera yeh paigham hai ke $cleanInput! Mian de naare vajjan ge! Vote ko izzat do, sher aaya maidan vich!"
            }
            "shehbaz_sharif" -> {
                "Khadim-e-Aala speed se keh raha hoon! Din raat aik kar denge, aadhi aadhi raat ko inspection karenge ke $cleanInput! Bheekh nahi mangenge, mehnat se mulk banayenge!"
            }
            "nawaz_sharif" -> {
                "Mujhe kyun nikala? Mera jurm kya tha? Main aaj awam ki adalat mein yeh sawal le kar aaya hoon ke $cleanInput! Mulk taraqqi ki raah par wapis aayega."
            }
            "shah_rukh_khan" -> {
                "Bade bade deshon mein aisi chhoti chhoti baatein hoti rehti hain, Senorita... Sach keh raha hoon, $cleanInput! Picture abhi baaki hai mere dost!"
            }
            "salman_khan" -> {
                "Ek baar jo maine commitment kar di, uske baad toh main apne aap ki bhi nahi sunta! Zindagi ka asool simple hai: $cleanInput! Swag se karenge sabka swagat!"
            }
            "ajay_devgn" -> {
                "Aata maajhi satakli! Jismein hai dum, fakhat Bajirao Singham! Dua mein yaad rakhna, kyunki $cleanInput! Kanoon andha hota hai, main nahi!"
            }
            else -> input
        }
    }

    /**
     * Curated sample user prompts for rapid testing and fun play.
     */
    val samplePrompts = listOf(
        "Bijli ka bill bohat zyada aa gaya hai",
        "Subah office jaldi nahi utha jata",
        "Biryani mein aalu hona chahiye ya nahi?",
        "Chai thandi ho gayi hai",
        "Petrol ki qeemat kam karo",
        "Weekend par party karni hai",
        "Traffic jam mein phans gaya hoon"
    )
}
