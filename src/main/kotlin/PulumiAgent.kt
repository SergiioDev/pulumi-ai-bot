package com.example

import ai.koog.agents.core.agent.AIAgentService
import ai.koog.agents.core.agent.context.RollbackStrategy
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.asTools
import ai.koog.agents.ext.agent.chatAgentStrategy
import ai.koog.agents.ext.tool.AskUser
import ai.koog.agents.snapshot.feature.Persistence
import ai.koog.agents.snapshot.providers.InMemoryPersistenceStorageProvider
import ai.koog.prompt.executor.clients.anthropic.AnthropicModels
import ai.koog.prompt.executor.llms.all.simpleAnthropicExecutor
import com.example.aws.S3RuntimeImpl
import com.example.config.ConfigHolder.config
import com.example.stack.StackRuntimeImpl
import com.example.tools.PulumiTools
import kotlinx.coroutines.runBlocking

private val SYSTEM_PROMPT = """
    You are a Pulumi deployment assistant. You help users deploy and manage AWS S3 buckets.

    This is a proof of concept. Your scope is limited to S3 bucket operations only (deploy, preview, destroy).
    If the user asks about other resource types, let them know this POC only supports S3 buckets.

    To help the user if he wants to create a resource inside a region please show some examples of aws regions.

    Be concise. Always explain what resources will be created, updated, or deleted before deploying.

    After a request from the user, do a plan, show the plan to the user and after the implementation provide a short
    summary to the user of what you just did.
    
    Rules:
    - Ask the user for the AWS region and bucket name if they haven't provided them
    - Before deleting adding/deleting resources **always run a preview** first.
    - If the user confirms, run the deployment.
""".trimIndent()

fun main() = runBlocking {
    val executor = simpleAnthropicExecutor(apiKey = config.anthropicKey)

    val agentService = AIAgentService(
        promptExecutor = executor,
        systemPrompt = SYSTEM_PROMPT,
        strategy = chatAgentStrategy(),
        llmModel = AnthropicModels.Sonnet_4_5,
        toolRegistry = ToolRegistry {
            tools(
                    PulumiTools(
                    s3Runtime = S3RuntimeImpl(),
                    stackRuntime = StackRuntimeImpl()
                    ).asTools()
            )
            tool(AskUser)
        },
        maxIterations = 1000
    ) {
        install(Persistence) {
            storage = InMemoryPersistenceStorageProvider()
            enableAutomaticPersistence = true
            rollbackStrategy = RollbackStrategy.MessageHistoryOnly
        }
    }.createAgent()

    println("")

    val result = agentService.run("Hello! I'm ready to help you manage AWS S3 buckets.")
    println(result)
}
