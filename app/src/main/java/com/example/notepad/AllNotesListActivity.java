package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notepad.data.DBManager;
import com.example.notepad.databinding.ActivityAllNotesListBinding;
import com.example.notepad.models.Note;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class AllNotesListActivity extends AppCompatActivity {
	private ActivityAllNotesListBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityAllNotesListBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		//1
		/*String[] ss = {"aa", "bb", "cc"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
			this, android.R.layout.simple_list_item_1, ss);
		binding.notesList.setAdapter(adapter);
		binding.notesList.setOnItemClickListener((parent, view, position, id) -> {
			Log.e("FF", parent + " " + view + " " + position + " " + id);
			Toast.makeText(this, ((TextView)view).getText(),Toast.LENGTH_LONG).show();
		});*/
		//2
		/*List<Note> list = Arrays.asList(
			new Note("aa", LocalDateTime.now(), "aaa"),
			new Note("bb", LocalDateTime.now(), "bbb"),
			new Note("cc", LocalDateTime.now(), "ccc")
		);
		ArrayAdapter<Note> adapter = new ArrayAdapter<>(
			this, android.R.layout.simple_list_item_1, list);
		binding.notesList.setAdapter(adapter);*/
		//3
		List<Note> list = Arrays.asList(
			new Note("aa", LocalDateTime.now(), "aaa"),
			new Note("bb", LocalDateTime.now(), "bbb"),
			new Note("cc", LocalDateTime.now(), "ccc")
		);
		DBManager manager = new DBManager(this);
		for (Note note : list) {
			manager.insert(note);
		}
		manager.findAllToList().forEach(System.out::println);
		//4
		/*Cursor cursor = manager.findAllToCursor();
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(
			this, android.R.layout.simple_list_item_2, cursor,
			new String[]{DBManager.TITLE, DBManager.TIME},
			new int[]{android.R.id.text1, android.R.id.text2},
			0
		);
		binding.notesList.setAdapter(adapter);*/
		//
		//Log.e("FF", manager.findById(1).toString());
		//5
		Cursor cursor = manager.findAllToCursor();
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(
			this, R.layout.notes_element, cursor,
			new String[]{DBManager.TITLE, DBManager.TIME},
			new int[]{R.id.text1, R.id.text2},
			0
		);
		binding.notesList.setAdapter(adapter);
	}
}