package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.RecordingRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("MimicVoice", appName)
  }

  @Test
  fun `repository saves and retrieves recording`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = RecordingRepository(context)

    val dummyPcm = ShortArray(1000) { 100 }
    val saved = repo.saveRecording(
      title = "Test Speech",
      speakerName = "Imran Khan",
      speakerEmoji = "🏏",
      effectTitle = "Jalsa Rally Echo",
      textOrScript = "Ghabrana nahi hai",
      pcmData = dummyPcm,
      isTtsGenerated = true
    )

    assertNotNull(saved)
    assertEquals("Test Speech", saved.title)
    assertEquals("Imran Khan", saved.speakerName)

    val list = repo.recordings.value
    assertEquals(1, list.size)

    repo.deleteRecording(saved.id)
    assertEquals(0, repo.recordings.value.size)
  }
}
