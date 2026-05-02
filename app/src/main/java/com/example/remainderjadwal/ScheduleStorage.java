package com.example.remainderjadwal;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ScheduleStorage {
    private static final String PREF_NAME = "schedule_pref";
    private static final String KEY_LIST = "schedule_list";

    public static ArrayList<Schedule> getSchedules(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_LIST, null);
        Type type = new TypeToken<ArrayList<Schedule>>(){}.getType();
        ArrayList<Schedule> list = new Gson().fromJson(json, type);
        if (list == null) list = new ArrayList<>();
        return list;
    }

    public static void saveSchedules(Context context, ArrayList<Schedule> list) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_LIST, new Gson().toJson(list)).apply();
    }

    public static void deleteSchedule(Context context, String id) {
        ArrayList<Schedule> list = getSchedules(context);
        list.removeIf(s -> s.getId().equals(id));
        saveSchedules(context, list);
    }
}
