package com.zwonb.network.upload

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink

/**
 * 文本类型 text/plain
 *
 * @author zwonb
 * @date 2020/9/19
 */
class TextBody(val key: String, val value: String, private val uploaded: ((Long) -> Unit)? = null) :
    RequestBody() {

    override fun contentType(): MediaType? {
        return MediaType.parse("text/plain; charset=UTF-8")
    }

    override fun contentLength() = try {
        value.toByteArray().size.toLong()
    } catch (e: Exception) {
        super.contentLength()
    }

    override fun writeTo(sink: BufferedSink) {
        value.byteInputStream().use {
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

fun ArrayList<MultipartBody.Part>.addTextBody(
    key: String,
    value: String
) = add(MultipartBody.Part.createFormData(key, null, TextBody(key, value, null)))