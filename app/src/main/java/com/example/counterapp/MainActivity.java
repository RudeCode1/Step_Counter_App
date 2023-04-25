package com.example.counterapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    TextView tv_steps;

    SensorManager sensorManager;
    Button btn,insert,view;
    DBHelper DB;
    boolean running = false;
    int variable = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION )== PackageManager.PERMISSION_DENIED){

            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION},0);
        }
        setContentView(R.layout.activity_main);

        tv_steps = (TextView) findViewById(R.id.tv_steps);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        btn = (Button) findViewById(R.id.button);
        insert = (Button) findViewById(R.id.btnInsert);
        view = (Button) findViewById(R.id.btnView);

        //Database class
        DB = new DBHelper(this);

        //Code for getting present date
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String date = df.format(c);

        //code for inserting steps in database
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stepsTXT = tv_steps.getText().toString();
                String dateTXT = date;

                Boolean checkinsertdata = DB.insertuserdata(stepsTXT,dateTXT);
                if(checkinsertdata==true){
                    Toast.makeText(MainActivity.this,"New Entry Inserted",Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(MainActivity.this,"New Entry Not Inserted",Toast.LENGTH_SHORT).show();

            }
        });

        //code for viewing entries in database
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor res = DB.getData();
                if(res.getCount()==0){
                    Toast.makeText(MainActivity.this,"No Entry Exist",Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuffer buffer = new StringBuffer();
                while(res.moveToNext()){
                    buffer.append("Steps :"+res.getString(0)+"\n");
                    buffer.append("Date :"+res.getString(1)+"\n");
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Steps Recorded");
                builder.setMessage(buffer.toString());
                builder.show();
            }
        });

        //step counter button
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_steps.setText("0");
                variable = 0;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        running = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, sensorManager.SENSOR_DELAY_UI);
        }
        else {
            Toast.makeText(this, "SENSOR IS NOT PRESENT", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        running = false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (running) {
           tv_steps.setText(String.valueOf(variable));
            variable++;
            System.out.println(variable);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}