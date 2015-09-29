package com.girish.slickrate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.util.Date;

/**
 * Created by Girish on 29/09/15.
 */
public class SlickRateDialog {

    private Context mContext;
    private SharedPreferences mSharedPreference;
    private SharedPreferences.Editor mEditor;

    /*
    * Default values
    * Change these values as needed
    * */
    int mMaxDays = 10;
    int mMaxOpens = 3;


    /*
    * Shared preference keys
    * */
    final private String PREFERENCES = "sra_preference";
    final private String MAX_DAYS = "sra_max_days";
    final private String MAX_OPENS = "sra_max_opens";
    final private String INSTALL_TIME = "sra_install_time";
    final private String NO_OF_OPENS = "sra_no_of_opens";



    public SlickRateDialog(Context context) {

        mContext = context;
        mSharedPreference = context.getSharedPreferences(PREFERENCES, context.MODE_PRIVATE);
        mEditor = mSharedPreference.edit();


        /*
        * setting values only if the
        * app is opened for the first time
        * */
        int noOfOpens = getIntKey(NO_OF_OPENS);
        if (noOfOpens == -1) {
            setIntKey(MAX_OPENS, mMaxOpens);
            setIntKey(MAX_DAYS, mMaxDays);
            setIntKey(NO_OF_OPENS, 1);
            setStringKey(INSTALL_TIME, System.currentTimeMillis() + "");
        }
        else {
            setIntKey(NO_OF_OPENS, noOfOpens + 1);
        }

    }

    public void showDialogIfRequired() {

        int maxDays = getIntKey(MAX_DAYS);
        String install_time = getStringKey(INSTALL_TIME);
        long installTime = Long.valueOf(install_time);
        long currentTime = System.currentTimeMillis();
        float daysSoFar = (float) (currentTime - installTime) / (float) (1000 * 60 * 60 * 24);

        int noOfOpens = getIntKey(NO_OF_OPENS);
        int maxOpens = getIntKey(MAX_OPENS);

        if (daysSoFar >= maxDays || noOfOpens >= maxOpens) {
            showDialog();
        }

    }

    public void showDialog() {
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        final AlertDialog.Builder builder2 = new AlertDialog.Builder(mContext);
        final AlertDialog.Builder builder3 = new AlertDialog.Builder(mContext);

        /*
        * configuring the main alert dialog
        * */
        builder1.setMessage(R.string.dialog_message1);
        builder1.setTitle("RATE APP");

        builder1
                .setPositiveButton(R.string.positive_message1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        builder2.create().show();
                    }
                })
                .setNegativeButton(R.string.negative_message1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        builder3.create().show();
                    }

                });

        /*
        * configuring the positive alert dialog
        * */
        builder2.setMessage(R.string.dialog_message2);
        builder2.setTitle("RATE APP");

        builder2
                .setPositiveButton(R.string.positive_message2, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton(R.string.negative_message2, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }

                });

        /*
        * configuring the positive alert dialog
        * */
        builder3.setMessage(R.string.dialog_message3);
        builder3.setTitle("RATE APP");

        builder3
                .setPositiveButton(R.string.positive_message3, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton(R.string.negative_message3, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }

                });

        // showing the first builder
        builder1.create().show();
    }

    private void setIntKey(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    private int getIntKey(String key) {
        return mSharedPreference.getInt(key, -1);
    }

    private void setStringKey(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
    }

    private String getStringKey(String key) {
        return mSharedPreference.getString(key, "");
    }
}

