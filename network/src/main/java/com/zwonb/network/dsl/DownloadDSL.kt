package com.zwonb.network.dsl

import com.zwonb.network.model.DownloadBean
import com.zwonb.network.smartCrateNewFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import java.io.RandomAccessFile

/**
 * 下载请求 DSL
 *
 * @author zwonb
 * @date 2020/9/22
 */
class DownloadDSL : NetworkDSL<Response<ResponseBody>>() {

    /**
     * 需要保存的文件
     */
    lateinit var saveFile: File
    val range: Long by lazy(LazyThreadSafetyMode.NONE) {
        val file = File(tmpFilePath())
        if (file.exists()) file.length() else 0
    }

    private var progress: (suspend (Int) -> Unit)? = null
    private var downloadComplete: (suspend () -> Unit)? = null

    private var callbackTime = 0L

    fun onProgress(block: suspend (Int) -> Unit) {
        progress = block
    }

    override fun onSuccess(block: suspend (Response<ResponseBody>) -> Unit) {
        throw Throwable("请重写onDownloadComplete(block)")
    }

    fun onDownloadComplete(block: suspend () -> Unit) {
        downloadComplete = block
    }

    override suspend fun analyzeResponse(response: Response<ResponseBody>) {
        withContext(Dispatchers.IO) {
            val body = response.body() ?: throw NullPointerException("body is null")
            val contentLength = body.contentLength()
            val byteStream = body.byteStream()

            val tmpFile = createTmpFile()
            val bean = DownloadBean(range + contentLength, range)

            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            byteStream.use {
                var read = it.read(buffer)
                while (read >= 0) {
                    if (!isActive) {
                        body.close()
                        tmpFile.close()
                    }

                    tmpFile.write(buffer, 0, read)
                    bean.bytesLoaded += read

                    if (canCallback(bean.percent())) {
                        withContext(Dispatchers.Main) {
                            progress?.invoke(bean.percent())
                        }
                    }
                    read = it.read(buffer)
                }

                File(tmpFilePath()).renameTo(saveFile)
                body.close()
                tmpFile.close()
            }
        }
        downloadComplete?.invoke()
    }

    private fun tmpFilePath() = saveFile.path + ".tmp"

    private fun createTmpFile(): RandomAccessFile {
        val tmpFilePath = tmpFilePath()
        File(tmpFilePath).apply {
            if (!exists()) smartCrateNewFile()
        }
        val tmpFile = RandomAccessFile(tmpFilePath, "rwd")
        if (range > 0) {
            tmpFile.seek(range)
        }
        return tmpFile
    }

    private fun canCallback(percent: Int): Boolean {
        val currentTimeMillis = System.currentTimeMillis()
        return if (percent >= 100 || currentTimeMillis - callbackTime >= 60) {
            callbackTime = currentTimeMillis
            true
        } else false
    }

}