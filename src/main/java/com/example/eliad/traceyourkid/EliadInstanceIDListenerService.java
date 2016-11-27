package com.example.eliad.traceyourkid;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class EliadInstanceIDListenerService extends InstanceIDListenerService {


    @Override
    public void onTokenRefresh() {
        //send new registration id to my server
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
