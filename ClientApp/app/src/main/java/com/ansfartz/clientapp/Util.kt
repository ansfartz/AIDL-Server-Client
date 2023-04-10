package com.ansfartz.clientapp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo


class Util {

    companion object {

        fun implicitToExplicitIntent(intent: Intent, ctx: Context): Intent? {
            val pm: PackageManager = ctx.getPackageManager()
            val resolveInfoList: List<ResolveInfo> = pm.queryIntentServices(
                intent, PackageManager.ResolveInfoFlags.of(
                    PackageManager.MATCH_DEFAULT_ONLY.toLong()
                )
            )

            if (resolveInfoList == null || resolveInfoList.size != 1) {
                return null
            }

            val serviceInfo = resolveInfoList[0]
            val component = ComponentName(serviceInfo.serviceInfo.packageName, serviceInfo.serviceInfo.name)
            val explicitIntent = Intent(intent)
            explicitIntent.component = component
            return explicitIntent
        }

    }

}