package com.example.notepad;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.notepad.databinding.ActivityMainBinding;
import com.example.notepad.data.FileManager;

public class MainActivity extends AppCompatActivity {
	private ActivityMainBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
	}

	@Override
	public boolean onCreateOptionsMenu(@NonNull Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.saveMenu:
				Log.e("FF", "saveMenu");
				FileManager.save(this, binding.text.getText().toString());
				break;
			case R.id.loadMenu:
				Log.e("FF", "loadMenu");
				String load = FileManager.load(this);
				binding.text.setText(load);
				break;
			case R.id.clearMenu:
				Log.e("FF", "clearMenu");
				binding.text.setText("");
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}












