package com.example.notepad;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ClipDescription;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.notepad.adapters.NotesAdapterCursor;
import com.example.notepad.data.DBManager;
import com.example.notepad.data.FileManager;
import com.example.notepad.databinding.ActivityAllNotesRecyclerBinding;
import com.example.notepad.models.Note;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class AllNotesRecyclerActivity extends AppCompatActivity {
	private ActivityAllNotesRecyclerBinding binding;
	private ActivityResultLauncher<Intent> resultLauncher;
	private ActivityResultLauncher<String> createDocLauncher;
	private ActivityResultLauncher<String> requestPermissionLauncher;
	private ActivityResultLauncher<String[]> readDocLauncher;
	private ActivityResultLauncher<Uri> openDocTree;
	private ActivityResultLauncher<String[]> requestMultiplePermissionLauncher;
	private ActivityResultLauncher<String[]> openDocTreePermission;
	DBManager manager;

	public ActivityResultLauncher<Intent> getResultLauncher(){
		return resultLauncher;
	}

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

		//result
		resultLauncher=registerForActivityResult(
			new ActivityResultContracts.StartActivityForResult(),
			result -> {
				Log.e("FF",""+result);
				if(result.getResultCode()==RESULT_OK){
					Log.e("FF",""+result.getData().getStringExtra("test"));
					Snackbar.make(binding.notesRecycler,"Saved",Snackbar.LENGTH_LONG).show();
				}
				else if(result.getResultCode()==RESULT_CANCELED){
					Snackbar.make(binding.notesRecycler,"Canceled",Snackbar.LENGTH_LONG).show();
				}
			}
		);

		//createDocLauncher
		createDocLauncher=registerForActivityResult(
				new ActivityResultContracts.CreateDocument(ClipDescription.MIMETYPE_TEXT_PLAIN),
				(Uri uri)->{
					try {
						Log.e("FF", uri.toString());
						ContentResolver resolver = getContentResolver();
						OutputStream outputStream = resolver.openOutputStream(uri);
						outputStream.write("test".getBytes(StandardCharsets.UTF_8));
						outputStream.close();
					}
					catch (Exception e){
						e.printStackTrace();
					}
				}
		);
		//readDocLauncher
		readDocLauncher=registerForActivityResult(
				new ActivityResultContracts.OpenDocument(),
				(Uri uri)->{
					try {
						Log.e("FF", uri.toString());
						ContentResolver resolver = getContentResolver();
						InputStream inputStream = resolver.openInputStream(uri);
						Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
						String json = scanner.nextLine();
						Log.e("FF", json);
						inputStream.close();
					}
					catch (Exception e){
						e.printStackTrace();
					}
				}
		);
		//openDocTree
		openDocTree=registerForActivityResult(
				new ActivityResultContracts.OpenDocumentTree(),
				(Uri uri)->{
					Log.e("FF", uri.toString());

				}
		);
		openDocTreePermission=requestMultiplePermissions(
				permission->permission.get(Manifest.permission.READ_EXTERNAL_STORAGE),
				o->openDocTree.launch(null),
				"some text"
		);
		//requestSinglePermissionLauncher
		requestPermissionLauncher=registerForActivityResult(
				new ActivityResultContracts.RequestPermission(),
				permission->{
					Log.e("FF",permission.toString());
					if(permission){
						createDocLauncher.launch(null);
					}
					else{
						Snackbar snackbar=Snackbar.make(AllNotesRecyclerActivity.this,
								binding.notesRecycler, "Next time press other button =(",
								BaseTransientBottomBar.LENGTH_LONG);
						snackbar.show();
					}
				}
		);
		//requestMultiplePermissionLauncher
		/*requestMultiplePermissionLauncher=registerForActivityResult(
				new ActivityResultContracts.RequestMultiplePermissions(),
				permissions->{
					Log.e("FF",permissions.toString());
					if(permissions.get(Manifest.permission.READ_EXTERNAL_STORAGE)){
						createDocLauncher.launch("");
					}
					else{
						Snackbar snackbar=Snackbar.make(AllNotesRecyclerActivity.this,
								binding.notesRecycler, "Next time press other button =(",
								BaseTransientBottomBar.LENGTH_LONG);
						snackbar.show();
					}
				}
		);*/
		requestMultiplePermissionLauncher=requestMultiplePermissions(
				permissions->permissions.get(Manifest.permission.READ_EXTERNAL_STORAGE),
				o->createDocLauncher.launch("fileName"),
				"To export notes allow permission"
		);
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
			case R.id.saveListMenu:
				requestMultiplePermissionLauncher.launch(new String[]{
						Manifest.permission.READ_EXTERNAL_STORAGE,
						Manifest.permission.WRITE_EXTERNAL_STORAGE
				});
				break;
			case R.id.loadListMenu:
				break;
			case R.id.browseMenu:
				openDocTreePermission.launch(new String[]{
						Manifest.permission.READ_EXTERNAL_STORAGE,
						Manifest.permission.WRITE_EXTERNAL_STORAGE
				});
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private ActivityResultLauncher<String[]> requestMultiplePermissions(
			Predicate<Map<String, Boolean>> checkPermissions,
			Consumer<?> action,
			String msg){
		return registerForActivityResult(
				new ActivityResultContracts.RequestMultiplePermissions(),
				(Map<String,Boolean>permissions)->{
					if(checkPermissions.test(permissions)){
						action.accept(null);
					}
					else{
						Snackbar snackbar=Snackbar.make(AllNotesRecyclerActivity.this,
								binding.notesRecycler, msg,
								BaseTransientBottomBar.LENGTH_LONG);
						snackbar.show();
					}
				}
		);
	}

	/*public void startNoteActivity(){
		Intent intent = new Intent(this, NoteActivity.class);
		resultLauncher.launch(intent);
	}*/
}