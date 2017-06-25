package com.example.pantera.questionnaire_depression;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.pantera.questionnaire_depression.settings.SettingsActivity;

import org.joda.time.DateTime;

import static android.content.Context.ALARM_SERVICE;
import static com.example.pantera.questionnaire_depression.MainActivity.ALARM_REQUEST;

/**
 * Created by Pantera on 2017-05-27.
 */

public class NotifyManager {

    private static final String TAG = NotifyManager.class.getSimpleName();
    private Context context;

    public NotifyManager(Context context) {
        this.context = context;
    }

    public void onStart(){
        SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(context);
        boolean notifyEnabled = preferenceManager.getBoolean(SettingsActivity.KEY_NOTIFY, false);
        Log.d("settigs val en: ", String.valueOf(notifyEnabled));
        String timeToNotify = preferenceManager.getString(SettingsActivity.KEY_TIME, "");
        Log.d("settings val time: ",timeToNotify);
        if(notifyEnabled && !timeToNotify.isEmpty()){
            int[] time = stringTimeToIntArray(timeToNotify);
            DateTime dateTime = new DateTime().withHourOfDay(time[0]).withMinuteOfHour(time[1]).withSecondOfMinute(0).plusMonths(1);
            //DateTime dateTime = new DateTime().plusMinutes(1);

            Intent intent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST, intent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, dateTime.getMillis(), pendingIntent);
            Log.d(TAG + " milisec: ", String.valueOf(dateTime.getMillis()));
            Log.d(TAG + " Pending: ", pendingIntent.toString());
        }
    }

    public void onStop(){
        Intent intent = new Intent(context, AlarmReceiver.class);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST, intent, PendingIntent.FLAG_NO_CREATE);
        if(pendingIntent != null){
            alarmManager.cancel(pendingIntent);
        }
    }

    private int[] stringTimeToIntArray(String time){
        String[]split = time.split(":");
        return new int[]{Integer.parseInt(split[0]), Integer.parseInt(split[1])};
    }

}
