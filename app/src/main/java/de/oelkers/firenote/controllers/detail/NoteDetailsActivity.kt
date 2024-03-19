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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)
        val editTitle = findViewById<EditText>(R.id.editTitle)
        val editContent = findViewById<EditText>(R.id.editContent)
        val saveButton = findViewById<ImageButton>(R.id.saveButton)
        val deleteButton = findViewById<ImageButton>(R.id.deleteButton)
        val alarmButton = findViewById<ImageButton>(R.id.alarmButton)
        val recordButton = findViewById<AudioRecorderButton>(R.id.recordButton)
        val playButton = findViewById<AudioPlayerButton>(R.id.playButton)
        val note = intent.getParcelableExtra(NOTE_ARG) ?: Note(UUID.randomUUID().toString(), created = LocalDateTime.now())
        editTitle.setText(note.title)
        editContent.setText(note.content)
        setButtonVisibility(note, playButton, recordButton)
        alarmButton.setOnClickListener { onAlarmClick() }
        saveButton.setOnClickListener {
            val result = Intent(intent)
            note.title = editTitle.text.toString()
            note.content = editContent.text.toString()
            note.audioPath = recordButton.audioFile ?: note.audioPath
            result.putExtra(NOTE_ARG, note as Parcelable)
            setResult(Activity.RESULT_OK, result)
            finish()
        }
        deleteButton.setOnClickListener {
            val result = Intent(intent)
            setResult(RESULT_DELETED, result)
            finish()
        }
        recordButton.registerListener(this, note.audioPath)
        playButton.registerListener { recordButton.audioFile ?: note.audioPath!! }
    }

    private fun setButtonVisibility(note: Note, playButton: AudioPlayerButton, recordButton: AudioRecorderButton) {
        if (note.audioPath == null) {
            playButton.visibility = View.GONE
        }
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (preferences.getBoolean("allow_voice_messages", false)) {
            playButton.visibility = View.GONE
            recordButton.visibility = View.GONE
        }
    }

    private fun onAlarmClick() {
        val intent = Intent(this, NoteAlarmActivity::class.java)
        startActivity(intent)
    }
}
