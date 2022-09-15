package com.example.notepad.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notepad.NoteActivity;
import com.example.notepad.R;
import com.example.notepad.data.DBManager;
import com.example.notepad.models.Note;

import java.time.LocalDateTime;

public class NotesAdapterCursor extends RecyclerView.Adapter<NotesAdapterCursor.NoteViewHolderCursor> {
	private Context context;
	private Cursor cursor;
	private LayoutInflater inflater;

	public NotesAdapterCursor(Context context, Cursor cursor) {
		this.context = context;
		this.cursor = cursor;
		inflater = LayoutInflater.from(context);
	}

	@NonNull
	@Override
	public NoteViewHolderCursor onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.notes_element, parent, false);
		/*view.setOnClickListener(v -> {
			Intent intent = new Intent(context, NoteActivity.class);
			intent.putExtra(DBManager.ID, 0);
			context.startActivity(intent);
		});*/
		return new NoteViewHolderCursor(view);
	}

	@SuppressLint("Range")
	@Override
	public void onBindViewHolder(@NonNull NoteViewHolderCursor holder, int position) {
		if (cursor.moveToPosition(position)) {
			holder.id = cursor.getInt(cursor.getColumnIndex(DBManager.ID));
			holder.text1.setText(cursor.getString(cursor.getColumnIndex(DBManager.TITLE)));
			LocalDateTime dt = LocalDateTime.parse(cursor.getString(cursor.getColumnIndex(DBManager.TIME)));
			holder.text2.setText(dt.format(Note.formatter));
			//
			holder.itemView.setOnClickListener(v -> {
				Intent intent = new Intent(context, NoteActivity.class);
				intent.putExtra(DBManager.ID, holder.id);
				context.startActivity(intent);
			});
		}
	}

	@Override
	public int getItemCount() {
		return cursor.getCount();
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
