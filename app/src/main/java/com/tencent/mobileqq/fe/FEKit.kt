package com.tencent.mobileqq.fe

import com.tencent.mobileqq.dt.Dtn
import com.tencent.mobileqq.fe.utils.DeepSleepDetector
import com.tencent.mobileqq.qsec.qsecurity.QSec
import com.tencent.mobileqq.sign.QQSecuritySign
import moe.fuqiuluo.signfaker.logger.TextLogger.log
import moe.fuqiuluo.signfaker.proxy.ProxyContext
import java.io.File

object FEKit {
    fun init(proxyContext: ProxyContext) {
        kotlin.runCatching {
            log("尝试载入FEKIT二进制库...")
            System.loadLibrary("fekit")
            log("载入FEKIT二进制库成功...")

            DeepSleepDetector.startCheck()

            QQSecuritySign.initSafeMode(false)
            log("设置安全模式 = false")

            val file = File(proxyContext.getFilesDirV2(), "5463306EE50FE3AA")
            if (!file.exists()) {
                log("目录`5463306EE50FE3AA`不存在，创建成功！")
                file.mkdirs()
            }
            Dtn.initContext(proxyContext, file.absolutePath)
            log("初始化Dtn成功")
            Dtn.initLog(object: IFEKitLog() {
                override fun d(str: String, i2: Int, str2: String) {
                    log("FEKitLogDebug $str: $str2")
                }

                override fun e(str: String?, i2: Int, str2: String?) {
                    log("FEKitLogErr $str: $str2")
                }

                override fun i(str: String?, i2: Int, str2: String?) {
                    log("FEKitLogInfo $str: $str2")
                }

                override fun v(str: String?, i2: Int, str2: String?) {
                    log("FEKitLogV $str: $str2")
                }

                override fun w(str: String?, i2: Int, str2: String?) {
                    log("FEKitLogWarn $str: $str2")
                }
            })
            log("尝试初始化Xwid for empty uin")
            Dtn.initUin("0")
            log("初始化init_uin成功")

            QSec.doSomething(proxyContext, 1)

            log("FEKIT初始化结束")
        }.onFailure {
            log("错误：${it.stackTraceToString()}")
        }
    }


}