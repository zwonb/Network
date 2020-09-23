package com.zwonb.network.upload

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File

/**
 * 文件表单类型
 *
 * @author zwonb
 * @date 2020/9/19
 */
class FileBody(
    private val file: File,
    private val contentType: String = "application/octet-stream",
    private val uploaded: ((Long) -> Unit)? = null
) : RequestBody() {

    override fun contentType(): MediaType? {
        return MediaType.parse(contentType)
    }

    override fun contentLength(): Long {
        return file.length()
    }

    override fun writeTo(sink: BufferedSink) {
        file.inputStream().use {
            val buffer = ByteArray(1024 * 8)
            var read = it.read(buffer)
            while (read >= 0) {
                sink.write(buffer, 0, read)
                uploaded?.invoke(read.toLong())
                read = it.read(buffer)
            }
        }
    }

}

fun ArrayList<MultipartBody.Part>.addFileBody(
    name: String,
    file: File,
    contentType: String = "multipart/form-data",
    uploaded: ((Long) -> Unit)? = null
) = add(
    MultipartBody.Part.createFormData(name, file.name, FileBody(file, contentType, uploaded))
)

fun ArrayList<MultipartBody.Part>.addImgBody(
    name: String,
    img: File,
    uploaded: ((Long) -> Unit)? = null
) = addFileBody(name, img, "image/jpeg", uploaded)

fun ArrayList<MultipartBody.Part>.addMP4Body(
    name: String,
    video: File,
    uploaded: ((Long) -> Unit)? = null
) = addFileBody(name, video, "video/mpeg4", uploaded)


