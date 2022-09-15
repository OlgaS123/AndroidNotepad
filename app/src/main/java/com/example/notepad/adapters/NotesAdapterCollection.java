package com.example.notepad.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notepad.R;
import com.example.notepad.models.Note;

import java.util.List;

public class NotesAdapterCollection extends RecyclerView.Adapter<NotesAdapterCollection.NoteViewHolderCollection> {
	private Context context;
	private List<Note> list;
	private LayoutInflater inflater;

	public NotesAdapterCollection(Context context, List<Note> list) {
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
	}

	@NonNull
	@Override
	public NoteViewHolderCollection onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.notes_element, parent, false);
		return new NoteViewHolderCollection(view);
	}

	@Override
	public void onBindViewHolder(@NonNull NoteViewHolderCollection holder, int position) {
		Note note = list.get(position);
		holder.text1.setText(note.getTitle());
		holder.text2.setText(note.getFormattedTime());
	}

	@Override
	public int getItemCount() {
		return list.size();
	}

	//HOLDER
	static class NoteViewHolderCollection extends RecyclerView.ViewHolder {
		TextView text1;
		TextView text2;

		public NoteViewHolderCollection(@NonNull View itemView) {
			super(itemView);
			text1 = itemView.findViewById(R.id.text1);
			text2 = itemView.findViewById(R.id.text2);
		}
	}
}
