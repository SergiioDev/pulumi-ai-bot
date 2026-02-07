package com.example.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addMapSource
import com.sksamuel.hoplite.addResourceSource
import com.sksamuel.hoplite.hocon.HoconParser
import io.github.cdimascio.dotenv.dotenv


object ConfigHolder {
    val config by lazy { load<Config>() }
}


data class Config(val pulumiProjectDir: String, val anthropicKey: String)

inline fun <reified T : Any> load(): T {
    val env = dotenv { ignoreIfMissing = true }
        .entries()
        .associateBy(
            { it.key }, { it.value }
        )


    return ConfigLoaderBuilder.default()
        .addParser("conf", HoconParser())
        .addMapSource(env)
        .addResourceSource("/application.conf")
        .build()
        .loadConfigOrThrow()
}