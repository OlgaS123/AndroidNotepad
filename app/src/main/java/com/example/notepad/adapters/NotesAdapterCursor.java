package com.example.notepad.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.AbstractWindowedCursor;
import android.database.Cursor;
import android.database.CursorJoiner;
import android.database.CursorWindow;
import android.database.CursorWrapper;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteCursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notepad.AllNotesRecyclerActivity;
import com.example.notepad.NoteActivity;
import com.example.notepad.R;
import com.example.notepad.data.DBManager;
import com.example.notepad.models.Note;

import java.time.LocalDateTime;
import java.util.List;

public class NotesAdapterCursor extends RecyclerView.Adapter<NotesAdapterCursor.NoteViewHolderCursor> {
	private Context context;
	private LayoutInflater inflater;
	private MergeCursor cursor;
	private CursorWindow window;

	public NotesAdapterCursor(Context context, Cursor cursor) {
		this.context = context;
		this.cursor = new MergeCursor(new Cursor[]{cursor});
		inflater = LayoutInflater.from(context);

		window = new CursorWindow("cursorWindow");
		this.cursor.fillWindow(0, window);
	}

	@NonNull
	@Override
	public NoteViewHolderCursor onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.notes_element, parent, false);
		return new NoteViewHolderCursor(view);
	}

	@SuppressLint("Range")
	@Override
	public void onBindViewHolder(@NonNull NoteViewHolderCursor holder, int position) {
		/*Log.e("FF", "onBindViewHolder 1");
		if (cursor.moveToPosition(position)) {
			holder.id = cursor.getInt(cursor.getColumnIndex(DBManager.ID));
			holder.text1.setText(cursor.getString(cursor.getColumnIndex(DBManager.TITLE)));
			LocalDateTime dt = LocalDateTime.parse(cursor.getString(cursor.getColumnIndex(DBManager.TIME)));
			holder.text2.setText(dt.format(Note.formatter));
			//
			holder.itemView.setOnClickListener(v -> {
				Intent intent = new Intent(context, NoteActivity.class);
				intent.putExtra(DBManager.ID, holder.id);
				//context.startActivity(intent);
				//((Activity)context).finish();
				AllNotesRecyclerActivity activity = (AllNotesRecyclerActivity) context;
				activity.getResultLauncher().launch(intent);
			});
		}*/
	}

	@SuppressLint("Range")
	@Override
	public void onBindViewHolder(
			@NonNull NoteViewHolderCursor holder, int position, @NonNull List<Object> payloads) {
		super.onBindViewHolder(holder, position, payloads);
		Log.e("FF", "onBindViewHolder 2");
		Log.e("FF", String.valueOf(payloads));
		if (payloads.isEmpty()) {
			Log.e("FF", "empty");
			//CURSOR
			/*if (cursor.moveToPosition(position)) {
				holder.id = cursor.getInt(cursor.getColumnIndex(DBManager.ID));
				holder.text1.setText(cursor.getString(cursor.getColumnIndex(DBManager.TITLE)));
				LocalDateTime dt = LocalDateTime.parse(cursor.getString(cursor.getColumnIndex(DBManager.TIME)));
				holder.text2.setText(dt.format(Note.formatter));
				//
				holder.itemView.setOnClickListener(v -> {
					Intent intent = new Intent(context, NoteActivity.class);
					intent.putExtra(DBManager.ID, holder.id);
					intent.putExtra("position", position);
					AllNotesRecyclerActivity activity = (AllNotesRecyclerActivity) context;
					activity.getResultLauncher().launch(intent);
				});
			}*/

			//CURSORWINDOW
			holder.id = window.getInt(position,cursor.getColumnIndex(DBManager.ID));
			holder.text1.setText(window.getString(position,cursor.getColumnIndex(DBManager.TITLE)));
			LocalDateTime dt = LocalDateTime.parse(window.getString(position,cursor.getColumnIndex(DBManager.TIME)));
			holder.text2.setText(dt.format(Note.formatter));
			holder.itemView.setOnClickListener(v -> {
				Intent intent = new Intent(context, NoteActivity.class);
				intent.putExtra(DBManager.ID, holder.id);
				intent.putExtra("position", position);
				AllNotesRecyclerActivity activity = (AllNotesRecyclerActivity) context;
				activity.getResultLauncher().launch(intent);
			});
		} else {
			Log.e("FF", "some");
		}
	}

	@Override
	public int getItemCount() {
		return cursor.getCount();
	}

	//ADD

	public void addElementCursor(Cursor cursor) {
		//this.cursor = new MergeCursor(new Cursor[]{this.cursor, cursor});
		this.cursor = new MergeCursor(new Cursor[]{cursor});
		window=new CursorWindow("newWindow");
		this.cursor.fillWindow(0,window);
		notifyDataSetChanged();
		//notifyItemInserted(getItemCount() - 1);
	}

	public void updateElement(Note note, int position){
		window.putString(note.getTitle(), position, cursor.getColumnIndex(DBManager.TITLE));
		window.putString(note.getTime().toString(), position, cursor.getColumnIndex(DBManager.TIME));
		window.putString(note.getText(), position, cursor.getColumnIndex(DBManager.TEXT));
		notifyItemChanged(position);
		cursor.moveToPosition(position);
	}

	//HOLDER
	static class NoteViewHolderCursor extends RecyclerView.ViewHolder {
		int id;
		TextView text1;
		TextView text2;

		public NoteViewHolderCursor(@NonNull View itemView) {
			super(itemView);
			text1 = itemView.findViewById(R.id.text1);
			text2 = itemView.findViewById(R.id.text2);
		}
	}
}
