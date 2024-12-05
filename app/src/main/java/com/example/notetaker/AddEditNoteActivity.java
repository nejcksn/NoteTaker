package com.example.notetaker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditNoteActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextContent;
    private NoteDatabaseHelper dbHelper;
    private int noteId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextContent = findViewById(R.id.edit_text_content);
        dbHelper = new NoteDatabaseHelper(this);

        if (getIntent().hasExtra("noteId")) {
            noteId = getIntent().getIntExtra("noteId", -1);
            Note note = findNoteById(noteId);
            if (note != null) {
                editTextTitle.setText(note.getTitle());
                editTextContent.setText(note.getContent());
            }
        }

        Button buttonSave = findViewById(R.id.button_save);
        buttonSave.setOnClickListener(v -> saveNote());
    }

    private Note findNoteById(int noteId) {
        for (Note note : dbHelper.getAllNotes()) {
            if (note.getId() == noteId) {
                return note;
            }
        }
        return null;
    }

    private void saveNote() {
        String title = editTextTitle.getText().toString().trim();
        String content = editTextContent.getText().toString().trim();

        if (noteId == -1) {
            dbHelper.addNote(title, content);
        } else {
            Note note = new Note(noteId, title, content);
            dbHelper.updateNote(note);
        }

        finish();
    }
}
