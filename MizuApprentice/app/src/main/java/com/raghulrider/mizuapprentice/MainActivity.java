package com.raghulrider.mizuapprentice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import me.itangqi.waveloadingview.WaveLoadingView;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    public TextView waterlevel, waterusage, candoview, costestimation;
    WaveLoadingView waveLoadingView;
    private Integer percentage,costpl, usage, level;
    private String cando;
    DatabaseReference rootRef, mainRef;
    TextToSpeech t1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        waveLoadingView = findViewById(R.id.waveLoadingView);
        auth = FirebaseAuth.getInstance();
        waterlevel =findViewById(R.id.waterlevel);
        waterusage = findViewById(R.id.waterusage);
        candoview = findViewById(R.id.cando);
        costestimation = findViewById(R.id.costestimation);
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener(){

            @Override
            public void onInit(int status) {
                t1.setLanguage(Locale.getDefault());
            }
        });
        rootRef = FirebaseDatabase.getInstance().getReference();
//database reference pointing to maindata node
        mainRef = rootRef.child("maindata");
        mainRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MainData mainData = dataSnapshot.getValue(MainData.class);
                cando = mainData.getCando();
                costpl = mainData.getCost();
                usage = mainData.getUsage();
                level = mainData.getLevel();
                percentage = mainData.getPercentage();
                updateMainActivity(cando, costpl, usage, level, percentage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void updateMainActivity(String cando,Integer costpl,Integer usage,Integer level,Integer percentage){
        waveLoadingView.setProgressValue(percentage);
        waveLoadingView.setBottomTitle(percentage.toString()+"%");
        if (percentage<26){
            waveLoadingView.setWaveColor(getResources().getColor(android.R.color.holo_red_light));
        }else if (percentage>75){
            waveLoadingView.setWaveColor(getResources().getColor(android.R.color.holo_blue_dark));
        }else{
            waveLoadingView.setWaveColor(getResources().getColor(android.R.color.holo_blue_light));
        }
        waterlevel.setText(" " + level.toString()+" ltr ");
        waterusage.setText(" "+usage.toString()+" ltr ");
        Integer cost = costpl*usage;
        candoview.setText(cando);
        costestimation.setText(" ₹"+cost.toString()+" ");
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Mypref",0);
        boolean isFirstRun = pref.getBoolean("FIRSTRUN", true);
        if (isFirstRun)
        {
            t1.speak(getString(R.string.maincontent), TextToSpeech.QUEUE_FLUSH, null);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("FIRSTRUN", false);
            editor.commit();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainactivity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.motor) {
            startActivity(new Intent(MainActivity.this, MotorControl.class));
        }
        else if(id==R.id.logout){
            auth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            Toast.makeText(getApplicationContext(), "Signed Out", Toast.LENGTH_SHORT).show();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
