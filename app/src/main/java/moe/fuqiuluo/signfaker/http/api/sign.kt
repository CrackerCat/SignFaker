package moe.fuqiuluo.signfaker.http.api

import com.tencent.mobileqq.qsec.qsecurity.QSec
import com.tencent.mobileqq.qsec.qsecurity.QSecConfig
import io.ktor.server.routing.Routing

import com.tencent.mobileqq.sign.QQSecuritySign
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.Serializable
import moe.fuqiuluo.signfaker.http.ext.APIResult
import moe.fuqiuluo.signfaker.http.ext.fetchGet
import moe.fuqiuluo.signfaker.http.ext.fetchPost
import moe.fuqiuluo.signfaker.http.ext.hex2ByteArray
import moe.fuqiuluo.signfaker.http.ext.toHexString


fun Routing.sign() {
    get("/sign") {
        val uin = fetchGet("uin")!!
        val qua = fetchGet("qua", QSecConfig.business_qua)!!
        val cmd = fetchGet("cmd")!!
        val seq = fetchGet("seq")!!.toInt()
        val buffer = fetchGet("buffer")!!.hex2ByteArray()
        val qimei36 = fetchGet("qimei36", def = QSecConfig.business_q36)!!

        requestSign(cmd, uin, qua, seq, buffer, qimei36)
    }

    post("/sign") {
        val param = call.receiveParameters()
        val uin = fetchPost(param, "uin")!!
        val qua = fetchPost(param, "qua", QSecConfig.business_qua)!!
        val cmd = fetchPost(param, "cmd")!!
        val seq = fetchPost(param, "seq")!!.toInt()
        val buffer = fetchPost(param, "buffer")!!.hex2ByteArray()
        val qimei36 = fetchPost(param, "qimei36", def = "")!!

        requestSign(cmd, uin, qua, seq, buffer, qimei36)
    }
}

@Serializable
private data class Sign(
    val token: String,
    val extra: String,
    val sign: String,
    val o3did: String
)

private suspend fun PipelineContext<Unit, ApplicationCall>.requestSign(cmd: String, uin: String, qua: String, seq: Int, buffer: ByteArray, qimei36: String = QSecConfig.business_q36) {
    fun int32ToBuf(i: Int): ByteArray {
        val out = ByteArray(4)
        out[3] = i.toByte()
        out[2] = (i shr 8).toByte()
        out[1] = (i shr 16).toByte()
        out[0] = (i shr 24).toByte()
        return out
    }

    val sign = QQSecuritySign.getSign(QSec, qua, cmd, buffer, int32ToBuf(seq), uin)!!

    call.respond(
        APIResult(0, "success", Sign(
        sign.token.toHexString(),
        sign.extra.toHexString(),
        sign.sign.toHexString(), QSecConfig.business_o3did ?: ""
    )))
}