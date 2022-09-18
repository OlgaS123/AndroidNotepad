package com.example.notepad.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.notepad.models.Note;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DBManager extends SQLiteOpenHelper {
	public static final String DB_NAME = "notes";
	public static final String TAB = "notes";
	public static final String ID = "_id";
	public static final String TITLE = "title";
	public static final String TIME = "time";
	public static final String TEXT = "text";
	private SQLiteDatabase db;

	public DBManager(@Nullable Context context) {
		super(context, DB_NAME, null, 1);
		//dropTab();
		//createTab();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public void dropTab() {
		db = getWritableDatabase();
		String sql = "drop table if exists %s";
		sql = String.format(sql, TAB);
		db.execSQL(sql);
	}

	public void createTab() {
		db = getWritableDatabase();
		String sql = "create table if not exists %s(" +
			"%s integer primary key autoincrement," +
			"%s text," +
			"%s text," +
			"%s text" +
			")";
		sql = String.format(sql, TAB, ID, TITLE, TIME, TEXT);
		db.execSQL(sql);
	}

	public void insert(Note note) {
		db = getWritableDatabase();
		String sql = "insert into %s(%s,%s,%s) values('%s','%s','%s')";
		sql = String.format(sql, TAB, TITLE, TIME, TEXT,
			note.getTitle(), note.getTime(), note.getText());
		Log.e("FF", sql);
		db.execSQL(sql);

		/*deleteById(1);
		update(new Note(2,"UPD",LocalDateTime.now(),"UPD"));
		System.err.println(findAllToList());*/
	}

	@SuppressLint("Range")
	public List<Note> findAllToList() {
		db = getReadableDatabase();
		String sql = "select * from " + TAB;
		Cursor cursor = db.rawQuery(sql, new String[]{});
		//
		ArrayList<Note> list = new ArrayList<>();
		Note note;
		//
		while (cursor.moveToNext()) {
			//1
			/*for (int i = 1; i < cursor.getColumnCount(); i++) {
				Log.e("FF", cursor.getString(i));
			}
			Log.e("FF", "---");*/
			//2
			note = new Note(
				cursor.getInt(cursor.getColumnIndex(ID)),
				cursor.getString(cursor.getColumnIndex(TITLE)),
				LocalDateTime.parse(cursor.getString(cursor.getColumnIndex(TIME))),
				cursor.getString(cursor.getColumnIndex(TEXT))
			);
			list.add(note);
		}
		return list;
	}

	public Cursor findAllToCursor() {
		db = getReadableDatabase();
		String sql = "select * from " + TAB;
		return db.rawQuery(sql, new String[]{});
	}

	@SuppressLint("Range")
	public Note findById(@NonNull Integer id) {
		db = getReadableDatabase();
		String sql = "select * from %s where %s=?";
		sql = String.format(sql, TAB, ID);
		Cursor cursor = db.rawQuery(sql, new String[]{id.toString()});
		if (!cursor.moveToNext())
			return null;
		return new Note(
			cursor.getInt(cursor.getColumnIndex(ID)),
			cursor.getString(cursor.getColumnIndex(TITLE)),
			LocalDateTime.parse(cursor.getString(cursor.getColumnIndex(TIME))),
			cursor.getString(cursor.getColumnIndex(TEXT))
		);
	}

	public void deleteById(Integer id)
	{
		db = getWritableDatabase();
		String sql = "delete from %s where %s=%d";
		sql = String.format(sql, TAB, ID, id);
		Log.e("FF", sql);
		db.execSQL(sql);
	}

	public void update(Note note)
	{
			db = getWritableDatabase();
			String sql = "update %s set %s='%s', %s='%s', %s='%s' where %s=%d";
			sql = String.format(sql, TAB, TITLE, note.getTitle(), TIME, note.getTime(), TEXT, note.getText(), ID, note.getId());
			Log.e("FF", sql);
			db.execSQL(sql);
	}
}
