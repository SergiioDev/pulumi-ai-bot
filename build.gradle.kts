val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.3.0"
    id("io.ktor.plugin") version "3.4.0"
}

group = "com.example"
version = "0.0.1"

application {
    mainClass = "com.example.ApplicationKt"
}

kotlin {
    jvmToolchain(21)
}

ktor {
    openApi {
        enabled = true
        codeInferenceEnabled = true
    }
}

dependencies {
    // base dependencies
    implementation("io.ktor:ktor-server-compression")
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-swagger")
    implementation("io.ktor:ktor-server-routing-openapi")
    implementation("io.ktor:ktor-server-openapi")
    implementation("io.ktor:ktor-server-netty")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    // config
    implementation("com.sksamuel.hoplite:hoplite-core:2.9.0")
    implementation("com.sksamuel.hoplite:hoplite-hocon:2.9.0")


    // test
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
