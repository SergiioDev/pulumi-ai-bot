package com.example.utils

fun checkPulumiInstalled() {
    runCatching {
        val process = ProcessBuilder("pulumi", "version")
            .start()
        val exitCode = process.waitFor()
        if (exitCode != 0) {
            error("Pulumi CLI returned exit code $exitCode. Please ensure Pulumi is installed correctly.")
        }
    }.onFailure {
        error("Pulumi CLI is not installed or not found in PATH. Install it from https://www.pulumi.com/docs/install/")
    }
}