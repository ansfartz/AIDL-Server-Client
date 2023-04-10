package com.ansfartz.clientapp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ansfartz.clientapp.databinding.ActivityMainBinding
import com.ansfartz.serverapp.IMathManager
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    private var mathManager: IMathManager? = null
    private var isServiceConnected = false
    private val serviceConnection: ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "onServiceConnected: name = $name, service = $service")
            mathManager = IMathManager.Stub.asInterface(service)

            isServiceConnected = true
            updateConnectionStatusIcons(connected = true)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "onServiceDisconnected: name = $name")

            isServiceConnected = false
            updateConnectionStatusIcons(connected = false)
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            bindBtn.setOnClickListener {
                if (!isServiceConnected) {
                    var intent = Intent("com.ansfartz.service.AIDL")
                    intent.component = ComponentName("com.ansfartz.serverapp", "com.ansfartz.serverapp.MathService")
                    intent = implicitToExplicitIntent(intent)!!

                    val connectedOK = bindService(intent, serviceConnection, BIND_AUTO_CREATE)

                    if (connectedOK) isServiceConnected = true
                    else Snackbar.make(root, "Service could not bind", Snackbar.LENGTH_SHORT).show()

                } else
                    Snackbar.make(root, "Service already bound", Snackbar.LENGTH_SHORT).show()
            }

            unbindBtn.setOnClickListener {
                if (isServiceConnected) {
                    unbindService(serviceConnection)

                    updateConnectionStatusIcons(connected = false)
                    isServiceConnected = false
                } else
                    Snackbar.make(root, "Service already not bound", Snackbar.LENGTH_SHORT).show()

            }

            addRunBtn.setOnClickListener {
                try {
                    val a = addNo1Text.text.toString().toInt()
                    val b = addNo2Text.text.toString().toInt()
                    if (isServiceConnected) {
                        val result = mathManager!!.add(a, b)
                        addResultText.text = result.toString()
                    } else
                        Snackbar.make(root, "Service not bound", Snackbar.LENGTH_SHORT).show()

                } catch (e: NumberFormatException) {
                    Log.e(TAG, "addRunBtn: NumberFormatException ")
                } catch (e: RemoteException) {
                    Log.e(TAG, "addRunBtn: RemoteException")
                }
            }

            substractRunBtn.setOnClickListener {
                try {
                    val a = substractNo1Text.text.toString().toInt()
                    val b = substractNo2Text.text.toString().toInt()
                    if (isServiceConnected) {
                        val result = mathManager!!.substract(a, b)
                        substractResultText.text = result.toString()
                    } else
                        Snackbar.make(root, "Service not bound", Snackbar.LENGTH_SHORT).show()

                } catch (e: NumberFormatException) {
                    Log.e(TAG, "substractRunBtn: NumberFormatException ")
                } catch (e: RemoteException) {
                    Log.e(TAG, "substractRunBtn: RemoteException")
                }
            }
        }
    }

    private fun updateConnectionStatusIcons(connected: Boolean) {
        with(binding) {
            addRunBtn.setImageResource(
                if(connected) R.drawable.run_circle_ok_24
                else R.drawable.run_circle_error_24)
            substractRunBtn.setImageResource(
                if(connected) R.drawable.run_circle_ok_24
                else R.drawable.run_circle_error_24)
            connectedStatus.setImageResource(
                if(connected) R.drawable.run_circle_ok_24
                else R.drawable.run_circle_error_24)
        }
    }

    private fun implicitToExplicitIntent(intent: Intent): Intent? {
        val resolveInfoList: List<ResolveInfo> = packageManager.queryIntentServices(
            intent,
            PackageManager.ResolveInfoFlags.of(
                PackageManager.MATCH_DEFAULT_ONLY.toLong()
            )
        )

        if (resolveInfoList.size != 1) {
            return intent
        }

        val serviceInfo = resolveInfoList[0]
        val component = ComponentName(serviceInfo.serviceInfo.packageName, serviceInfo.serviceInfo.name)
        val explicitIntent = Intent(intent)
        explicitIntent.component = component
        return explicitIntent
    }

}