package de.oelkers.firenote.controllers.detail

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import de.oelkers.firenote.R
import de.oelkers.firenote.controllers.alarm.NoteAlarmActivity
import de.oelkers.firenote.models.Note
import de.oelkers.firenote.util.AppBarActivity
import java.time.LocalDateTime
import java.util.*

const val ARG_NOTE = "ARG_NOTE"
const val RESULT_DELETED = AppCompatActivity.RESULT_FIRST_USER + 1

class NoteDetailsActivity : AppBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)
        val editTitle = findViewById<EditText>(R.id.editTitle)
        val editContent = findViewById<EditText>(R.id.editContent)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val deleteButton = findViewById<Button>(R.id.deleteButton)
        val alarmButton = findViewById<Button>(R.id.alarmButton)
        val recordButton = findViewById<AudioRecorderButton>(R.id.recordButton)
        val playButton = findViewById<AudioPlayerButton>(R.id.playButton)
        val note = intent.getParcelableExtra(ARG_NOTE) ?: Note(UUID.randomUUID().toString(), created = LocalDateTime.now())
        editTitle.setText(note.title)
        editContent.setText(note.content)
        setButtonVisibility(note, playButton, recordButton)
        alarmButton.setOnClickListener { onAlarmClick() }
        saveButton.setOnClickListener {
            val result = Intent(intent)
            note.title = editTitle.text.toString()
            note.content = editContent.text.toString()
            note.audioPath = recordButton.audioFile ?: note.audioPath
            result.putExtra(ARG_NOTE, note as Parcelable)
            setResult(RESULT_OK, result)
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
