/*
 * Copyright 2012-2015 Girish Budhwani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.girish.slickrate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

/**
 * Slick Rate app
 *
 * A library to show slicke app rate dialog
 * which helps you get only good reviews and redirect
 * bad ones to your feedback portal
 *
 * @author Girish Budhwani (girishiitj@gmail.com)
 */

public class SlickRateDialog {

    private Context mContext;
    private SharedPreferences mSharedPreference;
    private SharedPreferences.Editor mEditor;
    private String mEmailAddress;

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
    final private String OPT_OUT = "sra_opt_out";


    /**
     * Constructor for initializing basic paraameter
     * @param context
     */
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
            setBooleanKey(OPT_OUT, false);
        }
        else {
            setIntKey(NO_OF_OPENS, noOfOpens + 1);
        }

    }

    /**
     * Showing the dialog if constraints satisfy
     */
    public void showDialogIfRequired() {

        /*
        * show dialog only if not opted out
        * */
        if (!getBooleanKey(OPT_OUT)) {
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
    }

    /**
     * Showing the dialog
     */
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
                        String packageName = mContext.getPackageName();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
                        mContext.startActivity(intent);
                        setBooleanKey(OPT_OUT, true);
                    }
                })
                .setNegativeButton(R.string.negative_message2, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setBooleanKey(OPT_OUT, true);
                    }

                });

        /*
        * configuring the negative alert dialog
        * */
        builder3.setMessage(R.string.dialog_message3);
        builder3.setTitle("RATE APP");

        builder3
                .setPositiveButton(R.string.positive_message3, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        if (mEmailAddress != null) intent.putExtra(Intent.EXTRA_EMAIL, new String[] { mEmailAddress });
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                        intent.putExtra(Intent.EXTRA_TEXT, "I'd like to suggest..");

                        mContext.startActivity(Intent.createChooser(intent, "Send Email"));
                        setBooleanKey(OPT_OUT, true);
                    }
                })
                .setNegativeButton(R.string.negative_message3, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setBooleanKey(OPT_OUT, true);
                    }

                });

        // showing the first builder
        builder1.create().show();
    }

    /**
     * Email is required for sending feedback
     * @param email
     */
    public void setEmailAddress(String email) {
        mEmailAddress = email;
    }

    /**
     * helper method to store the int key value pair in shared preference
     * @param key
     * @param value
     */
    private void setIntKey(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    /**
     * helper method to get the int value for a give key in shared preference
     * @param key
     * @return int
     */
    private int getIntKey(String key) {
        return mSharedPreference.getInt(key, -1);
    }


    /**
     * helper method to store the string key value pair in shared preference
     * @param key
     * @param value
     */
    private void setStringKey(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
    }

    /**
     * helper method to get the String value for a give key in shared preference
     * @param key
     * @return String
     */
    private String getStringKey(String key) {
        return mSharedPreference.getString(key, "");
    }

    /**
     * helper method to store the string key value pair in shared preference
     * @param key
     * @param value
     */
    private void setBooleanKey(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }

    /**
     * helper method to get the String value for a give key in shared preference
     * @param key
     * @return boolean
     */
    private boolean getBooleanKey(String key) {
        return mSharedPreference.getBoolean(key, false);
    }
}

