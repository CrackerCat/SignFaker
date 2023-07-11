package moe.fuqiuluo.signfaker.http.api

import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import moe.fuqiuluo.signfaker.http.ext.APIResult

@Serializable
data class APIInfo(
    val version: String,
    val protocol: String
)

fun Routing.index() {
    get("/") {
        val out = APIResult(0, "IAA 云天明 章北海 王淼", APIInfo(
            version = "1.0.0",
            protocol = "v8.9.68"
        ))
        kotlin.runCatching {
            println(out)
            println(Json.encodeToString(out))
        }.onFailure {
            it.printStackTrace()
        }
        call.respond(out)
    }
}