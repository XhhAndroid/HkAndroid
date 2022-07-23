package com.h.android.http

import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * 2020/11/20
 *
 * @author zhangxiaohui
 * @describe
 */
class HApiManager private constructor() {

    fun <T> getApiService(apiClazz: Class<T>?): T {
        return Hhttp.getApiService(apiClazz)
    }

    companion object {
        private var apiManager: HApiManager? = null
        fun get(): HApiManager {
            if (apiManager == null) {
                apiManager = HApiManager()
            }
            return apiManager!!
        }
    }

    /**
     * 简单的同步请求
     */
    fun getRequestSync(url: String): ResponseBody {
        val okHttpClient = OkHttpClient.Builder()
            .sslSocketFactory(createSSLSocketFactory()!!,TrustAllCerts())
        val request: Request = Request.Builder()
            .url(url)
            .build()
        val response = okHttpClient.build().newCall(request).execute()
        return response.body()!!
    }

    /**
     * 简单异步请求
     */
    fun getRequestAsync(url: String, callback: Callback) {
        val okHttpClient = OkHttpClient().newBuilder().sslSocketFactory(createSSLSocketFactory()!!,TrustAllCerts()).connectTimeout(3, TimeUnit.SECONDS).build()
        val request: Request = Request.Builder()
            .url(url)
            .build()
        okHttpClient.newCall(request).enqueue(callback)
    }

    private fun createSSLSocketFactory(): SSLSocketFactory? {
        var ssfFactory: SSLSocketFactory? = null

        try {
            val sc = SSLContext.getInstance("TLS")
            sc.init(null, arrayOf<TrustManager>(TrustAllCerts()), SecureRandom())
            ssfFactory = sc.socketFactory
        } catch (e: Exception) {
        }

        return ssfFactory
    }

    private class TrustAllCerts : X509TrustManager {
        override fun getAcceptedIssuers(): Array<X509Certificate?> {
            return arrayOfNulls(0)
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        @Throws(CertificateException::class)
        override fun checkClientTrusted(x509Certificates: Array<X509Certificate>, s: String) {
        }
    }
}