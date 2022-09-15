package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.notepad.data.DBManager;
import com.example.notepad.databinding.ActivityNoteBinding;

public class NoteActivity extends AppCompatActivity {
	private ActivityNoteBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityNoteBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		//
		Intent intent = getIntent();
		int id = intent.getIntExtra(DBManager.ID, 0);
		Log.e("FF", String.valueOf(id));
	}
}