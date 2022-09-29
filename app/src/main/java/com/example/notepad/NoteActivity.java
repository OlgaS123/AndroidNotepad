package com.example.notepad;

import static android.text.Html.FROM_HTML_MODE_COMPACT;
import static android.text.Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL;

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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.example.notepad.data.DBManager;
import com.example.notepad.data.FileManager;
import com.example.notepad.databinding.ActivityNoteBinding;
import com.example.notepad.models.Note;

import java.time.LocalDateTime;

public class NoteActivity extends AppCompatActivity {
	private ActivityNoteBinding binding;
	private DBManager manager;
	private AlertDialog alertDialog;
	Integer noteId;
	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityNoteBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		//
		manager = new DBManager(this);
		//
		intent = getIntent();
		noteId = intent.getIntExtra(DBManager.ID, 0);
		Note fromFile = (Note)intent.getSerializableExtra("NoteFromFile");
		Log.e("FF", String.valueOf(noteId));
		//
		if (noteId != 0) {
			Note note = manager.findById(noteId);
			binding.titleNote.setText(note.getTitle());
			binding.timeNote.setText(note.getTime().toString());
			SpannableString spannableString = new SpannableString(Html.fromHtml(note.getText(),FROM_HTML_MODE_COMPACT));
			binding.textNote.setText(spannableString);
		}
		else if(fromFile!=null){
			binding.titleNote.setText(fromFile.getTitle());
			binding.timeNote.setText(fromFile.getFormattedTime());
			SpannableString spannableString = new SpannableString(Html.fromHtml(fromFile.getText(),FROM_HTML_MODE_COMPACT));
			binding.textNote.setText(spannableString);

		}
		//
		binding.styleNote.setOnClickListener(v->{createPopUpMenu(binding.styleNote);});
		binding.colorNote.setOnClickListener(v->{createPopUpMenu(binding.colorNote);});
		binding.clearNote.setOnClickListener(v->{createPopUpMenu(binding.clearNote);});
		binding.backNote.setOnClickListener(v->{createPopUpMenu(binding.backNote);});
		//

		alertDialog = new AlertDialog.Builder(this)
				.setCancelable(true)
				.setTitle("Saving")
				.setMessage("Save Changes?")
				.setNegativeButton("Cancel", (dialog, which) -> {
					setResult(RESULT_CANCELED);
					super.onBackPressed();
				})
				.setPositiveButton("Save", (dialog, which) -> {
					saveNoteToDb();
				})
				.create();
	}

	public void saveNoteToDb(){
		Note noteSave = new Note(
				binding.titleNote.getText().toString(),
				LocalDateTime.now(),
				Html.toHtml(new SpannableString(binding.textNote.getText()),
						TO_HTML_PARAGRAPH_LINES_INDIVIDUAL)
		);
		//insert/update
		Integer idSave = 0;
		Intent intentSave = new Intent();
		int position = getIntent().getIntExtra("position", -1);
		if (position < 0){
			idSave = manager.insert(noteSave);
			intentSave.putExtra("create", true);
		}
		else{
			idSave=noteId;
			noteSave.setId(idSave);
			manager.update(noteSave);
			intentSave.putExtra("position", position);
		}
		intentSave.putExtra(DBManager.ID, idSave);
		setResult(RESULT_OK, intentSave);
		super.onBackPressed();
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
				saveNoteToDb();
				break;
			case R.id.saveFileMenu:
				FileManager.save(this, new Note(
						binding.titleNote.getText().toString(),
						LocalDateTime.now(),
						Html.toHtml(new SpannableString(binding.textNote.getText()), TO_HTML_PARAGRAPH_LINES_INDIVIDUAL)));
				break;
			case R.id.clearMenu:
				binding.textNote.setText("");
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		alertDialog.show();
	}

	public void createPopUpMenu(View view){
		PopupMenu popupMenu = new PopupMenu(this, view);
		switch (view.getId()) {
			case R.id.styleNote:
				popupMenu.inflate(R.menu.text_style_menu);
				break;
			case R.id.colorNote:
				popupMenu.inflate(R.menu.text_color_menu);
				break;
			case R.id.backNote:
				popupMenu.inflate(R.menu.bg_color_menu);
				break;
			case R.id.clearNote:
				popupMenu.inflate(R.menu.text_clear_style_menu);
				break;
		}
		popupMenu.setOnMenuItemClickListener(item -> {
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
				case R.id.noneBgMenu:
					delSpan(BackgroundColorSpan.class, false);
					break;
				case R.id.redBgMenu:
					setSpan(new BackgroundColorSpan(Color.RED));
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
			return true;
		});
		popupMenu.show();
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
}