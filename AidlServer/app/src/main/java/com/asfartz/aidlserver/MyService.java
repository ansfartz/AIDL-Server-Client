package com.asfartz.aidlserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class MyService extends Service {

    /*
    IMathManager.Stub mBinder = new IMathManager.Stub() {
        @Override
        public int add(int x, int y) throws RemoteException {
            return x + y;
        }

        @Override
        public int substract(int x, int y) throws RemoteException {
            return x - y;
        }
    };
     */

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("MyService", "onBind: intent: getPackage = " + intent.getPackage()
                + " getAction = " + intent.getAction()
                + " getData = " + intent.getData()
                + " getComponent = " + intent.getComponent()
                + " getScheme = " + intent.getScheme()
                + " getDataString = " + intent.getDataString());
        return mathManagerImpl;
    }

    private MathManagerImpl mathManagerImpl = new MathManagerImpl();

    private class MathManagerImpl extends IMathManager.Stub {

        @Override
        public int add(int x, int y) throws RemoteException {
            return x + y;
        }

        @Override
        public int substract(int x, int y) throws RemoteException {
            return x - y;
        }
    }



}
