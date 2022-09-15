package com.example.notepad.data;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class FileManager {
	public static String FILE_NAME = "note.txt";
	public static String ENCODING = "utf8";

	public static void save(Context context, String text) {
		try {
			FileOutputStream output = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
			PrintStream printStream = new PrintStream(output, true, ENCODING);
			printStream.println(text);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String load(Context context) {
		try {
			FileInputStream input = context.openFileInput(FILE_NAME);
			Scanner scanner = new Scanner(input, ENCODING);
			StringBuilder builder = new StringBuilder();
			while (scanner.hasNext())
				builder.append(scanner.nextLine()).append("\n");
			return builder.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
