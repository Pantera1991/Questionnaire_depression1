package com.example.pantera.questionnaire_depression.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by Pantera on 2016-05-24.
 */
public class ServerConnectionLost {
    public static void returnToLoginActivity(final Activity context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Brak połączenia z serwerem zaloguj sie ponowanie")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        SessionManager sessionManager = new SessionManager(context);
                        sessionManager.logoutUser();
                        context.finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }
}
