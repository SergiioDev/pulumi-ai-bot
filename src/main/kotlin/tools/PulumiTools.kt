package com.example.tools

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet
import java.io.File

@LLMDescription("Tools for managing Pulumi infrastructure deployments")
class PulumiTools(private val projectDir: String) : ToolSet {
    private fun runPulumi(vararg args: String): String {
        val process = ProcessBuilder("pulumi", *args)
            .directory(File(projectDir))
            .redirectErrorStream(true)
            .start()

        val output = process.inputStream.bufferedReader().readText()
        val exitCode = process.waitFor()

        return if (exitCode == 0) {
            output
        }
        else {
            "Command failed (exit code $exitCode):\n$output"
        }
    }

    @Tool
    @LLMDescription("Preview infrastructure changes. Shows what will be created, updated, or deleted without making any actual changes.")
    fun preview(): String = runPulumi("preview", "--non-interactive")

    @Tool
    @LLMDescription("Deploy infrastructure changes. Creates, updates, or deletes real AWS resources. Only call this after the user has confirmed they want to proceed.")
    fun deploy(): String = runPulumi("up", "--yes", "--non-interactive")

    @Tool
    @LLMDescription("Destroy all infrastructure in the current stack. Only call this if the user explicitly asks to tear down resources.")
    fun destroy(): String = runPulumi("destroy", "--yes", "--non-interactive")
}
