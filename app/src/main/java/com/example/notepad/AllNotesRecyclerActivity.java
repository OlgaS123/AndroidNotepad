package com.example.notepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.notepad.adapters.NotesAdapterCursor;
import com.example.notepad.data.DBManager;
import com.example.notepad.data.FileManager;
import com.example.notepad.databinding.ActivityAllNotesRecyclerBinding;
import com.example.notepad.models.Note;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class AllNotesRecyclerActivity extends AppCompatActivity {
	private ActivityAllNotesRecyclerBinding binding;
	DBManager manager;

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
		manager = new DBManager(this);
		manager.dropTab();
		manager.createTab();
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

	@Override
	protected void onResume() {
		Cursor cursor = manager.findAllToCursor();
		NotesAdapterCursor adapter = new NotesAdapterCursor(this, cursor);
		binding.notesRecycler.setAdapter(adapter);
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(@NonNull Menu menu) {
		getMenuInflater().inflate(R.menu.note_list_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.newNoteMenu:
				Intent intent = new Intent(this, NoteActivity.class);
				startActivity(intent);
				break;
			case R.id.loadFileMenu:
				Note note = FileManager.load(this);
				if(note!=null){
					Intent intent1 = new Intent(this, NoteActivity.class);
					intent1.putExtra("NoteFromFile", note);
					startActivity(intent1);
				}
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}