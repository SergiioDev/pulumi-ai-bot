package com.example.aws

import com.pulumi.Context
import com.pulumi.aws.s3.Bucket
import com.pulumi.aws.s3.BucketV2
import com.pulumi.aws.s3.BucketV2Args
import com.pulumi.core.Output
import java.util.function.Consumer

interface S3Runtime {
    fun createBucket(name: String): BucketV2Args

    fun defineBucketResource(resourceName: String, bucketDefinition: BucketV2Args): Consumer<Context>

    operator fun invoke() = S3RuntimeImpl()
}

class S3RuntimeImpl : S3Runtime {

    override fun createBucket(name: String): BucketV2Args = BucketV2Args.builder().bucket(name)
        .tags(
            mapOf(
                "Name" to name,
                "ManagedBy" to "pulumi-ai"
            )
        ).build()


    override fun defineBucketResource(resourceName: String, bucketDefinition: BucketV2Args): Consumer<Context>  = Consumer { context ->
        val bucket = BucketV2(resourceName, bucketDefinition)
        context.export("bucketName", bucket.bucket())
    }

}
