package com.example

import com.example.audio.AudioProcessor
import com.example.data.generator.ScriptPersonaGenerator
import com.example.data.model.AudioEffectType
import com.example.data.model.CelebrityCatalog
import com.example.data.model.CelebrityCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testAllRequestedCelebritiesPresent() {
        val celebs = CelebrityCatalog.celebrities
        val ids = celebs.map { it.id }.toSet()

        assertTrue(ids.contains("imran_khan"))
        assertTrue(ids.contains("maryam_nawaz"))
        assertTrue(ids.contains("shehbaz_sharif"))
        assertTrue(ids.contains("nawaz_sharif"))
        assertTrue(ids.contains("shah_rukh_khan"))
        assertTrue(ids.contains("salman_khan"))
        assertTrue(ids.contains("ajay_devgn"))

        assertEquals(7, celebs.size)
    }

    @Test
    fun testCelebrityCategories() {
        val political = CelebrityCatalog.celebrities.filter { it.category == CelebrityCategory.POLITICAL }
        val bollywood = CelebrityCatalog.celebrities.filter { it.category == CelebrityCategory.BOLLYWOOD }

        assertEquals(4, political.size) // Imran, Maryam, Shehbaz, Nawaz
        assertEquals(3, bollywood.size) // SRK, Salman, Ajay
    }

    @Test
    fun testScriptPersonaGenerator() {
        val ik = CelebrityCatalog.getById("imran_khan")
        val ikSpeech = ScriptPersonaGenerator.stylizeText("Petrol mehenga hai", ik)
        assertTrue(ikSpeech.contains("ghabrana bilkul nahi hai"))
        assertTrue(ikSpeech.contains("Petrol mehenga hai"))

        val srk = CelebrityCatalog.getById("shah_rukh_khan")
        val srkSpeech = ScriptPersonaGenerator.stylizeText("I love you", srk)
        assertTrue(srkSpeech.contains("Senorita"))
        assertTrue(srkSpeech.contains("Picture abhi baaki hai"))

        val salman = CelebrityCatalog.getById("salman_khan")
        val salmanSpeech = ScriptPersonaGenerator.stylizeText("Commitment", salman)
        assertTrue(salmanSpeech.contains("commitment kar di"))
    }

    @Test
    fun testAudioProcessorWavSerialization() {
        // Generate a 1-second 440Hz sine wave PCM
        val sampleRate = AudioProcessor.SAMPLE_RATE
        val testPcm = ShortArray(sampleRate) { i ->
            (kotlin.math.sin(2.0 * kotlin.math.PI * 440.0 * i / sampleRate) * 16000).toInt().toShort()
        }

        val wavBytes = AudioProcessor.pcmToWav(testPcm, sampleRate)
        assertTrue(wavBytes.size > 44)
        // Verify 'RIFF' header
        assertEquals('R'.code.toByte(), wavBytes[0])
        assertEquals('I'.code.toByte(), wavBytes[1])
        assertEquals('F'.code.toByte(), wavBytes[2])
        assertEquals('F'.code.toByte(), wavBytes[3])

        // Verify 'WAVE' format
        assertEquals('W'.code.toByte(), wavBytes[8])
        assertEquals('A'.code.toByte(), wavBytes[9])
        assertEquals('V'.code.toByte(), wavBytes[10])
        assertEquals('E'.code.toByte(), wavBytes[11])
    }

    @Test
    fun testAudioDspEffectsProcessing() {
        val sampleRate = AudioProcessor.SAMPLE_RATE
        val testPcm = ShortArray(sampleRate / 2) { i ->
            (kotlin.math.sin(2.0 * kotlin.math.PI * 220.0 * i / sampleRate) * 12000).toInt().toShort()
        }

        val params = AudioProcessor.ProcessingParams(
            pitchShiftSemitones = 4,
            tempoMultiplier = 1.1f,
            delayMs = 200,
            delayFeedback = 0.3f,
            reverbAmount = 0.5f,
            megaphoneIntensity = 0.5f
        )

        val processed = AudioProcessor.processPcm(testPcm, params, sampleRate)
        assertNotNull(processed)
        assertTrue(processed.isNotEmpty())
    }
}
