package com.h.android.utils

import android.Manifest
import android.annotation.TargetApi
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import androidx.core.app.ActivityCompat

/**
 * @author zhangxiaohui
 * @describe 指纹密码
 * @date 2019/7/2
 */
object FingerUtil {
    private var cancellationSignal: CancellationSignal? = null
    fun cancelListener() {
        if (cancellationSignal != null && !cancellationSignal!!.isCanceled) {
            cancellationSignal!!.cancel()
            cancellationSignal = null
        }
    }

    /**
     * 指纹识别回调
     *
     * @param mContext
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun fingerAuthenticate(mContext: Context, fingerStatusListener: FingerStatusListener) {
        fingerStatusListener.fingerAuthenticationInit()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            fingerStatusListener.fingerAuthenticationFail(-1)
            return
        }
        val fingerprintManager = mContext.getSystemService(FingerprintManager::class.java)
        if (cancellationSignal == null) {
            cancellationSignal = CancellationSignal()
        }
        if (fingerprintManager == null) {
            fingerStatusListener.fingerAuthenticationFail(-1)
            return
        }
        //        fingerprintManager.authenticate(null, cancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
//            @Override
//            public void onAuthenticationError(int errorCode, CharSequence errString) {
//                super.onAuthenticationError(errorCode, errString);
//                if (errorCode == FingerprintManager.FINGERPRINT_ERROR_CANCELED) {
//                    return;
//                }
//                //指纹验证失败，不可再验
//                fingerStatusListener.fingerAuthenticationFail(-1);
//            }
//
//            @Override
//            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
//                super.onAuthenticationHelp(helpCode, helpString);
//                //指纹验证失败，可再验，可能手指过脏，或者移动过快等原因。
//                fingerStatusListener.fingerAuthenticationFail(-1);
//            }
//
//            @Override
//            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
//                super.onAuthenticationSucceeded(result);
//                //指纹验证成功
//                fingerStatusListener.fingerAuthenticationSuccess();
//            }
//
//            @Override
//            public void onAuthenticationFailed() {
//                super.onAuthenticationFailed();
//                //指纹验证失败，指纹识别失败，可再验，该指纹不是系统录入的指纹。
//                fingerStatusListener.fingerAuthenticationFail(-1);
//            }
//        }, null);
    }

    /**
     * 检查手机是否支持指纹(录入指纹)
     *
     * @return
     */
    fun supportFinger(mContext: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val fingerprintManager = mContext.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
                ?: return false
            //检查指纹权限
            if (ActivityCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.USE_FINGERPRINT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
            //            return fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints();
        }
        return false
    }

    /**
     * 检查指纹状态
     *
     * @return 0 未知，1 正常，2 版本过低， 4 没有指纹API，8 没有指纹权限， 16 未设置界面开启密码锁屏功能 32 没有录入指纹
     */
    fun getFingerPrintStatus(context: Context): Int {
        var result = 1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (hasFingerprintApi()) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.USE_FINGERPRINT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    result = 8
                } else {
                    val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                    // 如果没有设置密码锁屏，则不能使用指纹识别
                    if (keyguardManager == null || !keyguardManager.isKeyguardSecure) {
                        result = 16
                    }
                    val fingerprintManager = context.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
                    // 如果没有录入指纹，则不能使用指纹识别
//                    if (fingerprintManager == null || !fingerprintManager.hasEnrolledFingerprints()) {
//                        result = 32;
//                    }
                }
            } else {
                result = 4
            }
        } else {
            result = 2 //不支持指纹
        }
        return result
    }

    //检查是否具有指纹API
    fun hasFingerprintApi(): Boolean {
        try {
            val cls = Class.forName("android.hardware.fingerprint.FingerprintManager") // 通过反射判断是否存在该类
            return cls != null
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        return false
    }

    interface FingerStatusListener {
        /**
         * 初始状态
         */
        fun fingerAuthenticationInit()

        /**
         * 指纹识别成功
         */
        fun fingerAuthenticationSuccess()

        /**
         * 指纹识别失败
         *
         * @param errorCode -1 : 指纹识别失败
         */
        fun fingerAuthenticationFail(errorCode: Int)
    }
}