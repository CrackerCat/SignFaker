package com.tencent.mobileqq.dt.app

import moe.fuqiuluo.signfaker.logger.TextLogger.log

object Dtc {
    private val cacheDB  = hashMapOf<String, String>()

    @JvmStatic
    fun mmKVValue(key: String): String {
        val ret =  when(key) {
            "o3_switch_Xwid" -> cacheDB.getOrDefault(key, "")

            else -> cacheDB.getOrDefault(key, "")
        }
        log("Dtc.mmkvValue(\"$key\") => \"$ret\"")
        return ret
    }

    @JvmStatic
    fun mmKVSaveValue(k: String, v: String) {
        log("Dtc.mmKVSaveValue(\"$k\", \"$v\")")
        cacheDB[k] = v
    }

}