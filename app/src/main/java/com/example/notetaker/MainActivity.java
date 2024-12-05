package com.example.notetaker;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private NoteDatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private EditText editTextSearch;
    private String searchQuery = ""; // Для хранения текста поиска

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new NoteDatabaseHelper(this);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NoteAdapter(new ArrayList<>(), new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                intent.putExtra("noteId", note.getId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Note note) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Удалить заметку?")
                        .setMessage("Вы уверены, что хотите удалить эту заметку?")
                        .setPositiveButton("Да", (dialog, which) -> {
                            dbHelper.deleteNote(note.getId());
                            loadNotes(); // Перезагружаем заметки
                            filterNotes(searchQuery); // Применяем фильтр после удаления
                        })
                        .setNegativeButton("Нет", null)
                        .show();
            }
        }, MainActivity.this);

        recyclerView.setAdapter(adapter);
        loadNotes();

        editTextSearch = findViewById(R.id.edit_text_search);

        // Добавляем TextWatcher для поиска заметок
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString(); // Сохраняем текущий текст поиска
                filterNotes(searchQuery); // Применяем фильтрацию
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Очистка поля поиска при нажатии на кнопку добавления заметки
        Button buttonAddNote = findViewById(R.id.button_add_note);
        buttonAddNote.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddEditNoteActivity.class));
        });

        // Восстановление текста поиска при возвращении на экран
        if (savedInstanceState != null) {
            searchQuery = savedInstanceState.getString("searchQuery", "");
            editTextSearch.setText(searchQuery);
            filterNotes(searchQuery); // Применяем фильтр при восстановлении
        }
    }

    // Сохранение состояния текста поиска при смене экрана
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("searchQuery", searchQuery);
    }

    // Метод для загрузки и фильтрации заметок
    private void loadNotes() {
        adapter.setNotes(dbHelper.getAllNotes());
        filterNotes(searchQuery); // Применяем фильтр после загрузки заметок
    }

    // Фильтрация заметок на основе текста поиска
    private void filterNotes(String query) {
        adapter.filterNotes(query); // Фильтруем заметки через адаптер
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes(); // Загружаем и фильтруем заметки при возвращении
    }
}

