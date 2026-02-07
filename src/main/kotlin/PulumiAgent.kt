package com.example

import ai.koog.agents.core.agent.AIAgentService
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.asTools
import ai.koog.agents.ext.agent.chatAgentStrategy
import ai.koog.agents.ext.tool.AskUser
import ai.koog.agents.ext.tool.SayToUser
import ai.koog.prompt.executor.clients.anthropic.AnthropicModels
import ai.koog.prompt.executor.llms.all.simpleAnthropicExecutor
import com.example.config.ConfigHolder.config
import com.example.tools.PulumiTools
import kotlinx.coroutines.runBlocking

private val SYSTEM_PROMPT = """
    You are a Pulumi deployment assistant. You help users deploy and manage AWS infrastructure..

    This is a proof of concept. Your scope is limited to S3 bucket operations only (deploy, preview, destroy).
    If the user asks about other resource types, let them know this POC only supports S3 buckets.

    Follow this workflow strictly:
    1. Run a preview first to see what changes will be made
    2. Show the preview results to the user
    3. Ask the user if they want to proceed with deployment
    4. Only deploy if the user explicitly confirms
    5. After deployment, show the results to the user

    Be concise. Always explain what resources will be created, updated, or deleted before deploying.

    After a request from the user, do a plan, show the plan to the user and after the implementation provide a short
    summary to the user of what you just did.
""".trimIndent()


fun main() = runBlocking {
    checkPulumiInstalled()

    val executor = simpleAnthropicExecutor(apiKey = config.anthropicKey)

    val agentService = AIAgentService(
        promptExecutor = executor,
        systemPrompt = SYSTEM_PROMPT,
        strategy = chatAgentStrategy(),
        llmModel = AnthropicModels.Sonnet_4_5,
        toolRegistry = ToolRegistry {
            tools(PulumiTools(config.pulumiProjectDir).asTools())
            tool(SayToUser)
            tool(AskUser)
        },
        maxIterations = 20
    )

    println("Pulumi AI Agent ready. Type your request (or 'exit' to quit):")
    while (true) {
        print("\n> ")
        val input = readlnOrNull()?.trim() ?: break

        if (input.equals("exit", ignoreCase = true)) break

        if (input.isEmpty()) continue

        val result = agentService.createAgentAndRun(input)
        println(result)
    }
}
