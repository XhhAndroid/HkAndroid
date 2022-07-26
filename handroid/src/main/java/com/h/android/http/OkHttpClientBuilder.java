package com.h.android.http;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * 2020/11/20
 *
 * @author zhangxiaohui
 * @describe
 */
public class OkHttpClientBuilder {
    /**
     * 指定OKHttp线程池，不要每次都创建。
     */
    private static final ConnectionPool CONNECTION_POOL = new ConnectionPool();

    protected OkHttpClient.Builder builder = new OkHttpClient.Builder()
            .sslSocketFactory(createSSLSocketFactory(), new TrustAllManager())
            .hostnameVerifier(new TrustAllHostnameVerifier())
            .retryOnConnectionFailure(true)
            .writeTimeout(1, TimeUnit.MINUTES)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.MINUTES)
            .connectionPool(CONNECTION_POOL);

    public OkHttpClientBuilder addInterceptor(Interceptor interceptor) {
        builder.addInterceptor(interceptor);
        return this;
    }

    public OkHttpClientBuilder addNetworkInterceptor(Interceptor interceptor) {
        builder.addNetworkInterceptor(interceptor);
        return this;
    }

    public OkHttpClientBuilder cache(@Nullable Cache cache) {
        builder.cache(cache);
        return this;
    }

    public OkHttpClientBuilder cookieJar(@Nullable CookieJar cookieJar) {
        builder.cookieJar(cookieJar);
        return this;
    }

    public OkHttpClient build() {
        return builder.build();
    }


    /**
     * 默认信任所有的证书
     * 最好加上证书认证，主流App都有自己的证书
     *
     * @return
     */
    @SuppressLint("TrulyRandom")
    private static SSLSocketFactory createSSLSocketFactory() {

        SSLSocketFactory sSLSocketFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllManager()},
                    new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return sSLSocketFactory;
    }


    private static class TrustAllManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            if (TextUtils.isEmpty(hostname)) {
                return false;
            }
            return !Arrays.asList(VERIFY_HOST_NAME_ARRAY).contains(hostname);
        }
    }
    private static final String[] VERIFY_HOST_NAME_ARRAY = new String[]{};
}
