package com.example.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource
import com.sksamuel.hoplite.hocon.HoconParser
import java.nio.file.Path


object ConfigHolder {
    val config by lazy { load<Config>() }
}


data class Config(val pulumiProjectDir: String, val anthropicKey: String)

inline fun <reified T : Any> load(): T =
    ConfigLoaderBuilder.default()
        .addParser("conf", HoconParser())
        .addResourceSource("/application.conf")
        .build()
        .loadConfigOrThrow()