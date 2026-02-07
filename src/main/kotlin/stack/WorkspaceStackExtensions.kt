@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package com.example.stack

import com.pulumi.automation.DestroyOptions
import com.pulumi.automation.PreviewOptions
import com.pulumi.automation.PreviewResult
import com.pulumi.automation.UpOptions
import com.pulumi.automation.UpResult
import com.pulumi.automation.UpdateResult
import com.pulumi.automation.WorkspaceStack

fun WorkspaceStack.previewAndLog(): PreviewResult = preview(
    PreviewOptions.builder()
        .onStandardOutput(System.out::println)
        .build()
)

fun WorkspaceStack.upAndLog(): UpResult = up(
    UpOptions.builder()
        .onStandardOutput(System.out::println).build()
)

fun WorkspaceStack.destroyAndLog(): UpdateResult = destroy(
    DestroyOptions.builder()
        .onStandardOutput(System.out::println)
        .build()
)