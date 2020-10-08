package com.example.saufio;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import androidx.appcompat.app.AlertDialog;

import static android.content.Context.MODE_PRIVATE;

public class Allgemein {

    static String KEY_TON = "KEY_TON";
    SharedPreferences sharedpreferences;
    static SharedPreferences prefs;
    static SharedPreferences.Editor prefsedit;

    public static void alertOK(Context c, String title, String message, String button) {
        AlertDialog.Builder alterDialog = new AlertDialog.Builder(c);
        alterDialog.setTitle(title);
        alterDialog.setMessage(message);
        alterDialog.setPositiveButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alterDialog.create();
        alterDialog.show();
    }

    public static boolean gebeTon(Context c){
        final SharedPreferences prefs;
        prefs = c.getSharedPreferences("Speicher", MODE_PRIVATE);
        return prefs.getBoolean(KEY_TON, true);
    }

    public static boolean tonaendern(Context c) {
        prefs = c.getSharedPreferences("Speicher", MODE_PRIVATE);
        prefsedit = prefs.edit();
        if (prefs.getBoolean(KEY_TON, true)) {
            prefsedit.putBoolean(KEY_TON, false);
            prefsedit.commit();
        } else {
            prefsedit.putBoolean(KEY_TON, true);
            prefsedit.commit();
        }
        return prefs.getBoolean(KEY_TON, true);
    }
}
