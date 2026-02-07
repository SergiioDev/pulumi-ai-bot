package com.example.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource
import com.sksamuel.hoplite.hocon.HoconParser


object ConfigHolder {
    val config by lazy { load<Config>() }
}


data class Config(val port: Int)

inline fun <reified T : Any> load(): T =
    ConfigLoaderBuilder.default()
        .addParser("conf", HoconParser())
        .addResourceSource("/application.conf")
        .build()
        .loadConfigOrThrow()