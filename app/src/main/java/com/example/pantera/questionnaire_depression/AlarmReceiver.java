package com.example.pantera.questionnaire_depression;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int mNotificationId = 1001;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Dostępne jest badanie")
                .setContentText("Wypełnij miesęczny test i wyślij lekarzowi !")
                .setAutoCancel(true)
                .setOngoing(false);


        PendingIntent contentIntent = PendingIntent.getActivity(context, MainActivity.SEND_QUESTIONNAIRE_REQUEST,
                new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
