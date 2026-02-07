val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.serialization") version "2.3.0"
    id("io.ktor.plugin") version "3.4.0"
}

group = "com.example"
version = "0.0.1"

application {
    mainClass = "com.example.PulumiAgentKt"
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation("ch.qos.logback:logback-classic:$logback_version")

    // config
    implementation("com.sksamuel.hoplite:hoplite-core:2.9.0")
    implementation("com.sksamuel.hoplite:hoplite-hocon:2.9.0")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.2")

    // ai agent
    implementation("ai.koog:koog-agents:0.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

    // test
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
