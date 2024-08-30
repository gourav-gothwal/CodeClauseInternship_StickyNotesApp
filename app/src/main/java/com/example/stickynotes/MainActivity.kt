package com.example.stickynotes

import NoteAdapter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

class MainActivity : AppCompatActivity() {
    private val notes = mutableListOf<Note>()
    private lateinit var adapter: NoteAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var noteCountTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        noteCountTextView = findViewById(R.id.noteCountTextView)
        adapter = NoteAdapter(notes) { note, position -> showUpdateDeleteDialog(note, position) }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            showAddNoteDialog()
        }

        val layoutSwitch: Switch = findViewById(R.id.layoutSwitch)
        layoutSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                recyclerView.layoutManager = GridLayoutManager(this, 2)
                layoutSwitch.text = "Vertical View"
            } else {
                recyclerView.layoutManager = LinearLayoutManager(this)
                layoutSwitch.text = "Grid View"
            }
        }
        updateNoteCount()
    }

    private fun showAddNoteDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_note, null)
        val titleEditText: EditText = dialogView.findViewById(R.id.titleEditText)
        val contentEditText: EditText = dialogView.findViewById(R.id.contentEditText)
        val colorPickerButton: FloatingActionButton = dialogView.findViewById(R.id.colorPickerButton)

        var selectedColor = 0xFFFFFFFF.toInt()

        colorPickerButton.setOnClickListener {
            ColorPickerDialog.Builder(this)
                .setTitle("Pick Note Color")
                .setPositiveButton("Select", ColorEnvelopeListener { envelope, _ ->
                    selectedColor = envelope.color
                    colorPickerButton.setBackgroundColor(selectedColor)
                })
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .attachAlphaSlideBar(true)
                .attachBrightnessSlideBar(true)
                .show()
        }

        AlertDialog.Builder(this)
            .setTitle("Add Note")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val title = titleEditText.text.toString()
                val content = contentEditText.text.toString()
                if (title.isNotEmpty() && content.isNotEmpty()) {
                    notes.add(Note(title, content, selectedColor))
                    adapter.notifyDataSetChanged()
                    updateNoteCount()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showUpdateDeleteDialog(note: Note, position: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_note, null)
        val titleEditText: EditText = dialogView.findViewById(R.id.titleEditText)
        val contentEditText: EditText = dialogView.findViewById(R.id.contentEditText)
        val colorPickerButton: FloatingActionButton = dialogView.findViewById(R.id.colorPickerButton)

        titleEditText.setText(note.title)
        contentEditText.setText(note.content)
        var selectedColor = note.color
        colorPickerButton.setBackgroundColor(selectedColor)

        colorPickerButton.setOnClickListener {
            ColorPickerDialog.Builder(this)
                .setTitle("Pick Note Color")
                .setPositiveButton("Select", ColorEnvelopeListener { envelope, _ ->
                    selectedColor = envelope.color
                    colorPickerButton.setBackgroundColor(selectedColor)
                })
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .attachAlphaSlideBar(true) // Optional
                .attachBrightnessSlideBar(true) // Optional
                .show()
        }

        AlertDialog.Builder(this)
            .setTitle("Update or Delete Note")
            .setView(dialogView)
            .setPositiveButton("Update") { dialog, _ ->
                val title = titleEditText.text.toString()
                val content = contentEditText.text.toString()
                if (title.isNotEmpty() && content.isNotEmpty()) {
                    notes[position] = Note(title, content, selectedColor)
                    adapter.notifyItemChanged(position)
                    updateNoteCount()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Delete") { dialog, _ ->
                notes.removeAt(position)
                adapter.notifyItemRemoved(position)
                updateNoteCount()
                dialog.dismiss()
            }
            .setNeutralButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun updateNoteCount() {
        noteCountTextView.text = "Notes: ${notes.size}"
    }
}
