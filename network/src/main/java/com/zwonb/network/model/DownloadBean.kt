package com.zwonb.network.model

/**
 * 下载数据类
 *
 * @author zwonb
 * @date 2020/9/22
 */
data class DownloadBean(val total: Long, var bytesLoaded: Long = 0L) {

    fun percent(): Int = (bytesLoaded * 100 / total).toInt()
}