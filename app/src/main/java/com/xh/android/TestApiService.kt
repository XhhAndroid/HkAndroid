package com.xh.android

import com.h.android.http.annotation.BaseUrlProvider
import com.xh.android.module.TestModel
import retrofit2.http.POST

/**
 *2021/1/22
 *@author zhangxiaohui
 *@describe
 */
@BaseUrlProvider(MiBaseUrlProvider::class)
interface TestApiService {

    @POST("text/api")
    suspend fun textApi() : TestModel
}