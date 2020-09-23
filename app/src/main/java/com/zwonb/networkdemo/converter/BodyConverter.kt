package com.zwonb.networkdemo.converter

import android.util.Log
import com.google.gson.JsonObject
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.zwonb.network.gson
import com.zwonb.networkdemo.BuildConfig
import com.zwonb.networkdemo.security.decrypt
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * 天天电信响应body处理
 *
 * @author zwonb
 * @date 2020/9/18
 */
class YdBodyConverterFactory private constructor() : Converter.Factory() {

    companion object {
        fun create() = YdBodyConverterFactory()
    }

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? = when (type) {
        String::class.java -> null
        JsonObject::class.java -> null
        JSONObject::class.java -> JSONObjectConverter()
        else -> YdBodyConverter(gson.getAdapter(TypeToken.get(type)))
    }
}

class YdBodyConverter<T>(private val adapter: TypeAdapter<T>) : Converter<ResponseBody, T> {

    override fun convert(value: ResponseBody): T? {
        val jObj = JSONObject(value.string())
        val code = jObj.getString("ydCode")
        val msg = jObj.getString("ydMsg")
        val body = jObj.getString("ydBody").decrypt()
        val jsonStr = """{"ydCode":"$code","ydMsg":"$msg","ydBody":$body}"""
        if (BuildConfig.DEBUG) {
            Log.e("zwonb", "请求数据:\n$jsonStr")
        }
        return adapter.fromJson(jsonStr)
    }
}

class JSONObjectConverter : Converter<ResponseBody, JSONObject> {
    override fun convert(value: ResponseBody) = JSONObject(value.string())
}