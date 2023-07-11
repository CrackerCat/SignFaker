package moe.fuqiuluo.signfaker.proxy

import android.content.pm.ApplicationInfo
import moe.fuqiuluo.signfaker.logger.TextLogger.log

class ProxyApplicationInfo(
    val myApplicationInfo: ApplicationInfo
): ApplicationInfo() {
    init {
        targetSdkVersion = 26
        log("ProxyApplicationInfo.targetSdkVersion = 26")
        nativeLibraryDir = myApplicationInfo.nativeLibraryDir
        log("ProxyApplicationInfo.nativeLibraryDir = ${myApplicationInfo.nativeLibraryDir}")
    }
}