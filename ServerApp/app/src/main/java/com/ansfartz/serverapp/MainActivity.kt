package com.ansfartz.serverapp

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var imgConnected: ImageView

    private lateinit var mathManager: IMathManager

    private val serviceConnection: ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "onServiceConnected: name = $name, service = $service")

            mathManager = IMathManager.Stub.asInterface(service)
            imgConnected.setImageResource(R.drawable.check_circle_24)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "onServiceDisconnected: name = $name")

            imgConnected.setImageResource(R.drawable.stop_circle_24)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imgConnected = findViewById(R.id.connectedImageView)

        val serviceIntent = Intent(this, MathService::class.java)
        Log.d("XXXX", "intent = $intent")
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE)
    }
}