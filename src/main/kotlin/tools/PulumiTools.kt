package com.example.tools

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet
import java.io.File

@LLMDescription("Tools for managing Pulumi infrastructure deployments")
class PulumiTools(private val projectDir: String) : ToolSet {
    private fun runPulumi(vararg args: String): String {
        val process = ProcessBuilder("pulumi", *args, "--non-interactive")
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
    fun preview(): String = runPulumi("preview")

    @Tool
    @LLMDescription("Deploy infrastructure changes. Creates, updates, or deletes real AWS resources. Only call this after the user has confirmed they want to proceed.")
    fun deploy(): String = runPulumi("up", "--yes")

    @Tool
    @LLMDescription("Destroy all infrastructure in the current stack. Only call this if the user explicitly asks to tear down resources.")
    fun destroy(): String = runPulumi("destroy", "--yes")

    @Tool
    @LLMDescription("Checks if user is logged in. Before any user request can be handled, this tool must be called to ensure the user is authenticated.")
    fun checkUserLoggedIn(): String = runPulumi("login")

    @Tool
    @LLMDescription("Creates a pulumi stack for the current project. Any action tool like deploy or destroy can not be completed without a stack")
    fun createStack(stackName: String): String = runPulumi("stack", "init", stackName)

    @Tool
    @LLMDescription("Checks if the user has a stack selected")
    fun retrieveCurrentStack(): String = runPulumi("stack")
}
