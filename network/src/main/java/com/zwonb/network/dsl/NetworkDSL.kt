package com.zwonb.network.dsl

import android.util.Log
import kotlinx.coroutines.*

/**
 * 网络请求 DSL
 *
 * @author zwonb
 * @date 2020/9/19
 */
open class NetworkDSL<T> {

    private var start: (suspend () -> Unit)? = null
    internal lateinit var request: suspend () -> T
    internal lateinit var success: (suspend (T) -> Unit)
    private var error: (suspend (Throwable) -> Unit)? = null

    lateinit var scope: CoroutineScope

    fun onStart(block: (suspend () -> Unit)?) {
        start = block
    }

    fun onRequest(block: suspend () -> T) {
        request = block
    }

    open fun onSuccess(block: suspend (T) -> Unit) {
        success = block
    }

    fun onError(block: (suspend (Throwable) -> Unit)?) {
        error = block
    }

    fun launch(scope: CoroutineScope) = scope.launch(Dispatchers.Main) {
        try {
            this@NetworkDSL.scope = scope
            start?.invoke()
            val response = withContext(Dispatchers.IO) {
                request()
            }
            analyzeResponse(response)
        } catch (e: Throwable) {
            Log.e("zwonb", "requestError: ", e)
            if (e !is CancellationException) {
                error?.invoke(e)
            }
        }
    }

    open suspend fun analyzeResponse(response: T) {
        success(response)
    }

}