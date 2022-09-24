package com.example.notepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.ParcelableSpan;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.notepad.data.DBManager;
import com.example.notepad.data.FileManager;
import com.example.notepad.databinding.ActivityNoteBinding;
import com.example.notepad.models.Note;

import java.time.LocalDateTime;

public class NoteActivity extends AppCompatActivity {
	private ActivityNoteBinding binding;
	private DBManager dbManager;
	private Integer noteId;
	private AlertDialog alertDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityNoteBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		//
		Intent intent = getIntent();
		dbManager=new DBManager(this);
		//
		noteId = intent.getIntExtra(DBManager.ID, 0);
		Note fromFile = (Note)intent.getSerializableExtra("NoteFromFile");
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
		//
		registerForContextMenu(binding.styleNote);
		registerForContextMenu(binding.colorNote);
		registerForContextMenu(binding.clearNote);
		binding.backNote.setOnClickListener(v->{createPopUpMenu(binding.backNote);});
		//
		alertDialog = new AlertDialog.Builder(this)
				.setCancelable(true)
				.setTitle("Saving")
				.setMessage("Save?")
				.setNegativeButton("Cancel", (dialog, which)->{
					Toast.makeText(this, "Canceled", Toast.LENGTH_LONG).show();
					setResult(RESULT_CANCELED);
					super.onBackPressed();
				})
				.setPositiveButton("Save",(dialog, which)->{
					Note note = new Note(binding.titleNote.getText().toString(), LocalDateTime.now(), binding.textNote.getText().toString());
					dbManager.insert(note);
					Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
					setResult(RESULT_OK, new Intent().putExtra("test","test result"));
					super.onBackPressed();
				}).create();
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

	@Override
	public void onBackPressed() {
		//setResultAndClose();
		//super.onBackPressed();
		alertDialog.show();
	}

	public void setSpan(ParcelableSpan span){
		SpannableString spannableString = new SpannableString(binding.textNote.getText());
		spannableString.setSpan(span,binding.textNote.getSelectionStart(),binding.textNote.getSelectionEnd(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		binding.textNote.setText(spannableString);
	}

	public void delSpan(Class spanClass, boolean clearFullString){
		SpannableString spannableString = new SpannableString(binding.textNote.getText());
		ParcelableSpan[] spans;
		if(clearFullString)
			spans = (ParcelableSpan[]) spannableString.getSpans(0,spannableString.length(), spanClass);
		else
			spans = (ParcelableSpan[]) spannableString.getSpans(binding.textNote.getSelectionStart(),binding.textNote.getSelectionEnd(), spanClass);

		for(ParcelableSpan ps:spans){
			spannableString.removeSpan(ps);
		}
		String html = Html.toHtml(spannableString, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);
		Log.e("FF",html);
		binding.textNote.setText(spannableString);
	}

	/*private void setResultAndClose(){
		alertDialog.show();
	}*/

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		switch (v.getId()){
			case R.id.styleNote:
				menu.setHeaderTitle("Style");
				getMenuInflater().inflate(R.menu.text_style_menu, menu);
				break;
			case R.id.colorNote:
				menu.setHeaderTitle("Color");
				getMenuInflater().inflate(R.menu.text_color_menu, menu);
				break;
			case R.id.clearNote:
				menu.setHeaderTitle("Clear");
				getMenuInflater().inflate(R.menu.text_clear_style_menu, menu);
				break;
		}

	}

	@Override
	public boolean onContextItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()){
			case R.id.normalMenu:
				delSpan(StyleSpan.class, false);
				break;
			case R.id.boldMenu:
				setSpan(new StyleSpan(Typeface.BOLD));
				break;
			case R.id.italicMenu:
				setSpan(new StyleSpan(Typeface.ITALIC));
				break;
			case R.id.boldItalicMenu:
				setSpan(new StyleSpan(Typeface.BOLD_ITALIC));
				break;
			//===================================
			case R.id.blackColorMenu:
				//setSpan(new StyleSpan(Typeface.NORMAL));
				delSpan(ForegroundColorSpan.class, false);
				break;
			case R.id.redColorMenu:
				setSpan(new ForegroundColorSpan(Color.RED));
				break;
			//===================================
			case R.id.allMenu:
				delSpan(StyleSpan.class, true);
				delSpan(ForegroundColorSpan.class, true);
				delSpan(BackgroundColorSpan.class, true);
				break;
			case R.id.selectionMenu:
				delSpan(StyleSpan.class, false);
				delSpan(ForegroundColorSpan.class, false);
				delSpan(BackgroundColorSpan.class, false);
				break;
		}
		return super.onContextItemSelected(item);
	}

	public void createPopUpMenu(View view){
		PopupMenu popupMenu = new PopupMenu(this, view);
		popupMenu.inflate(R.menu.text_color_menu);
		popupMenu.setOnMenuItemClickListener(item -> {
			switch (item.getItemId()){
				case R.id.blackColorMenu:
					delSpan(BackgroundColorSpan.class, false);
					break;
				case R.id.redColorMenu:
					setSpan(new BackgroundColorSpan(Color.RED));
					break;
			}
			return true;
		});
		popupMenu.show();
	}
}