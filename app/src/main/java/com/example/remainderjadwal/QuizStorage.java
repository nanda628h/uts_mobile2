package com.example.remainderjadwal;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class QuizStorage {
    private static final String PREF = "quiz_pref";
    private static final String KEY = "quiz_list";

    public static ArrayList<Quiz> getQuizzes(Context c) {
        SharedPreferences p = c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String json = p.getString(KEY, null);
        Type type = new TypeToken<ArrayList<Quiz>>(){}.getType();
        ArrayList<Quiz> list = new Gson().fromJson(json, type);
        if (list == null) list = new ArrayList<>();
        return list;
    }

    public static void saveQuizzes(Context c, ArrayList<Quiz> list) {
        SharedPreferences p = c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        p.edit().putString(KEY, new Gson().toJson(list)).apply();
    }
}
