package com.zwonb.networkdemo.model

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * @author zwonb
 * @date 2020/9/19
 */
interface ApiService {

    @GET("article/listproject/0/json/")
    suspend fun getArticle(): String

    @FormUrlEncoded
    @POST("article/query/0/json/")
    suspend fun searchArticle(@Field("k") key: String): String

}

