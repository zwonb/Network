package com.zwonb.network

import java.io.File

/**
 * File 拓展类
 *
 * @author zwonb
 * @date 2020/9/22
 */

/**
 * 保证文件的父文件夹已经存在
 */
fun File.smartCrateNewFile(): Boolean {
    if (exists()) {
        return true
    }
    if (parentFile!!.exists()) {
        return createNewFile()
    } else {
        if (parentFile!!.mkdirs()) {
            return createNewFile()
        }
    }
    return false
}