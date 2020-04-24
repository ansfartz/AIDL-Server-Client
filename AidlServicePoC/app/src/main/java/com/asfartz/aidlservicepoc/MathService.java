package com.asfartz.aidlservicepoc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class MathService extends Service {

    private static final String TAG = "Andy";

    public MathService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: intent = " + intent);
        Log.d(TAG, "onBind: returning mBinder");
        return mBinder;
    }

    IMathService.Stub mBinder = new IMathService.Stub() {
        @Override
        public int add(int x, int y) throws RemoteException {
            return x + y;
        }

        @Override
        public int substract(int x, int y) throws RemoteException {
            return x - y;
        }
    };

}
