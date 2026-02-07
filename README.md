# Pulumi AI Agent (POC)

> **This is a proof of concept.** Currently scoped to deploying S3 buckets on AWS. The goal is to explore how an AI agent can orchestrate infrastructure deployments through Pulumi.

An AI-powered CLI agent built with [Koog](https://github.com/JetBrains/koog) (JetBrains' Kotlin AI agent framework) that deploys AWS S3 buckets using [Pulumi](https://www.pulumi.com/).

The agent follows a safe deployment workflow: **preview changes → show plan → wait for user confirmation → deploy → show results**.

## Prerequisites

- **JDK 21+** — [Amazon Corretto](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html) or any OpenJDK distribution
- **Pulumi CLI** — [Install guide](https://www.pulumi.com/docs/install/)
- **AWS account** with an IAM user (see below)
- **Anthropic API key** — [Get one here](https://console.anthropic.com/settings/keys)

## Setup

### 1. Create an IAM user

The agent needs an IAM user with permissions to create and delete S3 buckets.

1. Go to the [IAM Console](https://console.aws.amazon.com/iam/) > **Users** > **Create user**
2. Attach the `AmazonS3FullAccess` policy
3. Go to **Security credentials** > **Create access key** > select **Command Line Interface (CLI)**
4. Copy the **Access key ID** and **Secret access key**

### 2. Log in to Pulumi

```bash
pulumi login
```

This requires a [Pulumi account](https://app.pulumi.com/signup). Alternatively, use local state with `pulumi login --local`.

### 3. Configure the `.env` file

Create a `.env` file in the project root:

```env
ANTHROPIC_KEY=sk-ant-...
AWS_ACCESS_KEY_ID=AKIA...
AWS_SECRET_ACCESS_KEY=wJalr...
```

### 4. Running the agent

```bash
./gradlew run --console=plain
```

The agent will start an interactive session where you can ask it to deploy, preview, or destroy S3 buckets.

## Example session

```
Pulumi AI Agent ready. Type your request (or 'exit' to quit):

> Deploy an S3 bucket in eu-west-1
```

The agent will:
1. Ask for the bucket name if not provided
2. Run a preview and show what will be created
3. Ask for your confirmation before proceeding
4. Deploy the bucket and show the results

## Tech stack

- **Kotlin 2.3** + **JDK 21**
- **Koog 0.6.1** — JetBrains AI agent framework
- **Pulumi Java SDK** — Infrastructure as Code (Automation API)
- **Anthropic Claude** — LLM provider
- **Hoplite** — HOCON configuration
