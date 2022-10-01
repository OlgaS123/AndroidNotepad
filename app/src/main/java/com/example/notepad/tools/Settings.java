package com.example.notepad.tools;

import android.content.Context;
import android.util.Log;

import com.example.notepad.R;
import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class Settings {
    private static String FILE_NAME = "settings";
    private static Settings settings;

    public static Settings settings() {
        if (settings == null)
            settings = new Settings();
        return settings;
    }

    {
        theme = R.style.Light;
    }

    public int theme;

    public Settings save(Context context) {
        try {
            Gson gson = new Gson();
            OutputStreamWriter writer = new OutputStreamWriter(context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE));
            gson.toJson(this, writer);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return settings();
    }

    public Settings load(Context context) {
        Log.e("LL", settings.toString());
        try {
            Gson gson = new Gson();
            settings = gson.fromJson(new InputStreamReader(
                            context.openFileInput(FILE_NAME)
                    ),
                    Settings.class
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return settings();
    }

    @Override
    public String toString() {
        return "Settings{" +
                "theme=" + theme +
                '}';
    }
}
