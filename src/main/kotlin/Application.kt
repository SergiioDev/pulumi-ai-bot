package com.example

import com.example.config.ConfigHolder.config
import com.example.routes.helloWorldRoute
import io.ktor.openapi.OpenApiInfo
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.routing

fun main() {
    embeddedServer(Netty, port = config.port) {
        install(Compression)

        configureRouting()
    }.start(wait = true)
}

fun Application.configureRouting() {
    routing {
        swaggerUI("/openapi") {
            info = OpenApiInfo("My API", "1.0.0")
        }

        helloWorldRoute()
    }
}
