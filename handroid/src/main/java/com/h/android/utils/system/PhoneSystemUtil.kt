package com.h.android.utils.system

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Proxy
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import com.h.android.HAndroid

/**
 * @author zhangxiaohui
 * @describe 手机系统相关
 * @date 2019/6/21
 */
class PhoneSystemUtil {
    /**
     * 引导用户开启通知
     * @param context
     */
    private fun goToNotificationSetting(context: Context) {
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= 26) {
            // android 8.0引导
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
        } else if (Build.VERSION.SDK_INT >= 21) {
            // android 5.0-7.0
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("app_package", context.packageName)
            intent.putExtra("app_uid", context.applicationInfo.uid)
        } else {
            // 其他
            intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
            intent.data = Uri.fromParts("package", context.packageName, null)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    /**
     * 判断当前应用是否在前台
     * 系统签名的应用，可以获取其他应用进程，当前是否在前台 ，非系统签名应用只能获取自己的
     */
    fun isRunningForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses ?: return false
        val packageName = context.applicationInfo.packageName
        for (runningAppProcessInfo in runningAppProcesses) {
            if (packageName.equals(
                    runningAppProcessInfo.processName,
                    ignoreCase = true
                ) && runningAppProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            ) {
                return true
            }
        }
        return false
    }

    fun checkVPN(): Boolean {
        //don't know why always returns null:
        val connMgr = HAndroid.getApplication()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_VPN)
        return networkInfo?.isConnected ?: false
    }

    /*
    * 判断设备是否正在抓包
    * */
    fun isWifiProxy(context: Context): Boolean {
        val IS_ICS_OR_LATER: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
        val proxyAddress: String
        val proxyPort: Int
        if (IS_ICS_OR_LATER) {
            proxyAddress = if(System.getProperty("http.proxyHost") == null) "" else System.getProperty("http.proxyHost")
            val portStr = System.getProperty("http.proxyPort")
            proxyPort = (portStr ?: "-1").toInt()
        } else {
            proxyAddress = Proxy.getHost(context)
            proxyPort = Proxy.getPort(context)
        }
        return !TextUtils.isEmpty(proxyAddress) && proxyPort != -1
    }
}