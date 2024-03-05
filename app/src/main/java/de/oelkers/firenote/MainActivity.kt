package de.oelkers.firenote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.oelkers.firenote.models.Note

class MainActivity : AppCompatActivity() {

    private val notes = arrayListOf(
        Note("Note1", "Hello World!"),
        Note("SuperLong", "This is a super long note where the text does not fit into one line!"),
        Note("AnotherNote", "How many notes are you writing?")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val newNoteButton = findViewById<FloatingActionButton>(R.id.newNoteButton)
        newNoteButton.setOnClickListener { onNewNoteClick() }
        val notesView = findViewById<RecyclerView>(R.id.notesView)
        notesView.adapter = NoteAdapter(notes)
        println(notes.size)
    }

    private fun onNewNoteClick() {
        println("YES!")
    }
}