package com.ansfartz.serverapp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

/**
 * Service class which makes the calls to the implementation of the AIDL interface [MathManagerImpl]
 */
class MathService : Service() {

    private val mathManagerImpl = MathManagerImpl()

    override fun onCreate() {
        Log.d("MathService", "onCreate:")
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d("MathService", "onBind: intent: getPackage = ${intent!!.`package`} " +
                "getAction = ${intent.action} " +
                "getData = ${intent.data} " +
                "getComponent = ${intent.component} " +
                "getScheme = ${intent.scheme} " +
                "getDataString = ${intent.dataString}"
        )

        return mathManagerImpl
    }

    override fun onDestroy() {
        Log.d("MathService", "onCreate:")
        super.onDestroy()
    }
}