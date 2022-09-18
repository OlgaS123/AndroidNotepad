package com.example.notepad.data;

import android.content.Context;
import android.util.Log;

import com.example.notepad.models.Note;
import com.example.notepad.serializers.LocalDateTimeDeserializer;
import com.example.notepad.serializers.LocalDateTimeSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Scanner;

public class FileManager {
	public static String FILE_NAME = "note.txt";
	public static String ENCODING = "utf8";
	public static Gson gson;
	static {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
		gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
		gson=gsonBuilder.create();
	}

	public static void save(Context context, Note note) {
		try {
			String noteJson = gson.toJson(note);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE), ENCODING);
			outputStreamWriter.write(noteJson);
			outputStreamWriter.close();
			Log.e("FF", note.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Note load(Context context) {
		Note note = null;
		try {
			InputStream inputStream = context.openFileInput(FILE_NAME);

			if ( inputStream != null ) {
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream, ENCODING);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();

				while ( (receiveString = bufferedReader.readLine()) != null ) {
					stringBuilder.append("\n").append(receiveString);
				}

				inputStream.close();
				String res = stringBuilder.toString();
				Log.e("FF", res);
				note = gson.fromJson(res, Note.class);
				Log.e("FF", note.toString());
			}
		}
		catch (FileNotFoundException e) {
			Log.e("FF", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("FF", "Can not read file: " + e.toString());
		}
		return note;
	}
}
