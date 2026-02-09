package com.example.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.addMapSource
import com.sksamuel.hoplite.addResourceSource
import com.sksamuel.hoplite.hocon.HoconParser
import io.github.cdimascio.dotenv.dotenv

val ENV_KEY_MAPPING = mapOf(
    "AWS_ACCESS_KEY_ID" to "aws.accessKeyId",
    "AWS_SECRET_ACCESS_KEY" to "aws.secretAccessKey",
    "AWS_SESSION_TOKEN" to "aws.sessionToken",
    "ANTHROPIC_KEY" to "anthropicKey",
    "STACK_NAME" to "stackName"
 )

@OptIn(ExperimentalHoplite::class)
inline fun <reified T : Any> load(): T {
    val env = dotenv { ignoreIfMissing = true }
        .entries()
        .associate { (ENV_KEY_MAPPING[it.key] ?: it.key) to it.value }

    return ConfigLoaderBuilder.default()
        .addParser("conf", HoconParser())
        .addMapSource(env)
        .withExplicitSealedTypes()
        .addResourceSource("/application.conf")
        .build()
        .loadConfigOrThrow()
}