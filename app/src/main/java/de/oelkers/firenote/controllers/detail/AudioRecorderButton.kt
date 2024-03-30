package de.oelkers.firenote.controllers.detail

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.media.MediaRecorder
import android.util.AttributeSet
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import de.oelkers.firenote.R
import de.oelkers.firenote.persistence.AUDIO_DIRECTORY
import java.util.*

class AudioRecorderButton : MaterialButton {

    private var isRecording: Boolean = false
    private var initialBackground: ColorStateList? = null
    private lateinit var recorder: MediaRecorder
    var audioFile: String? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun registerListener(activity: ComponentActivity, audioFile: String?) {
        val permissionLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                recordAudio(audioFile)
            } else {
                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                builder.setMessage("Access to microphone is required for voice notes").setTitle("Feature Unavailable")
                builder.create().show()
            }
        }
        setOnClickListener { onRecordClick(audioFile, permissionLauncher) }
    }

    private fun onRecordClick(fileName: String?, launcher: ActivityResultLauncher<String>) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            recordAudio(fileName)
        } else {
            launcher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun recordAudio(fileName: String?) {
        if (isRecording) {
            recorder.apply {
                stop()
                release()
            }
            onRecordingStopped()
        } else {
            audioFile = fileName ?: context.filesDir.resolve(AUDIO_DIRECTORY).resolve("note-audio-${UUID.randomUUID()}.3gp").absolutePath
            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(audioFile)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                prepare()
                start()
            }
            onRecordingStarted()
        }
    }

    private fun onRecordingStarted() {
        initialBackground = backgroundTintList
        backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorSecondary, null))
        isRecording = true
    }

    private fun onRecordingStopped() {
        backgroundTintList = initialBackground
        isRecording = false
    }
}
