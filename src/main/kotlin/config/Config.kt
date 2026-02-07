package com.example.config

object ConfigHolder {
    val config by lazy { load<Config>() }
}

data class AwsConfig(
    val accessKeyId: String,
    val secretAccessKey: String,
)

data class PulumiConfig(
    val stackName: String,
)

data class Config(
    val anthropicKey: String,
    val aws: AwsConfig,
    val pulumi: PulumiConfig
)

fun AwsConfig.toPulumiEnvVars(): Map<String, String> = buildMap {
    put("AWS_ACCESS_KEY_ID", accessKeyId)
    put("AWS_SECRET_ACCESS_KEY", secretAccessKey)
}
