package com.example.notetaker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> notes;
    private List<Note> allNotes; // Сохраняем оригинальный список
    private OnItemClickListener listener;
    private Context context; // Контекст

    public interface OnItemClickListener {
        void onItemClick(Note note);
        void onDeleteClick(Note note); // Метод для удаления
    }

    public NoteAdapter(List<Note> notes, OnItemClickListener listener, Context context) {
        this.notes = notes;
        this.allNotes = new ArrayList<>(notes); // Сохраняем оригинальный список
        this.listener = listener;
        this.context = context;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTitle;
        public TextView textViewContent;
        public Button buttonDelete;

        public NoteViewHolder(@NonNull View itemView, final OnItemClickListener listener, final List<Note> notes) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewContent = itemView.findViewById(R.id.text_view_content);
            buttonDelete = itemView.findViewById(R.id.button_delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(notes.get(position));
                    }
                }
            });

            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(notes.get(position));
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view, listener, notes);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note currentNote = notes.get(position);
        holder.textViewTitle.setText(currentNote.getTitle());
        holder.textViewContent.setText(currentNote.getContent());

        // Чередование фонов
        holder.itemView.setBackgroundColor(
                position % 2 == 0 ? 0xFFE0E0E0 : 0xFFFFFFFF
        );
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        this.allNotes = new ArrayList<>(notes); // Обновляем оригинальный список
        notifyDataSetChanged();
    }

    public void filterNotes(String query) {
        if (query.isEmpty()) {
            notes = allNotes;
        } else {
            List<Note> filteredList = new ArrayList<>();
            for (Note note : allNotes) {
                if (note.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        note.getContent().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(note);
                }
            }
            notes = filteredList;
        }
        notifyDataSetChanged();
    }
}
