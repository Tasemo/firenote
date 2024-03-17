package de.oelkers.firenote.controllers.detail

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import de.oelkers.firenote.R
import de.oelkers.firenote.controllers.alarm.NoteAlarmActivity
import de.oelkers.firenote.controllers.overview.NOTE_ARG
import de.oelkers.firenote.controllers.overview.RESULT_DELETED
import de.oelkers.firenote.models.Note
import de.oelkers.firenote.persistence.AUDIO_DIRECTORY
import de.oelkers.firenote.util.AppBarActivity
import java.time.LocalDateTime
import java.util.*

class NoteDetailsActivity : AppBarActivity() {

    private var isRecording: Boolean = false
    private lateinit var recorder: MediaRecorder
    private var isPlaying: Boolean = false
    private lateinit var player: MediaPlayer
    private lateinit var playButton: ImageButton
    private lateinit var recordButton: ImageButton
    private lateinit var recordButtonBackground: Drawable
    private var audioFile: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)
        val editTitle = findViewById<EditText>(R.id.editTitle)
        val editContent = findViewById<EditText>(R.id.editContent)
        val saveButton = findViewById<ImageButton>(R.id.saveButton)
        val deleteButton = findViewById<ImageButton>(R.id.deleteButton)
        val alarmButton = findViewById<ImageButton>(R.id.alarmButton)
        recordButton = findViewById(R.id.recordButton)
        playButton = findViewById(R.id.playButton)
        val note = intent.getParcelableExtra(NOTE_ARG) ?: Note(UUID.randomUUID().toString(), created = LocalDateTime.now())
        editTitle.setText(note.title)
        editContent.setText(note.content)
        audioFile = note.audioPath
        if (audioFile == null) {
            playButton.visibility = View.GONE
        }
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (preferences.getBoolean("allow_voice_messages", false)) {
            playButton.visibility = View.GONE
            recordButton.visibility = View.GONE
        }
        alarmButton.setOnClickListener { onAlarmClick() }
        saveButton.setOnClickListener {
            val result = Intent(intent)
            note.title = editTitle.text.toString()
            note.content = editContent.text.toString()
            note.audioPath = audioFile
            result.putExtra(NOTE_ARG, note as Parcelable)
            setResult(Activity.RESULT_OK, result)
            finish()
        }
        deleteButton.setOnClickListener {
            val result = Intent(intent)
            setResult(RESULT_DELETED, result)
            finish()
        }

        val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                recordAudio(audioFile)
            } else {
                val builder: AlertDialog.Builder = AlertDialog.Builder(applicationContext)
                builder.setMessage("Access to microphone is required for voice notes").setTitle("Feature Unavailable")
                builder.create().show()
            }
        }
        recordButton.setOnClickListener { onRecordClick(baseContext, audioFile, permissionLauncher) }
        playButton.setOnClickListener { playAudio(audioFile!!) }
    }

    private fun onAlarmClick() {
        val intent = Intent(this, NoteAlarmActivity::class.java)
        startActivity(intent)
    }

    private fun onRecordClick(context: Context, fileName: String?, launcher: ActivityResultLauncher<String>) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            recordAudio(fileName)
        } else {
            launcher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun recordAudio(fileName: String?) {
        audioFile = fileName ?: baseContext.filesDir.resolve(AUDIO_DIRECTORY).resolve("note-audio-${UUID.randomUUID()}.3gp").absolutePath
        if (isRecording) {
            recorder.apply {
                stop()
                release()
            }
            onRecordingStopped()
        } else {
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

    private fun playAudio(fileName: String) {
        if (isPlaying) {
            player.stop()
            player.release()
            onAudioCompleted()
        } else {
            player = MediaPlayer().apply {
                setDataSource(fileName)
                setOnCompletionListener { onAudioCompleted() }
                prepare()
                start()
            }
            onAudioStarted()
        }
    }

    private fun onRecordingStarted() {
        recordButtonBackground = recordButton.background
        recordButton.setBackgroundColor(getColor(android.R.color.holo_red_dark))
        isRecording = true
    }

    private fun onRecordingStopped() {
        recordButton.background = recordButtonBackground
        isRecording = false
    }

    private fun onAudioStarted() {
        playButton.setImageResource(android.R.drawable.ic_media_pause)
        isPlaying = true
    }

    private fun onAudioCompleted() {
        playButton.setImageResource(android.R.drawable.ic_media_play)
        isPlaying = false
    }
}
