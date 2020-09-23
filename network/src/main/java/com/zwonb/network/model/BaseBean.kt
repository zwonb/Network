package com.zwonb.network.model

import androidx.annotation.Keep

/**
 * 网络请求返回的数据基类
 *
 * @author zwonb
 * @date 2020/9/19
 */
@Keep
data class BaseBean<T>(val ydCode: String, val ydMsg: String, val ydBody: T)

@Keep
data class ListBean<T>(val list: List<T>)

internal const val CODE_SUCCESS = "100"
internal const val CODE_TOAST = "101"
internal const val CODE_OTHER_LOGIN = "102"
internal const val CODE_DIALOG = "103"
internal const val CODE_NO_DATA = "104"