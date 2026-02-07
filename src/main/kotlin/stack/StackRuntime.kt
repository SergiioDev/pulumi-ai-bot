@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package com.example.stack

import com.example.config.ConfigHolder.config
import com.example.config.toPulumiEnvVars
import com.example.tools.PROJECT_NAME
import com.pulumi.Context
import com.pulumi.automation.*
import java.util.function.Consumer

interface StackRuntime {
    fun createOrSelectStack(program: Consumer<Context>, region: String): WorkspaceStack

    fun WorkspaceStack.destroy(): UpdateResult

    fun WorkspaceStack.preview(): PreviewResult

    fun WorkspaceStack.up(): UpResult

    operator fun invoke() = StackRuntimeImpl()
}

class StackRuntimeImpl : StackRuntime {
    override fun createOrSelectStack(program: Consumer<Context>, region: String ): WorkspaceStack {
        val options = LocalWorkspaceOptions.builder()
            .environmentVariables(config.aws.toPulumiEnvVars())
            .build()

        val stack = LocalWorkspace.createOrSelectStack(
            PROJECT_NAME,
            config.pulumi.stackName,
            program,
            options
        )

        stack.workspace().installPlugin("aws", "v6.68.0")

        stack.setConfig("aws:region", ConfigValue(region))

        return stack
    }

    override fun WorkspaceStack.preview(): PreviewResult = preview(
        PreviewOptions.builder()
            .onStandardOutput(System.out::println)
            .build()
    )

    override fun WorkspaceStack.up(): UpResult = up(
        UpOptions.builder()
            .onStandardOutput(System.out::println).build()
    )

    override fun WorkspaceStack.destroy(): UpdateResult = destroy(
        DestroyOptions.builder()
            .onStandardOutput(System.out::println)
            .build()
    )

}

