package com.example.tools

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet
import com.example.aws.S3Runtime
import com.example.config.AwsConfig
import com.example.stack.StackRuntime
import com.pulumi.automation.*
import java.util.*

const val PROJECT_NAME = "pulumi-ai-bot"
@LLMDescription("Tools for deploying and managing AWS S3 buckets using Pulumi")
class PulumiTools(
    private val s3Runtime: S3Runtime,
    private val stackRuntime: StackRuntime
) : ToolSet {
    private lateinit var lastPreviewedRegion: String
    private lateinit var lastPreviewedBucketName: String

    @Tool
    @LLMDescription(
        """
            Preview S3 bucket deployment. Shows what will be created, updated, or deleted without making changes.
            Returns the generated unique bucket name — remember it for deploy
            """
    )
    fun preview(
        @LLMDescription("AWS region, e.g. us-east-1, eu-west-1") region: String,
        @LLMDescription("Name for the s3 bucket (a UUID suffix will be appended automatically)") bucketName: String
    ): String {
        val uniqueName = "$bucketName-${UUID.randomUUID()}"
        lastPreviewedRegion = region
        lastPreviewedBucketName = uniqueName

        val bucket = s3Runtime.createBucket(uniqueName)

        val stack = stackRuntime.createOrSelectStack(
            program = s3Runtime.defineBucketResource(uniqueName, bucket),
            region = region
        )

        val previewResult = stack.preview()

        stack.close()

        return buildString {
            appendLine("Preview complete:")
            appendLine("Bucket name: $uniqueName")
            appendLine("Region: $region")
            previewResult.changeSummary().forEach { (op, count) ->
                appendLine("  $op: $count")
            }
        }

    }

    @Tool
    @LLMDescription("Deploy S3 bucket to AWS. Only call this after the user has confirmed — no parameters needed since the region and bucket name are stored")
    fun deploy(): String {
        val bucket = s3Runtime.createBucket(lastPreviewedBucketName)

        val stack = stackRuntime.createOrSelectStack(
            program = s3Runtime.defineBucketResource(
                lastPreviewedBucketName,
                bucket
            ),
            region = lastPreviewedRegion
        )

        val result = stack.up()

        stack.close()

        return buildString {
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

    @Tool
    @LLMDescription("Destroy all S3 infrastructure in the current stack. Only call this if the user explicitly asks to tear down resources.")
    fun destroy(): String {
        val bucket = s3Runtime.createBucket(lastPreviewedBucketName)

        val stack = stackRuntime.createOrSelectStack(
            program = s3Runtime.defineBucketResource(
                lastPreviewedBucketName,
                bucket
            ),
            region = lastPreviewedRegion
        )

        val result = stack.destroy(
            DestroyOptions.builder()
                .onStandardOutput(System.out::println)
                .build()
        )

        stack.close()

        println(result.summary())

        return "Stack destroyed successfully"
    }
}
