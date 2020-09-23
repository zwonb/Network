package com.zwonb.network.model

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * api service
 *
 * @author zwonb
 * @date 2020/9/22
 */
interface NetworkApi {

    @Multipart
    @POST("{path}/")
    suspend fun upload(
        @Path("path", encoded = true) path: String,
        @Part parts: List<MultipartBody.Part>
    ): BaseBean<JsonObject>

    @Streaming
    @GET
    suspend fun download(
        @Url url: String, @Header("Range") range: String = "bytes=0-"
    ): Response<ResponseBody>
}

fun Long.toDownloadRange() = "bytes=$this-"