package com.zwonb.network

import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter

/**
 * 网络配置
 *
 * @author zwonb
 * @date 2020/9/19
 */
object NetworkConfig {

    lateinit var url: String
    var client: OkHttpClient? = null
    var converter: List<Converter.Factory>? = null
    var callAdapter: List<CallAdapter.Factory>? = null

}