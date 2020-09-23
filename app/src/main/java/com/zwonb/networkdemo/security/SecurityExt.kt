package com.zwonb.networkdemo.security

/**
 * 安全相关拓展类
 *
 * @author zwonb
 * @date 2020/9/18
 */

fun String?.encrypt(): String {
    return this + "encrypt"
}

fun String?.decrypt(): String {
    return this + "decrypt"
}