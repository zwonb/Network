package com.zwonb.networkdemo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.JsonObject
import com.zwonb.network.*
import com.zwonb.network.model.NetworkApi
import com.zwonb.network.model.toDownloadRange
import com.zwonb.network.upload.addFileBody
import com.zwonb.network.upload.addTextBody
import com.zwonb.networkdemo.model.ApiService
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        text.setOnClickListener { requestPost() }
    }

    private fun requestGet() {
        lifecycleScope.httpString {
            onStart { text.text = "请求中..." }
            onRequest { apiCreate<ApiService>().getArticle() }
            onSuccess {
                text.text = it
            }
            onError {
                text.text = it.message
            }
        }
    }

    private fun requestPost() {
        lifecycleScope.httpString {
            onStart { text.text = "请求中..." }
            onRequest { apiCreate<ApiService>().searchArticle("android11") }
            onSuccess {
                text.text = it
            }
            onError {
                text.text = it.message
            }
        }
    }

    private fun requestUpload() {
        lifecycleScope.httpUpload<JsonObject> {
            uploadParts.addTextBody("act", "postVideo")
            uploadParts.addTextBody("recordId", "6692")
            val file =
                File(this@MainActivity.getExternalFilesDir(null)!!.path + "/upload20.mp4")
            uploadParts.addFileBody("video", file) { uploaded(it) }
            onStart { text.text = "请求中..." }
            onRequest {
                apiCreate<NetworkApi>().upload("operate", uploadParts)
            }
            onProgress {
                Log.e("zwonb", "requestUpload== $it %")
                text.text = it.toString()
            }
            onSuccess {
                text.text = it.ydBody.toString()
                Toast.makeText(this@MainActivity, it.toString(), Toast.LENGTH_SHORT).show()
            }
            onToast {
                text.text = it
                Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
            }
            onError {
                text.text = it.message
                Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestDownload() {
        lifecycleScope.httpDownload {
            saveFile =
                File(this@MainActivity.getExternalFilesDir(null)!!.path + "/download.mp4")
            onStart {
                text.text = "下载中..."
            }
            onRequest {
                downloadApiCreate<NetworkApi>().download(
                    "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319212559089721.mp4",
                    range.toDownloadRange()
                )
            }
            onProgress {
                Log.e("zwonb", "requestUpload== $it %")
                text.text = it.toString()
            }
            onDownloadComplete {
                text.text = saveFile.toString()
            }
            onError {
                text.text = it.message
                Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}