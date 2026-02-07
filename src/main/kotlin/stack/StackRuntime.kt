package com.example.stack

import com.example.config.ConfigHolder.config
import com.example.config.toPulumiEnvVars
import com.example.tools.PROJECT_NAME
import com.pulumi.Context
import com.pulumi.automation.*
import java.util.function.Consumer

interface StackRuntime {
    fun createOrSelectStack(program: Consumer<Context>, region: String): WorkspaceStack

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

}
