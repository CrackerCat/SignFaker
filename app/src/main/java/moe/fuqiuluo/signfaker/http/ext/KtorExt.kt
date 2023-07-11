package moe.fuqiuluo.signfaker.http.ext

import io.ktor.http.Parameters
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.Locale
import kotlin.experimental.xor

@Serializable
data class APIResult<T>(
    val code: Int,
    val msg: String = "",
    @Contextual
    val data: T? = null
)

suspend fun PipelineContext<Unit, ApplicationCall>.fetchGet(key: String, def: String? = null, err: String? = "Parameter '$key' is missing."): String? {
    val data = call.parameters[key] ?: def
    if (data == null && err != null) {
        failure(1, err)
    }
    return data
}

suspend fun PipelineContext<Unit, ApplicationCall>.fetchPost(params: Parameters, key: String, def: String? = null, err: String? = "Parameter '$key' is missing."): String? {
    val data = params[key] ?: def
    if (data == null && err != null) {
        failure(1, err)
    }
    return data
}

suspend fun PipelineContext<Unit, ApplicationCall>.failure(code: Int, msg: String) {
    call.respond(APIResult(code, msg, "failed"))
}

@JvmOverloads fun String.hex2ByteArray(replace: Boolean = false): ByteArray {
    val s = if (replace) this.replace(" ", "")
        .replace("\n", "")
        .replace("\t", "")
        .replace("\r", "") else this
    val bs = ByteArray(s.length / 2)
    for (i in 0 until s.length / 2) {
        bs[i] = s.substring(i * 2, i * 2 + 2).toInt(16).toByte()
    }
    return bs
}

@JvmOverloads fun ByteArray.toHexString(uppercase: Boolean = true): String = this.joinToString("") {
    (it.toInt() and 0xFF).toString(16)
        .padStart(2, '0')
        .let { s -> if (uppercase) s.lowercase(Locale.getDefault()) else s }
}

fun ByteArray.xor(key: ByteArray): ByteArray {
    val result = ByteArray(this.size)
    for (i in 0 until this.size) {
        result[i] = (this[i] xor key[i % key.size] xor ((i and 0xFF).toByte()))
    }
    return result
}

fun ByteArray.toAsciiHexString() = joinToString("") {
    if (it in 32..127) it.toInt().toChar().toString() else "{${
        it.toUByte().toString(16).padStart(2, '0').uppercase(
            Locale.getDefault())
    }}"
}