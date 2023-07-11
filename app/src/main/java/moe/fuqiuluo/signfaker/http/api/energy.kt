package moe.fuqiuluo.signfaker.http.api

import com.tencent.mobileqq.qsec.qsecdandelionsdk.Dandelion
import com.tencent.mobileqq.sign.QQSecuritySign.EMPTY_BYTE_ARRAY
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import moe.fuqiuluo.signfaker.ext.Crypt
import moe.fuqiuluo.signfaker.http.ext.APIResult
import moe.fuqiuluo.signfaker.http.ext.failure
import moe.fuqiuluo.signfaker.http.ext.fetchGet
import moe.fuqiuluo.signfaker.http.ext.hex2ByteArray
import moe.fuqiuluo.signfaker.http.ext.toHexString
import moe.fuqiuluo.utils.MD5
import java.nio.ByteBuffer

fun Routing.energy() {
    get("/custom_energy") {
        val uin = fetchGet("uin")!!.toLong()
        val data = fetchGet("data")!!
        val salt = fetchGet("salt")!!.hex2ByteArray()

        val sign = Dandelion.energy(data, salt)
        call.respond(APIResult(if (sign == null) -1 else 0, "success", sign?.toHexString() ?: "null"))
    }

    get("/energy") {
        val data = fetchGet("data")!!
        if(!(data.startsWith("810_") || data.startsWith("812_"))) {
            failure(-2, "data参数不合法")
        }

        val mode = fetchGet("mode", def = when(data) {
            "810_d", "810_a", "810_f", "810_9" -> "v2"
            "810_2", "810_25", "810_7", "810_24" -> "v1"
            "812_a" -> "v3"
            "812_5" -> "v4"
            else -> ""
        })?.also {
            if (it.isBlank()) failure(-3, "无法自动决断mode，请主动提供")
        }

        val salt = when (mode) {
            "v1" -> {
                val uin = (fetchGet("uin") ?: return@get).toLong()
                val version = fetchGet("version",  err = "lack of version") ?: return@get
                val guid = (fetchGet("guid", err = "lack of guid") ?: return@get).hex2ByteArray()
                val salt = ByteBuffer.allocate(8 + 2 + guid.size + 2 + 10)
                salt.putLong(uin)
                salt.putShort(guid.size.toShort())
                salt.put(guid)
                salt.putShort(version.length.toShort())
                salt.put(version.toByteArray())
                salt.array()
            }
            "v2" -> {
                val version = fetchGet("version",  err = "lack of version") ?: return@get
                val guid = (fetchGet("guid", err = "lack of guid") ?: return@get).hex2ByteArray()
                val sub = data.substring(4).toInt(16)
                val salt = ByteBuffer.allocate(4 + 2 + guid.size + 2 + 10 + 4 + 4)
                salt.putInt(0)
                salt.putShort(guid.size.toShort())
                salt.put(guid)
                salt.putShort(version.length.toShort())
                salt.put(version.toByteArray())
                salt.putInt(sub)
                salt.putInt(0)
                salt.array()
            }
            "v3" -> { // 812_a
                val version = fetchGet("version",  err = "lack of version") ?: return@get
                val phone = (fetchGet("phone", err = "lack of phone") ?: return@get).toByteArray() // 86-xxx
                val salt = ByteBuffer.allocate(phone.size + 2 + 2 + version.length + 2)
                // 38 36 2D 31 37 33 36 30 32 32 39 31 37 32
                // 00 00
                // 00 06
                // 38 2E 39 2E 33 38
                // 00 00
                // result => 0C051B17347DF3B8EFDE849FC233C88DBEA23F5277099BB313A9CD000000004B744F7A00000000
                salt.put(phone)
                //println(String(phone))
                salt.putShort(0)
                salt.putShort(version.length.toShort())
                salt.put(version.toByteArray())
                salt.putShort(0)
                salt.array()
            }
            "v4" -> { // 812_5
                val receipt = (fetchGet("receipt", err = "lack of receipt") ?: return@get).toByteArray()
                val code = fetchGet("code", err = "lack of code") ?: return@get
                val key = MD5.toMD5Byte(code)
                val encrypt = Crypt().encrypt(receipt, key)
                val salt = ByteBuffer.allocate(receipt.size + 2 + encrypt.size)
                salt.put(receipt)
                salt.putShort(encrypt.size.toShort())
                salt.put(encrypt)
                salt.array()
            }
            else -> {
                EMPTY_BYTE_ARRAY
            }
        }

        val sign = Dandelion.energy(data, salt)

        call.respond(APIResult(if (sign == null) -1 else 0, "success", sign?.toHexString() ?: "null"))
    }
}