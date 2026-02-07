package com.example.config

object ConfigHolder {
    val config by lazy { load<Config>() }
}

data class AwsConfig(
    val accessKeyId: String,
    val secretAccessKey: String,
    val sessionToken: String,
)

data class Config(
    val anthropicKey: String,
    val aws: AwsConfig,
)

fun AwsConfig.toPulumiEnvVars(): Map<String, String> = buildMap {
    put("AWS_ACCESS_KEY_ID", accessKeyId)
    put("AWS_SECRET_ACCESS_KEY", secretAccessKey)
    put("AWS_SESSION_TOKEN", sessionToken)
}
