package com.raghulrider.mizuapprentice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyBroadCastReciever extends BroadcastReceiver {
    DatabaseReference rootRef, motorcontrolstatus;
    @Override
    public void onReceive(Context context, Intent intent) {
        rootRef = FirebaseDatabase.getInstance().getReference();
        motorcontrolstatus = rootRef.child("motor").child("status");
        motorcontrolstatus.setValue("off");
        Toast.makeText(context, "Motor Turned off", Toast.LENGTH_LONG).show();
    }
}