package com.example.notepad;

import static com.example.notepad.tools.Settings.settings;

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
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.notepad.adapters.NotesAdapterCursor;
import com.example.notepad.data.DBManager;
import com.example.notepad.data.FileManager;
import com.example.notepad.databinding.ActivityAllNotesRecyclerBinding;
import com.example.notepad.models.Note;
import com.example.notepad.serializers.LocalDateTimeDeserializer;
import com.example.notepad.serializers.LocalDateTimeSerializer;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

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
	private ActivityResultLauncher<String[]> requestMultiplePermissionsCreateDocLauncher;
	private ActivityResultLauncher<String[]> requestMultiplePermissionsReadDocLauncher;
	private ActivityResultLauncher<String[]> readDocLauncher;
	private ActivityResultLauncher<Uri> openDocTree;
	private ActivityResultLauncher<String[]> openDocTreePermissions;
	public ActivityResultLauncher<Intent> getResultLauncher() {
		return resultLauncher;
	}

	Cursor cursor;
	NotesAdapterCursor adapter;

	public static String FILE_NAME = "noteList.txt";
	public static String ENCODING = "utf8";
	public static Gson gson;
	static {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
		gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
		gson=gsonBuilder.create();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		settings().load(this);
		setTheme(settings().theme);
		//
		Log.e("FF", "onCreate");
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
		manager.dropTab();
		manager.createTab();
		for (Note note : list) {
			manager.insert(note);
		}
		cursor = manager.findAllToCursor();
		adapter = new NotesAdapterCursor(this, cursor);
		binding.notesRecycler.setAdapter(adapter);

		//RESULT
		resultLauncher = registerForActivityResult(
				new ActivityResultContracts.StartActivityForResult(),
				result -> {
					Log.e("FF", "" + result);
					if (result.getResultCode() == RESULT_OK) {
						if (result.getData().getBooleanExtra("create", false)) {
							int id = result.getData().getIntExtra(DBManager.ID, 0);
							//adapter.addElementCursor(manager.findByIdCursor(id));
							adapter.addElementCursor(manager.findAllToCursor());
							//
							Snackbar.make(binding.notesRecycler, "Saved", Snackbar.LENGTH_LONG).show();
						} else {
							int id = result.getData().getIntExtra(DBManager.ID, 0);
							Note note = manager.findById(id);
							adapter.updateElement(
									note, result.getData().getIntExtra("position", -1));
						}
					} else if (result.getResultCode() == RESULT_CANCELED) {
						Snackbar.make(binding.notesRecycler, "Canceled", Snackbar.LENGTH_LONG).show();
					}
				}
		);
		//CREATE DOC
		createDocLauncher = registerForActivityResult(
				new ActivityResultContracts.CreateDocument(ClipDescription.MIMETYPE_TEXT_PLAIN),
				(Uri uri) -> {
					try {
						Log.e("FF", uri.toString());
						ContentResolver resolver = getContentResolver();
						List listFromDb = manager.findAllToList();
						String noteListGson = gson.toJson(listFromDb);
						OutputStream output = resolver.openOutputStream(uri);
						output.write(noteListGson.getBytes(ENCODING));
						output.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
		);
		//READ DOC
		readDocLauncher = registerForActivityResult(
				new ActivityResultContracts.OpenDocument(),
				(Uri uri) -> {
					try {
						Log.e("FF", uri.toString());
						ContentResolver resolver = getContentResolver();
						InputStream input = resolver.openInputStream(uri);
						Scanner scanner = new Scanner(input, ENCODING);
						String json = scanner.nextLine();
						Log.e("FF!!!!!!!!", json);

						List<Note> listFromJson = gson.fromJson(json,
								new TypeToken<List<Note>>(){}.getType());
						manager.dropTab();
						manager.createTab();
						for (Note note : listFromJson) {
							manager.insert(note);
						}
						cursor = manager.findAllToCursor();
						adapter = new NotesAdapterCursor(this, cursor);
						binding.notesRecycler.setAdapter(adapter);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
		);

		//OPEN DOC TREE
		openDocTree = registerForActivityResult(
				new ActivityResultContracts.OpenDocumentTree(),
				(Uri uri) -> {
					Log.e("FF", uri.toString());
				}
		);
		openDocTreePermissions = requestMultiplePermissions(
				permissions -> permissions.get(Manifest.permission.READ_EXTERNAL_STORAGE),
				o -> openDocTree.launch(null),
				"To export notes allow permission"
		);
		//REQUEST SINGLE PERMISSION
		requestPermissionLauncher = registerForActivityResult(
				new ActivityResultContracts.RequestPermission(),
				permission -> {
					Log.e("FF", permission.toString());
					if (permission) {
						createDocLauncher.launch(DocumentsContract.Document.MIME_TYPE_DIR);
					} else {
						Snackbar snackbar = Snackbar.make(AllNotesRecyclerActivity.this,
								binding.notesRecycler, "To export notes allow permission",
								BaseTransientBottomBar.LENGTH_LONG);
						snackbar.show();
					}
				}
		);
		//REQUEST MULTIPLE PERMISSIONS
		requestMultiplePermissionsCreateDocLauncher = requestMultiplePermissions(
				permissions -> permissions.get(Manifest.permission.WRITE_EXTERNAL_STORAGE),
				o -> createDocLauncher.launch(FILE_NAME),
				"To export notes allow permission"
		);
		requestMultiplePermissionsReadDocLauncher = requestMultiplePermissions(
				permissions -> permissions.get(Manifest.permission.READ_EXTERNAL_STORAGE),
				o -> readDocLauncher.launch(new String[]{"*/*"}),
				"To load notes allow permission"
		);
	}
	private void getCursor(){

	}
	private ActivityResultLauncher<String[]> requestMultiplePermissions(
			Predicate<Map<String, Boolean>> checkPermissions,
			Consumer<?> action,
			String message
	) {
		return registerForActivityResult(
				new ActivityResultContracts.RequestMultiplePermissions(),
				(Map<String, Boolean> permissions) -> {
					if (checkPermissions.test(permissions)) {
						action.accept(null);
					} else {
						Snackbar snackbar = Snackbar.make(AllNotesRecyclerActivity.this,
								binding.notesRecycler, message,
								BaseTransientBottomBar.LENGTH_LONG);
						snackbar.show();
					}
				}
		);
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
				resultLauncher.launch(intent);
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
				requestMultiplePermissionsCreateDocLauncher.launch(new String[]{
						Manifest.permission.READ_EXTERNAL_STORAGE,
						Manifest.permission.WRITE_EXTERNAL_STORAGE
				});
				break;
			case R.id.loadListMenu:
				requestMultiplePermissionsReadDocLauncher.launch(new String[]{
						Manifest.permission.READ_EXTERNAL_STORAGE,
						Manifest.permission.WRITE_EXTERNAL_STORAGE
				});
				break;
			case R.id.browseMenu:
				openDocTreePermissions.launch(new String[]{
						Manifest.permission.READ_EXTERNAL_STORAGE,
						Manifest.permission.WRITE_EXTERNAL_STORAGE
				});
				break;
			case R.id.lightThemeMenu:
				settings().theme=R.style.Light;
				settings().save(this);
				recreate();
				break;
			case R.id.darkThemeMenu:
				settings().theme=R.style.Dark;
				settings().save(this);
				recreate();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}