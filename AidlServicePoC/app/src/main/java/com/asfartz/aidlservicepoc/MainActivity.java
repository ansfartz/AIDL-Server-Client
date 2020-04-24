package com.asfartz.aidlservicepoc;

import androidx.appcompat.app.AppCompatActivity;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Andy";

    private Button addBtn, minusBtn;
    private TextView nr1, nr2, result;

    IMathService mathService;
    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: name = " + name.toString() + " service = " + service.toString());
            mathService = IMathService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent serviceIntent = new Intent(this, MathService.class);

        Log.d(TAG, "onCreate: bindService - using intent = " + serviceIntent);
        bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);

        init();

    }

    private void init() {
        addBtn = findViewById(R.id.addBtn);
        minusBtn = findViewById(R.id.minusBtn);
        nr1 = findViewById(R.id.nr1);
        nr2 = findViewById(R.id.nr2);
        result = findViewById(R.id.resultTV);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value1 = Integer.parseInt(nr1.getText().toString());
                int value2 = Integer.parseInt(nr2.getText().toString());
                int res = 0;
                try {
                    res = mathService.add(value1, value2);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                result.setText(String.valueOf(res));
            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value1 = Integer.parseInt(nr1.getText().toString());
                int value2 = Integer.parseInt(nr2.getText().toString());
                int res = 0;
                try {
                    res = mathService.substract(value1, value2);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                result.setText(String.valueOf(res));
            }
        });
    }






}
