package com.zwonb.network.dsl

import com.zwonb.network.upload.FileBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.util.concurrent.atomic.AtomicLong

/**
 * 上传请求 DSL
 *
 * @author zwonb
 * @date 2020/9/19
 */
class UploadDSL<T> : RequestDSL<T>() {

    /**
     * 装载需要上传的字段、file
     * uploadParts.addTextBody("xxx", "xxxxx")
     * val file = File(this@MainActivity.getExternalFilesDir(null)!!.path + "/upload20.mp4")
     * uploadParts.addFileBody("video", file) { uploaded(it) }
     * onRequest {
     *   apiCreate<ApiService>().uploadVideo(uploadParts)
     * }
     */
    val uploadParts by lazy(LazyThreadSafetyMode.NONE) { arrayListOf<MultipartBody.Part>() }

    private val uploadTotal by lazy { initUploadTotal() }
    private val uploadByte by lazy { AtomicLong(0) }
    private var progress: (suspend (Int) -> Unit)? = null
    private var callbackTime = 0L

    private fun initUploadTotal(): Long {
        var total = 0L
        for (part in uploadParts) {
            // 只监听文件的上传进度
            if (part.body() is FileBody) {
                total += part.body().contentLength()
            }
        }
        return total
    }

    /**
     * 需要监听上传的进度，每次上传一点都要调用
     * uploadParts.addFileBody("name", file) {
     *    uploaded(it)
     * }
     */
    fun uploaded(upload: Long) {
        // 所有文件当中的某一次上传都要调用
        val percent = (uploadByte.addAndGet(upload) * 100 / uploadTotal).toInt()
        if (canCallback(percent)) {
            progress?.let {
                // ？？？先这样切线程
                scope.launch(Dispatchers.Main) { it(percent) }
            }
        }
    }

    /**
     * 调用[uploaded]才会触发[progress]
     */
    fun onProgress(block: (suspend (Int) -> Unit)?) {
        progress = block
    }

    private fun canCallback(percent: Int): Boolean {
        val currentTimeMillis = System.currentTimeMillis()
        return if (percent >= 100 || currentTimeMillis - callbackTime >= 60) {
            callbackTime = currentTimeMillis
            true
        } else false
    }

}