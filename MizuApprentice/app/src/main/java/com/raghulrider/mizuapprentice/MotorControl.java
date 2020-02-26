package com.raghulrider.mizuapprentice;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class MotorControl extends AppCompatActivity {
    TextToSpeech t1;
    Button onBtn, offBtn, start;
    TextView completionStatus;
    MaterialButtonToggleGroup toggleGroup;
    EditText timerlimit;
    DatabaseReference rootRef, motorcontrolstatus, motorref;
    ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motor_control);
        onBtn = findViewById(R.id.onBtn);
        offBtn = findViewById(R.id.offBtn);
        toggleGroup = findViewById(R.id.motorControlToggle);
        completionStatus = findViewById(R.id.completionStatus);
        start = findViewById(R.id.button);
        timerlimit = findViewById(R.id.timerlimit);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Motor Control");
        actionBar.setDisplayHomeAsUpEnabled(true);
        start.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                setTimer();
            }
        });
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.getDefault());
                }
            }
        });
        rootRef = FirebaseDatabase.getInstance().getReference();
        motorcontrolstatus = rootRef.child("motor").child("status");
        motorref = rootRef.child("motor");
        motorref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Motor motor = dataSnapshot.getValue(Motor.class);
                completionStatus.setText(motor.getStatus());
                if (motor.getStatus().equals("on")){
                    toggleGroup.check(R.id.onBtn);
                    t1.speak("Motor is turned on",TextToSpeech.QUEUE_FLUSH, null);
                }
                else{
                    toggleGroup.check(R.id.offBtn);
                    t1.speak("Motor is turned off",TextToSpeech.QUEUE_FLUSH, null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        onBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                motorcontrolstatus.setValue("on");
            }
        });
        offBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                motorcontrolstatus.setValue("off");
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setTimer() {
        int i = Integer.parseInt(timerlimit.getText().toString());
        Intent intent = new Intent(this, MyBroadCastReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 234324243, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        motorcontrolstatus.setValue("on");
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                + (i * 60000), pendingIntent);
        Toast.makeText(this, "Motor will go off in " + i + " minutes",Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
