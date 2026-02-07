package com.example.tools

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet
import com.example.config.AwsConfig
import com.example.config.toPulumiEnvVars
import com.pulumi.automation.*
import com.pulumi.aws.s3.BucketV2
import com.pulumi.aws.s3.BucketV2Args
import java.util.UUID

const val PROJECT_NAME = "pulumi-ai-bot"

const val STACK_NAME = "dev"

@LLMDescription("""
    Tools for deploying and managing AWS S3 buckets using Pulumi.
""")
class PulumiTools(private val awsConfig: AwsConfig) : ToolSet {

    private var lastPreviewedRegion: String? = null
    private var lastPreviewedBucketName: String? = null

    private fun createStack(region: String, bucketName: String): WorkspaceStack {
        val options = LocalWorkspaceOptions.builder()
            .environmentVariables(awsConfig.toPulumiEnvVars())
            .build()

        val stack = LocalWorkspace.createOrSelectStack(
            PROJECT_NAME, STACK_NAME,
            { ctx: com.pulumi.Context ->

                val bucket = BucketV2(
                    "demo-bucket", BucketV2Args.builder()
                        .bucket(bucketName)
                        .tags(
                            mapOf(
                                "Name" to bucketName,
                                "ManagedBy" to "pulumi-ai"
                            )
                        )
                        .build()
                )
                ctx.export("bucketName", bucket.bucket())
            },
            options
        )
        stack.workspace().installPlugin("aws", "v6.68.0")
        stack.setConfig("aws:region", ConfigValue(region))
        return stack
    }

    @Tool
    @LLMDescription("Preview S3 bucket deployment. Shows what will be created, updated, or deleted without making changes. Returns the generated unique bucket name — remember it for deploy.")
    fun preview(
        @LLMDescription("AWS region, e.g. us-east-1, eu-west-1") region: String,
        @LLMDescription("Name for the s3 bucket (a UUID suffix will be appended automatically)") bucketName: String
    ): String {
        val uniqueName = "$bucketName-${UUID.randomUUID()}"
        lastPreviewedRegion = region
        lastPreviewedBucketName = uniqueName

        return createStack(region, uniqueName).use { stack ->
            val result = stack.preview(
                PreviewOptions.builder()
                    .onStandardOutput(System.out::println)
                    .build()
            )
            buildString {
                appendLine("Preview complete:")
                appendLine("Bucket name: $uniqueName")
                appendLine("Region: $region")
                result.changeSummary().forEach { (op, count) ->
                    appendLine("  $op: $count")
                }
            }
        }
    }

    @Tool
    @LLMDescription("Deploy S3 bucket to AWS. Only call this after the user has confirmed — no parameters needed since the region and bucket name are stored")
    fun deploy(): String {
        val region = lastPreviewedRegion ?: return "Error: run a preview first before deploying."
        val bucketName = lastPreviewedBucketName ?: return "Error: run a preview first before deploying."

        return createStack(region, bucketName).use { stack ->
            val result = stack.up(
                UpOptions.builder()
                    .onStandardOutput(System.out::println)
                    .build()
            )
            buildString {
                appendLine("Deployment complete!")
                result.summary().resourceChanges().forEach { (op, count) ->
                    appendLine("  $op: $count")
                }
                appendLine("Outputs:")
                result.outputs().forEach { (key, output) ->
                    appendLine("  $key: ${output.value()}")
                }
            }
        }
    }

    @Tool
    @LLMDescription("Destroy all S3 infrastructure in the current stack. Only call this if the user explicitly asks to tear down resources.")
    fun destroy(): String {
        val options = LocalWorkspaceOptions.builder()
            .environmentVariables(awsConfig.toPulumiEnvVars())
            .build()

        return LocalWorkspace.createOrSelectStack(
            PROJECT_NAME, STACK_NAME, { _: com.pulumi.Context -> }, options
        ).let { stack ->
            stack.destroy(
                DestroyOptions.builder()
                    .onStandardOutput(System.out::println)
                    .build()
            )
            "Stack destroyed successfully"
        }
    }
}
