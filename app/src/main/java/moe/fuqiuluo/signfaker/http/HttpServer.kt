@file:OptIn(DelicateCoroutinesApi::class)
package moe.fuqiuluo.signfaker.http

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import moe.fuqiuluo.signfaker.http.api.energy
import moe.fuqiuluo.signfaker.http.api.index
import moe.fuqiuluo.signfaker.http.ext.APIResult

object HttpServer {
    operator fun invoke(port: Int) {
        GlobalScope.launch {
            embeddedServer(Netty, port = port, host = "0.0.0.0") {
                module()
            }.start(wait = false)
        }
    }

    private fun Application.module() {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
        install(StatusPages) {
            exception<Throwable> { call, cause ->
                call.respond(APIResult(1, cause.message ?: cause.javaClass.name, call.request.uri))
            }
        }
        routing {
            index()
            energy()
        }
    }
}