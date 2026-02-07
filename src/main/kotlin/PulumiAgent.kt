package com.example

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.asTools
import ai.koog.agents.ext.agent.chatAgentStrategy
import ai.koog.agents.ext.tool.AskUser
import ai.koog.agents.ext.tool.SayToUser
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleAnthropicExecutor
import com.example.config.ConfigHolder.config
import com.example.tools.PulumiTools
import kotlinx.coroutines.runBlocking

private val SYSTEM_PROMPT = """
    You are a Pulumi deployment assistant. You help users deploy and manage AWS infrastructure.

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

    val executor = simpleAnthropicExecutor(apiKey = config.anthropicKey)

    val agent = AIAgent(
        promptExecutor = executor,
        systemPrompt = SYSTEM_PROMPT,
        id = "pulumi-agent",
        strategy = chatAgentStrategy(),
        llmModel = OpenAIModels.Chat.GPT4o,
        toolRegistry = ToolRegistry {
            tools(PulumiTools(config.pulumiProjectDir).asTools())
            tool(SayToUser)
            tool(AskUser)
        },
        maxIterations = 20
    )

    val result = agent.run("Deploy an S3 bucket using Pulumi")
    println(result)
}
