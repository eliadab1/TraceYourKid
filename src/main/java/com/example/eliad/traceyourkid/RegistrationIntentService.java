package com.example.eliad.traceyourkid;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

//Class for gcm token for each user

public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegistrationService";
    public String tokenOrRegistrationID;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "gcm_token";
    SharedPreferences.Editor editor;


    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        InstanceID instanceID = InstanceID.getInstance(this);
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        try {
            //get unique id from gcm by project id (SENDER_ID)
            tokenOrRegistrationID = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);



            //send regId to my server
            Log.d(TAG, "token: " + tokenOrRegistrationID);

            //save token in shared preferences
            editor.putString("token",tokenOrRegistrationID);
            editor.commit();

        } catch (IOException e) {
            e.printStackTrace();
        }


}
}
