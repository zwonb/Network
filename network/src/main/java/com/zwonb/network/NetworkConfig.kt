package com.zwonb.network

import com.zwonb.network.model.BaseBean
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

    /**
     * 对一些code的默认实现
     */
    var toast: (suspend (String) -> Unit)? = null
    var otherLogin: (suspend (String) -> Unit)? = null
    var dialog: (suspend (String) -> Unit)? = null
    var noData: (suspend (String) -> Unit)? = null
    var unknown: (suspend (BaseBean<*>) -> Unit)? = null

}