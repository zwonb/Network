package com.zwonb.network

import com.google.gson.Gson
import com.zwonb.network.dsl.DownloadDSL
import com.zwonb.network.dsl.NetworkDSL
import com.zwonb.network.dsl.RequestDSL
import com.zwonb.network.dsl.UploadDSL
import kotlinx.coroutines.CoroutineScope
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

/**
 * 创建 retrofit
 *
 * @author zwonb
 * @date 2020/9/19
 */
object ApiCreator {

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(NetworkConfig.url)
            .client(NetworkConfig.client ?: OkHttpClient())
            .apply {
                val callAdapter = NetworkConfig.converter
                if (!callAdapter.isNullOrEmpty()) {
                    for (factory in callAdapter) {
                        addConverterFactory(factory)
                    }
                }
            }
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .apply {
                val callAdapter = NetworkConfig.callAdapter
                if (!callAdapter.isNullOrEmpty()) {
                    for (factory in callAdapter) {
                        addCallAdapterFactory(factory)
                    }
                }
            }
            .build()
    }

    val downloadRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(NetworkConfig.url)
            .client(
                OkHttpClient().newBuilder()
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .build()
            )
            .build()
    }
}

val gson by lazy { Gson() }

fun CoroutineScope.httpString(dsl: NetworkDSL<String>.() -> Unit) =
    NetworkDSL<String>().apply(dsl).launch(this)

fun <T> CoroutineScope.httpRequest(dsl: RequestDSL<T>.() -> Unit) =
    RequestDSL<T>().apply(dsl).launch(this)

fun <T> CoroutineScope.httpUpload(dsl: UploadDSL<T>.() -> Unit) =
    UploadDSL<T>().apply(dsl).launch(this)

fun CoroutineScope.httpDownload(dsl: DownloadDSL.() -> Unit) =
    DownloadDSL().apply(dsl).launch(this)

inline fun <reified T> apiCreate(): T = ApiCreator.retrofit.create()
inline fun <reified T> downloadApiCreate(): T = ApiCreator.downloadRetrofit.create()
