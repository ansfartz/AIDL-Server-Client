# AIDL-Server-Client

##### Google Docs: https://developer.android.com/guide/components/aidl

Simple Proof-of-Concept ilustrating 2 applications (a server and a client) communicating using IPC (interprocess communication), by using an [AIDL](https://developer.android.com/guide/components/aidl) interface.  

Let's assume that the Server App contains some methods which do highly sophisticated calculations. We want the Client App to communicate with the Server App so that it too may call these sophisticated methods. In order for this to happen, these steps need to be followed:   
   1. Server App - Create an AIDL interface, whose abstract methods will be called by the Client App to communicate with the Server App. This interface needs to be made available to the Client App.
   2. Server App - Implement the AIDL interface's methods. Essentially whatever methods the Server App wants expose, they should be added in the interface and implemented. 
   3. Server App - Create a Service for the Client App to bind to. The return value of [onBind(): IBinder?](https://developer.android.com/reference/android/app/Service#onBind(android.content.Intent)) will be the AIDL interface's implementation from step 2.
   4. Client App - After including the shared AIDL interface, connect to the Server App's service and use the IBinder? from [onServiceConnected(ComponentName?, IBinder?)](https://developer.android.com/reference/android/content/ServiceConnection#onServiceConnected(android.content.ComponentName,%20android.os.IBinder)) to hold a reference to the AIDL interface's remote implementation (returned in step 3).

___

# AIDLServer
### 1.  The .aidl file

In the _project_ structure of your current project, go to **app/src/main**, Right Click on **main** > New > AIDL > AIDL File.  Create the IMathManager.aidl file and its methods. The aidl file will generate the required interface that both the client application and the server will be using.
```java
interface IMathManager {
    int add(in int x, in int y);
    int substract(in int x, in int y);
}
```

Now, after rebuilding the project, you should have in the Android project structure, in the **java *(generated)*** folder the IMathManager interface. Since it has been generated by the Android framework, we won't be modifying anything here. Notice however the Stub abstract class inside it. That is what we'll be using to communicate between processes.

### 2.  The Service

Create a new class extending Service, we'll call it MathService. It will have to override the public IBinder ```onBind(Intent intent)``` method, which returns an IBinder object, through which clients can call on to the service.

We'll have to declare the IBinder object however, before returning it in the onBind method.
```java
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
```
_Note: creating a class that extends IMathManager.Stub is optional. You could just create an annonymous implementation like this:_

_``` IMathManager mathManager = new IMathManager.Stub() { ... } ```_

_Creating a class that extends our Stub however means that we can add additional variables and methods to it, private to it._


The IMathManager.Stub class extends the Binder object and implements our .aidl file, the IMathManager interface, which means that this is where we are going to define our methods, and that this is what the client is going to receive from the onBind method.

All that is left now is to return our ```mathManagerImpl``` object in the onBind method:
```java
    @Override
    public IBinder onBind(Intent intent) {
        return mathManagerImpl;
    }
```

### 3.  Adding it to the AndroidManifest.xml

```xml
    <application...>
        
        ....

        <service
            android:name="com.asfartz.aidlserver.MathService"
            android:enabled="true"
            android:exported="true">

            <intent-filter>
                <action android:name="com.asfartz.service.MYSERVICE"/>
                <category android:name="android.intent.category.DEFAULT"/>
            </intent-filter>

        </service>

        ...
    </application>        
```
##### source: https://developer.android.com/guide/components/intents-filters#Types
**Implicit intent** = specifies an action that can invoke any app on the device able to perform the action.  
**Explicit intent** = used to launch a specific app component, such as a particular activity or service in your app.  
We are doing this because we're going to need the component name ( ```android:name``` in intent-filter ) to identify the service in the Client app, and pass it to the explicit intent.

*Note: If there are multiple Services with matching ```intent-filter```s, the Android framework will choose the one with highest priority. If multiple Services have highest priority, then a random one will be picked.* In our case, we should make sure no other services on the device have this ```intent-filter``` name.




# AIDLClient
### 1.  The .aidl file

The .aidl file must be the exact same one, it can't have any modifications. And it must have the same package name as the one on the server (in this case: ```com.asfartz.aidlserver``` )

### 2. Creating a ServiceConnection object

A ServiceConnection is an interface with 2 methods: ```onServiceConnected(ComponentName name, IBinder service)``` and ```onServiceDisconnected(ComponentName name)```. We'll be focusing on the first method, since we'll be using the IBinder parameter to initialize our IMathManager interface, which the client will be using for making calls to the server's MathService.

```java
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
```

### 3. Binding to the Service

We must use an explicit Intent, and pass the component name to it, as such:
```java
    Intent intent = toExplicitIntent (new Intent("com.asfartz.service.MYSERVICE"));
    bindService(intent, serviceConnection, BIND_AUTO_CREATE);


public Intent toExplicitIntent(Intent implicitIntent) {
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
```

Omitting the ```toExplicitIntent(Intent implicitIntent)``` method will cause an ```IllegalArgumentException```, because the  serviceIntent argument of bindService must be an explicit IntentI




# Running the apps
The Server app is not mandatory
