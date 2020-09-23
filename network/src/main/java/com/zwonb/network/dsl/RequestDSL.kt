package com.zwonb.network.dsl

import com.zwonb.network.model.*

/**
 * get post 请求 DSL
 *
 * @author zwonb
 * @date 2020/9/19
 */
open class RequestDSL<T> : NetworkDSL<BaseBean<T>>() {

    private var toast: (suspend (String) -> Unit)? = null
    private var otherLogin: (suspend (String) -> Unit)? = null
    private var dialog: (suspend (String) -> Unit)? = null
    private var noData: (suspend (String) -> Unit)? = null
    private var unknown: (suspend (BaseBean<T>) -> Unit)? = null

    fun onToast(block: (suspend (String) -> Unit)?) {
        toast = block
    }

    fun onOtherLogin(block: (suspend (String) -> Unit)?) {
        otherLogin = block
    }

    fun onDialog(block: (suspend (String) -> Unit)?) {
        dialog = block
    }

    fun onNoData(block: (suspend (String) -> Unit)?) {
        noData = block
    }

    // ydCode 未定义
    fun onUnknown(block: (suspend (BaseBean<T>) -> Unit)?) {
        unknown = block
    }

    override suspend fun analyzeResponse(response: BaseBean<T>) {
        when (response.ydCode) {
            CODE_SUCCESS -> success(response)
            CODE_TOAST -> toast?.invoke(response.ydMsg)
            CODE_OTHER_LOGIN -> otherLogin?.invoke(response.ydMsg)
            CODE_DIALOG -> dialog?.invoke(response.ydMsg)
            CODE_NO_DATA -> noData?.invoke(response.ydMsg)
            else -> unknown?.invoke(response)
        }
    }

}