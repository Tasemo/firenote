package de.oelkers.firenote

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton

class NoteDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)
        val editTitle = findViewById<EditText>(R.id.editTitle)
        editTitle.setText(intent.getStringExtra(TITLE_ARG))
        val editContent = findViewById<EditText>(R.id.editContent)
        editContent.setText(intent.getStringExtra(CONTENT_ARG))

        findViewById<ImageButton>(R.id.saveButton).setOnClickListener {
            val result = Intent(intent)
            result.putExtra(TITLE_ARG, editTitle.text.toString())
            result.putExtra(CONTENT_ARG, editContent.text.toString())
            setResult(Activity.RESULT_OK, result)
            finish()
        }

        findViewById<ImageButton>(R.id.deleteButton).setOnClickListener {
            val result = Intent(intent)
            setResult(RESULT_DELETED, result)
            finish()
        }
    }
}
