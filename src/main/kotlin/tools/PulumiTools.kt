package com.example.tools

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet
import com.example.aws.S3Runtime
import com.example.stack.StackRuntime
import com.example.stack.destroyAndLog
import com.example.stack.previewAndLog
import com.example.stack.upAndLog
import java.util.*

const val PROJECT_NAME = "pulumi-ai-bot"
@LLMDescription("Tools for deploying and managing AWS S3 buckets using Pulumi")
class PulumiTools(
    private val s3Runtime: S3Runtime,
    private val stackRuntime: StackRuntime
) : ToolSet {
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
        val suffix = UUID.randomUUID().toString().take(8)
        val uniqueName = "$bucketName-${suffix}"

        val bucket = s3Runtime.createBucket(uniqueName)

        val stack = stackRuntime.createOrSelectStack(
            program = s3Runtime.defineBucketResource(uniqueName, bucket),
            region = region
        )

        val previewResult = stack.previewAndLog()

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
    fun deploy(
        @LLMDescription("The exact unique bucket name returned by the preview tool") bucketName: String,
        @LLMDescription("The AWS region used in the preview") region: String
    )
    : String {
        val bucket = s3Runtime.createBucket(bucketName)

        val stack = stackRuntime.createOrSelectStack(
            program = s3Runtime.defineBucketResource(
                bucketName,
                bucket
            ),
            region = region
        )

        val result = stack.upAndLog()

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
    fun destroy(
        @LLMDescription("Bucket name that the user wants to destroy") bucketName: String,
        @LLMDescription("Region name of the bucket, can be the one that the preview returned if not is specified for the user") region: String,
    ): String {
        val bucket = s3Runtime.createBucket(bucketName)

        val stack = stackRuntime.createOrSelectStack(
            program = s3Runtime.defineBucketResource(
                resourceName = bucketName,
                bucketDefinition = bucket
            ),
            region = region
        )


        val result = stack.destroyAndLog()

        stack.close()

        println(result.summary())

        return "Stack destroyed successfully"
    }
}
