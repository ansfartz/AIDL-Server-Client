package com.asfartz.aidlclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.asfartz.aidlserver.IMathManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView res;
    private EditText nr1, nr2;
    private Button bindBtn, addBtn, substractBtn;
    private IMathManager mathManager;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mathManager = IMathManager.Stub.asInterface(service);
            Toast.makeText(MainActivity.this, "BOUND", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        res = findViewById(R.id.result);
        nr1 = findViewById(R.id.nr1);
        nr2 = findViewById(R.id.nr2);
        bindBtn = findViewById(R.id.bindBtn);
        addBtn = findViewById(R.id.btnAdd);
        substractBtn = findViewById(R.id.btnSubstract);


        bindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = convertImplicitIntentToExplicitIntent(new Intent("com.asfartz.service.AIDL"));
                bindService(intent, serviceConnection, BIND_AUTO_CREATE);
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int n1 = Integer.parseInt(nr1.getText().toString());
                int n2 = Integer.parseInt(nr2.getText().toString());
                try {
                    int result = mathManager.add(n1, n2);
                    Log.d("andy", "onClick: add = " + result);
                    res.setText(String.valueOf(result));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        substractBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int n1 = Integer.parseInt(nr1.getText().toString());
                int n2 = Integer.parseInt(nr2.getText().toString());
                try {
                    int result = mathManager.substract(n1, n2);
                    Log.d("andy", "onClick: substract = " + result);
                    res.setText(String.valueOf(result));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public Intent convertImplicitIntentToExplicitIntent(Intent implicitIntent) {
        PackageManager pm = getPackageManager();
        List<ResolveInfo> resolveInfoList = pm.queryIntentServices(implicitIntent, 0);
        if (resolveInfoList == null || resolveInfoList.size() != 1) {
            return null;
        }

        ResolveInfo serviceInfo = resolveInfoList.get(0);
        ComponentName component = new ComponentName(serviceInfo.serviceInfo.packageName, serviceInfo.serviceInfo.name);
        Intent explicitIntent = new Intent(implicitIntent);
        explicitIntent.setComponent(component);
        return explicitIntent;
    }

}
