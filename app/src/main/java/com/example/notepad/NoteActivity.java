package com.example.notepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.notepad.data.DBManager;
import com.example.notepad.data.FileManager;
import com.example.notepad.databinding.ActivityNoteBinding;
import com.example.notepad.models.Note;

import java.time.LocalDateTime;

public class NoteActivity extends AppCompatActivity {
	private ActivityNoteBinding binding;
	private DBManager dbManager;
	private Integer noteId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityNoteBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		//
		Intent intent = getIntent();
		noteId = intent.getIntExtra(DBManager.ID, 0);
		Note fromFile = (Note)intent.getSerializableExtra("NoteFromFile");
		dbManager=new DBManager(this);
		//Log.e("FF", String.valueOf(noteId));
		if(noteId!=0){
			Note note = dbManager.findById(noteId);
			binding.titleNote.setText(note.getTitle());
			binding.timeNote.setText(note.getFormattedTime());
			binding.textNote.setText(note.getText());
		}
		else if(fromFile!=null){
			binding.titleNote.setText(fromFile.getTitle());
			binding.timeNote.setText(fromFile.getFormattedTime());
			binding.textNote.setText(fromFile.getText());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(@NonNull Menu menu) {
		getMenuInflater().inflate(R.menu.note_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.saveDbMenu:
				if(noteId != 0)
					dbManager.update(new Note(noteId, binding.titleNote.getText().toString(), LocalDateTime.now(), binding.textNote.getText().toString()));
				else
					dbManager.insert(new Note(binding.titleNote.getText().toString(), LocalDateTime.now(), binding.textNote.getText().toString()));
				break;
			case R.id.saveFileMenu:
				FileManager.save(this, new Note(binding.titleNote.getText().toString(), LocalDateTime.now(), binding.textNote.getText().toString()));
				break;
			case R.id.clearMenu:
				binding.textNote.setText("");
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}