package com.example.remainderjadwal;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class AlarmUtil {

    public static void scheduleReminder(Context context, Schedule s) {
        if (s.getReminderMinutes() <= 0) return; // no reminder

        // parse jam "HH:mm"
        String[] parts = s.getJam().split(":");
        if (parts.length < 2) return;
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        // subtract reminder minutes
        cal.add(Calendar.MINUTE, -s.getReminderMinutes());

        // if time already passed today -> schedule for next day
        if (cal.getTimeInMillis() <= System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(NotificationReceiver.EXTRA_TITLE, "Pengingat Kuliah: " + s.getDosen());
        intent.putExtra(NotificationReceiver.EXTRA_TEXT, s.getDosen() + " jam " + s.getJam() + " di " + s.getRuangan());
        intent.putExtra("scheduleId", s.getId());

        PendingIntent pi = PendingIntent.getBroadcast(context, s.getId().hashCode(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
        }
    }

    public static void cancelReminder(Context context, String scheduleId) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, scheduleId.hashCode(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) am.cancel(pi);
    }
}
