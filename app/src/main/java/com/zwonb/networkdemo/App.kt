package com.zwonb.networkdemo

import android.app.Application
import android.content.Context
import com.zwonb.network.NetworkConfig
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * @author zwonb
 * @date 2020/9/21
 */
class App : Application() {

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this.applicationContext

        NetworkConfig.apply {
            url = "https://wanandroid.com/"
            client = OkHttpClient().newBuilder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
//                .addInterceptor(HeadersInterceptor())
//                .addInterceptor(SignInterceptor())
                .build()
//            converter = listOf(YdBodyConverterFactory.create())
        }
    }
}

val appContext by lazy { App.context }