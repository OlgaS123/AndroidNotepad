package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;

import com.example.notepad.adapters.NotesAdapterCursor;
import com.example.notepad.data.DBManager;
import com.example.notepad.databinding.ActivityAllNotesRecyclerBinding;
import com.example.notepad.models.Note;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class AllNotesRecyclerActivity extends AppCompatActivity {
	private ActivityAllNotesRecyclerBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityAllNotesRecyclerBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		//
		List<Note> list = Arrays.asList(
			new Note("aa", LocalDateTime.now(), "aaa"),
			new Note("bb", LocalDateTime.now(), "bbb"),
			new Note("cc", LocalDateTime.now(), "ccc")
		);
		DBManager manager = new DBManager(this);
		for (Note note : list) {
			manager.insert(note);
		}
		Cursor cursor = manager.findAllToCursor();
		//1
		/*NotesAdapterCollection adapter = new NotesAdapterCollection(this, list);
		binding.notesRecycler.setAdapter(adapter);*/
		//1 LayoutManager
		/*binding.notesRecycler.setLayoutManager(
			new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));*/
		/*binding.notesRecycler.setLayoutManager(
			new GridLayoutManager(this, 2, RecyclerView.HORIZONTAL, false));*/
		/*binding.notesRecycler.setLayoutManager(
			new LinearLayoutManager(this));*/
		//2
		NotesAdapterCursor adapter = new NotesAdapterCursor(this, cursor);
		binding.notesRecycler.setAdapter(adapter);
	}
}