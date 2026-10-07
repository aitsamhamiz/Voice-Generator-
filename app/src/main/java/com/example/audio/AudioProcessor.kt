package com.example.audio

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.tanh

object AudioProcessor {

    const val SAMPLE_RATE = 44100
    private const val NUM_CHANNELS = 1
    private const val BITS_PER_SAMPLE = 16

    data class ProcessingParams(
        val pitchShiftSemitones: Int = 0,
        val tempoMultiplier: Float = 1.0f,
        val delayMs: Int = 0,
        val delayFeedback: Float = 0.0f,
        val reverbAmount: Float = 0.0f,
        val megaphoneIntensity: Float = 0.0f,
        val tremoloDepth: Float = 0.0f,
        val bassBoost: Float = 1.0f,
        val ringModFreq: Float = 0.0f,
        val backgroundAmbianceVolume: Float = 0.0f,
        val backgroundAmbianceType: AmbianceType = AmbianceType.NONE
    )

    enum class AmbianceType {
        NONE,
        JALSA_CROWD,
        CINEMA_STRINGS,
        PRESS_SHUTTERS
    }

    /**
     * Process 16-bit PCM audio samples with the requested audio DSP parameters.
     */
    fun processPcm(
        inputPcm: ShortArray,
        params: ProcessingParams,
        sampleRate: Int = SAMPLE_RATE
    ): ShortArray {
        if (inputPcm.isEmpty()) return ShortArray(0)

        // Step 1: Pitch shift & Tempo stretch (Granular overlap-add)
        var processed = if (params.pitchShiftSemitones != 0 || params.tempoMultiplier != 1.0f) {
            pitchShiftAndStretch(
                input = inputPcm,
                semitones = params.pitchShiftSemitones,
                tempo = params.tempoMultiplier,
                sampleRate = sampleRate
            )
        } else {
            inputPcm.copyOf()
        }

        // Convert to float array (-1.0 to 1.0) for linear DSP pipeline
        val floatSamples = FloatArray(processed.size) { i ->
            (processed[i] / 32768.0f).coerceIn(-1.0f, 1.0f)
        }

        // Step 2: Megaphone / Bandpass & Overdrive saturation
        if (params.megaphoneIntensity > 0.01f) {
            applyMegaphoneEffect(floatSamples, params.megaphoneIntensity, sampleRate)
        }

        // Step 3: Bass boost (Low-shelf boost)
        if (params.bassBoost > 1.05f || params.bassBoost < 0.95f) {
            applyBassBoost(floatSamples, params.bassBoost, sampleRate)
        }

        // Step 4: Ring Modulation (Robot synth voice)
        if (params.ringModFreq > 1.0f) {
            applyRingModulation(floatSamples, params.ringModFreq, sampleRate)
        }

        // Step 5: Tremolo / Vibrato (SRK romantic breathless tremble)
        if (params.tremoloDepth > 0.01f) {
            applyTremolo(floatSamples, params.tremoloDepth, sampleRate)
        }

        // Step 6: Multi-tap Delay / Jalsa Stadium Echo
        val echoOutput = if (params.delayMs > 10 && params.delayFeedback > 0.01f) {
            applyDelayEcho(floatSamples, params.delayMs, params.delayFeedback, sampleRate)
        } else {
            floatSamples
        }

        // Step 7: Reverb (Schroeder / Freeverb comb filters)
        val reverbOutput = if (params.reverbAmount > 0.01f) {
            applyReverb(echoOutput, params.reverbAmount, sampleRate)
        } else {
            echoOutput
        }

        // Step 8: Mix synthetic ambient background (Jalsa crowd cheering or Cinema strings)
        if (params.backgroundAmbianceType != AmbianceType.NONE && params.backgroundAmbianceVolume > 0.01f) {
            mixAmbiance(reverbOutput, params.backgroundAmbianceType, params.backgroundAmbianceVolume, sampleRate)
        }

        // Normalize & Convert back to 16-bit PCM ShortArray
        return floatToPcm(reverbOutput)
    }

    /**
     * Pitch shifting and time stretching using granular overlap-add (SOLA style).
     */
    private fun pitchShiftAndStretch(
        input: ShortArray,
        semitones: Int,
        tempo: Float,
        sampleRate: Int
    ): ShortArray {
        val pitchFactor = 2.0.pow(semitones.toDouble() / 12.0).toFloat()
        // If no change needed
        if (kotlin.math.abs(pitchFactor - 1.0f) < 0.01f && kotlin.math.abs(tempo - 1.0f) < 0.01f) {
            return input
        }

        val grainMs = 35 // 35ms grain
        val grainSamples = (sampleRate * grainMs / 1000).coerceAtLeast(128)
        val hopInput = grainSamples / 2
        val effectiveTempo = tempo.coerceIn(0.5f, 2.0f)
        val hopOutput = (hopInput / effectiveTempo).toInt().coerceAtLeast(32)

        // Estimate output size
        val estimatedSize = (input.size / effectiveTempo).toInt() + grainSamples * 2
        val outputFloats = FloatArray(max(estimatedSize, input.size))
        val outputWeights = FloatArray(outputFloats.size)

        // Hanning window
        val window = FloatArray(grainSamples) { i ->
            (0.5 * (1.0 - cos(2.0 * PI * i / (grainSamples - 1)))).toFloat()
        }

        var inPos = 0
        var outPos = 0

        while (inPos + grainSamples < input.size && outPos + grainSamples < outputFloats.size) {
            // Read input grain with pitch resampling
            for (i in 0 until grainSamples) {
                val readOffset = (i * pitchFactor).toInt()
                val readIndex = inPos + readOffset
                val sample = if (readIndex in input.indices) {
                    input[readIndex].toFloat() / 32768.0f
                } else {
                    0.0f
                }

                val winVal = window[i]
                val outIdx = outPos + i
                if (outIdx in outputFloats.indices) {
                    outputFloats[outIdx] += sample * winVal
                    outputWeights[outIdx] += winVal
                }
            }

            inPos += hopInput
            outPos += hopOutput
        }

        // Find last valid sample
        val finalLen = max(outPos + grainSamples, 100).coerceAtMost(outputFloats.size)
        val result = ShortArray(finalLen)

        for (i in 0 until finalLen) {
            val weight = outputWeights[i]
            val sample = if (weight > 0.001f) outputFloats[i] / weight else outputFloats[i]
            result[i] = (sample.coerceIn(-1.0f, 1.0f) * 32767.0f).toInt().toShort()
        }

        return result
    }

    /**
     * Megaphone loudspeaker effect: 2-pole IIR bandpass + saturation clipping.
     */
    private fun applyMegaphoneEffect(samples: FloatArray, intensity: Float, sampleRate: Int) {
        val lowCut = 450.0f // Cut low bass
        val highCut = 3200.0f // Cut high treble

        // Simple high-pass filter
        val rcHigh = 1.0f / (2.0f * PI.toFloat() * lowCut)
        val dt = 1.0f / sampleRate
        val alphaHigh = rcHigh / (rcHigh + dt)
        var prevIn = 0.0f
        var prevOutHigh = 0.0f

        // Simple low-pass filter
        val rcLow = 1.0f / (2.0f * PI.toFloat() * highCut)
        val alphaLow = dt / (rcLow + dt)
        var prevOutLow = 0.0f

        val drive = 1.0f + intensity * 4.0f // Mild distortion for megaphone horn grit

        for (i in samples.indices) {
            val current = samples[i]

            // High pass
            val highPassed = alphaHigh * (prevOutHigh + current - prevIn)
            prevIn = current
            prevOutHigh = highPassed

            // Low pass
            val bandPassed = prevOutLow + alphaLow * (highPassed - prevOutLow)
            prevOutLow = bandPassed

            // Megaphone horn saturation (tanh soft clip)
            val saturated = tanh((bandPassed * drive).toDouble()).toFloat()

            // Wet/Dry blend
            samples[i] = (1.0f - intensity) * current + intensity * saturated
        }
    }

    /**
     * Bass boost filter (Low shelf filter).
     */
    private fun applyBassBoost(samples: FloatArray, boostFactor: Float, sampleRate: Int) {
        val cutoff = 180.0f
        val dt = 1.0f / sampleRate
        val rc = 1.0f / (2.0f * PI.toFloat() * cutoff)
        val alpha = dt / (rc + dt)
        var lowState = 0.0f

        val gain = boostFactor.coerceIn(0.2f, 3.0f)

        for (i in samples.indices) {
            val cur = samples[i]
            lowState += alpha * (cur - lowState)
            val boosted = cur + lowState * (gain - 1.0f)
            samples[i] = boosted.coerceIn(-1.0f, 1.0f)
        }
    }

    /**
     * Ring modulation carrier frequency for cyber/robot voice effect.
     */
    private fun applyRingModulation(samples: FloatArray, freqHz: Float, sampleRate: Int) {
        val angularFreq = 2.0 * PI * freqHz / sampleRate
        for (i in samples.indices) {
            val carrier = sin(i * angularFreq).toFloat()
            // 60% wet carrier mix
            samples[i] = samples[i] * (0.4f + 0.6f * carrier)
        }
    }

    /**
     * LFO Tremolo for emotional tremble / vibrato.
     */
    private fun applyTremolo(samples: FloatArray, depth: Float, sampleRate: Int) {
        val rateHz = 5.2f // Subtle emotional vocal flutter rate
        val angularFreq = 2.0 * PI * rateHz / sampleRate

        for (i in samples.indices) {
            val lfo = 1.0f - depth * 0.5f * (1.0f + sin(i * angularFreq).toFloat())
            samples[i] *= lfo
        }
    }

    /**
     * Stadium Rally Delay / Jalsa Echo with multi-tap feedback.
     */
    private fun applyDelayEcho(
        input: FloatArray,
        delayMs: Int,
        feedback: Float,
        sampleRate: Int
    ): FloatArray {
        val delaySamples = (sampleRate * delayMs / 1000).coerceAtLeast(10)
        // Additional secondary tap for stadium acoustics
        val delaySamples2 = (delaySamples * 1.45f).toInt()

        val extraLength = (sampleRate * 1.5f).toInt() // Tail for echoes
        val output = FloatArray(input.size + extraLength)
        val buffer = FloatArray(delaySamples2 + 10)
        var bufIdx = 0

        val fb = feedback.coerceIn(0.0f, 0.75f)

        for (i in output.indices) {
            val inSample = if (i < input.size) input[i] else 0.0f

            // Read delay line 1
            val tap1Idx = (bufIdx - delaySamples + buffer.size) % buffer.size
            val tap1 = buffer[tap1Idx]

            // Read secondary wider stadium tap
            val tap2Idx = (bufIdx - delaySamples2 + buffer.size) % buffer.size
            val tap2 = buffer[tap2Idx]

            val echoSum = tap1 * 0.7f + tap2 * 0.3f
            val newOut = inSample + echoSum * 0.85f

            // Write back with feedback
            buffer[bufIdx] = inSample + echoSum * fb
            bufIdx = (bufIdx + 1) % buffer.size

            output[i] = newOut.coerceIn(-1.0f, 1.0f)
        }

        return output
    }

    /**
     * Freeverb / Schroeder style comb filter reverb for Bollywood cinema acoustics.
     */
    private fun applyReverb(input: FloatArray, amount: Float, sampleRate: Int): FloatArray {
        val reverbWet = amount.coerceIn(0.0f, 0.9f)
        val reverbDry = 1.0f - reverbWet * 0.4f

        // Delay lengths in samples scaled to sample rate
        val combDelays = intArrayOf(
            (sampleRate * 0.0297f).toInt(), // ~30ms
            (sampleRate * 0.0371f).toInt(), // ~37ms
            (sampleRate * 0.0411f).toInt(), // ~41ms
            (sampleRate * 0.0437f).toInt()  // ~44ms
        )
        val combFeedback = 0.76f

        val extraTail = (sampleRate * 1.2f).toInt()
        val output = FloatArray(input.size + extraTail)

        // Setup comb buffers
        val combBuffers = combDelays.map { FloatArray(it) }
        val combIndices = IntArray(combDelays.size)

        for (i in output.indices) {
            val inSample = if (i < input.size) input[i] else 0.0f
            var combSum = 0.0f

            for (c in combDelays.indices) {
                val buf = combBuffers[c]
                val idx = combIndices[c]
                val outComb = buf[idx]
                combSum += outComb
                buf[idx] = inSample + outComb * combFeedback
                combIndices[c] = (idx + 1) % combDelays[c]
            }

            combSum /= combDelays.size
            output[i] = (inSample * reverbDry + combSum * reverbWet).coerceIn(-1.0f, 1.0f)
        }

        return output
    }

    /**
     * Algorithmic soundscape generator for background atmosphere (Jalsa crowd cheers, Cinema drone, Press cameras).
     */
    private fun mixAmbiance(
        samples: FloatArray,
        type: AmbianceType,
        volume: Float,
        sampleRate: Int
    ) {
        val vol = volume.coerceIn(0.0f, 1.0f)

        when (type) {
            AmbianceType.JALSA_CROWD -> {
                // Synthesize cheering crowd roar (filtered pink/brown noise + periodic cheer swells)
                var noiseState = 0.0f
                for (i in samples.indices) {
                    val white = (Math.random() * 2.0 - 1.0).toFloat()
                    noiseState = noiseState * 0.92f + white * 0.08f // Brownish roar
                    val swell = (0.5f + 0.5f * sin(2.0 * PI * i / (sampleRate * 2.5)).toFloat())
                    val crowdSample = noiseState * swell * 0.35f * vol
                    samples[i] = (samples[i] + crowdSample).coerceIn(-1.0f, 1.0f)
                }
            }

            AmbianceType.CINEMA_STRINGS -> {
                // Synthesize warm cinematic pad drone (C minor / D minor harmonic chord tones)
                val f1 = 130.81 // C3
                val f2 = 155.56 // Eb3
                val f3 = 196.00 // G3
                val f4 = 261.63 // C4

                for (i in samples.indices) {
                    val t = i.toDouble() / sampleRate
                    val chord = (
                        sin(2.0 * PI * f1 * t) * 0.35 +
                        sin(2.0 * PI * f2 * t) * 0.25 +
                        sin(2.0 * PI * f3 * t) * 0.25 +
                        sin(2.0 * PI * f4 * t) * 0.15
                    ).toFloat()
                    val pad = chord * 0.22f * vol
                    samples[i] = (samples[i] + pad).coerceIn(-1.0f, 1.0f)
                }
            }

            AmbianceType.PRESS_SHUTTERS -> {
                // Camera shutter clicks at random intervals
                var clickCountdown = (sampleRate * 0.4).toInt()
                for (i in samples.indices) {
                    clickCountdown--
                    if (clickCountdown <= 0) {
                        // Click burst
                        val burstLen = (sampleRate * 0.02).toInt() // 20ms click
                        for (k in 0 until burstLen) {
                            if (i + k < samples.size) {
                                val click = ((Math.random() * 2.0 - 1.0) * exp(-k.toDouble() / 150)).toFloat() * 0.4f * vol
                                samples[i + k] = (samples[i + k] + click).coerceIn(-1.0f, 1.0f)
                            }
                        }
                        clickCountdown = (sampleRate * (0.3 + Math.random() * 0.8)).toInt()
                    }
                }
            }

            AmbianceType.NONE -> {}
        }
    }

    private fun floatToPcm(floats: FloatArray): ShortArray {
        // Find peak to normalize safely
        var maxPeak = 0.01f
        for (f in floats) {
            val abs = kotlin.math.abs(f)
            if (abs > maxPeak) maxPeak = abs
        }

        val scale = if (maxPeak > 0.98f) 0.95f / maxPeak else 1.0f
        val out = ShortArray(floats.size)

        for (i in floats.indices) {
            val clamped = (floats[i] * scale).coerceIn(-1.0f, 1.0f)
            out[i] = (clamped * 32767.0f).toInt().toShort()
        }

        return out
    }

    /**
     * Encodes 16-bit PCM ShortArray to standard RIFF WAV byte array.
     */
    fun pcmToWav(pcmData: ShortArray, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val totalAudioLen = pcmData.size * 2
        val totalDataLen = totalAudioLen + 36
        val byteRate = sampleRate * NUM_CHANNELS * (BITS_PER_SAMPLE / 8)

        val header = ByteArray(44)
        val bb = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN)

        // RIFF header
        bb.put('R'.code.toByte())
        bb.put('I'.code.toByte())
        bb.put('F'.code.toByte())
        bb.put('F'.code.toByte())
        bb.putInt(totalDataLen)
        bb.put('W'.code.toByte())
        bb.put('A'.code.toByte())
        bb.put('V'.code.toByte())
        bb.put('E'.code.toByte())

        // 'fmt ' chunk
        bb.put('f'.code.toByte())
        bb.put('m'.code.toByte())
        bb.put('t'.code.toByte())
        bb.put(' '.code.toByte())
        bb.putInt(16) // Subchunk1Size for PCM
        bb.putShort(1) // AudioFormat (1 = PCM)
        bb.putShort(NUM_CHANNELS.toShort())
        bb.putInt(sampleRate)
        bb.putInt(byteRate)
        bb.putShort((NUM_CHANNELS * BITS_PER_SAMPLE / 8).toShort()) // BlockAlign
        bb.putShort(BITS_PER_SAMPLE.toShort())

        // 'data' chunk
        bb.put('d'.code.toByte())
        bb.put('a'.code.toByte())
        bb.put('t'.code.toByte())
        bb.put('a'.code.toByte())
        bb.putInt(totalAudioLen)

        val out = ByteArrayOutputStream(44 + totalAudioLen)
        out.write(header)

        val dataBytes = ByteArray(totalAudioLen)
        val dataBb = ByteBuffer.wrap(dataBytes).order(ByteOrder.LITTLE_ENDIAN)
        for (s in pcmData) {
            dataBb.putShort(s)
        }
        out.write(dataBytes)

        return out.toByteArray()
    }

    /**
     * Reads a WAV file into 16-bit PCM ShortArray.
     */
    fun readWavFile(file: File): ShortArray {
        if (!file.exists() || file.length() <= 44) return ShortArray(0)

        val bytes = file.readBytes()
        if (bytes.size < 44) return ShortArray(0)

        // Find 'data' chunk
        var dataOffset = 44
        for (i in 0 until bytes.size - 4) {
            if (bytes[i] == 'd'.code.toByte() &&
                bytes[i + 1] == 'a'.code.toByte() &&
                bytes[i + 2] == 't'.code.toByte() &&
                bytes[i + 3] == 'a'.code.toByte()
            ) {
                dataOffset = i + 8
                break
            }
        }

        val pcmByteCount = bytes.size - dataOffset
        if (pcmByteCount <= 0) return ShortArray(0)

        val shortCount = pcmByteCount / 2
        val shorts = ShortArray(shortCount)
        val bb = ByteBuffer.wrap(bytes, dataOffset, pcmByteCount).order(ByteOrder.LITTLE_ENDIAN)

        for (i in 0 until shortCount) {
            shorts[i] = bb.short
        }

        return shorts
    }

    /**
     * Writes ShortArray PCM directly to a WAV file on disk.
     */
    fun saveWavFile(pcmData: ShortArray, targetFile: File, sampleRate: Int = SAMPLE_RATE) {
        val wavBytes = pcmToWav(pcmData, sampleRate)
        targetFile.parentFile?.mkdirs()
        FileOutputStream(targetFile).use { it.write(wavBytes) }
    }
}
