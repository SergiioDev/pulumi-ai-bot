# Pulumi AI Agent (POC)

> **This is a proof of concept.** Currently scoped to deploying S3 buckets on AWS. The goal is to explore how an AI agent can orchestrate infrastructure deployments through Pulumi.

An AI-powered CLI agent built with [Koog](https://github.com/JetBrains/koog) (JetBrains' Kotlin AI agent framework) that deploys AWS S3 buckets using [Pulumi](https://www.pulumi.com/).

The agent follows a safe deployment workflow: **preview changes → show plan → wait for user confirmation → deploy → show results**.

## Prerequisites

- **JDK 21+** — [Amazon Corretto](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html) or any OpenJDK distribution
- **Pulumi CLI** — [Install guide](https://www.pulumi.com/docs/install/)
- **AWS CLI** — [Install guide](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)
- **Anthropic API key** — [Get one here](https://console.anthropic.com/settings/keys)

## Setup

### 1. Log in to AWS

```bash
aws configure
```

You'll need an AWS account with permissions to create S3 buckets.

### 2. Log in to Pulumi

```bash
pulumi login
```

This requires a [Pulumi account](https://app.pulumi.com/signup). Alternatively, use local state with `pulumi login --local`.

### 3. Initialize the Pulumi stack

```bash
cd infra
pulumi stack init dev
```

### 4. Set the Anthropic API key

```bash
export ANTHROPIC_API_KEY=sk-ant-...
```

### 5. (Optional) Override the Pulumi project directory

By default, the agent uses the `infra/` directory in this repo. To point to a different Pulumi project:

```bash
export PULUMI_PROJECT_DIR=/path/to/your/pulumi/project
```

## Running the agent

```bash
./gradlew run --console=plain
```

The agent will start an interactive session where you can ask it to deploy, preview, or destroy infrastructure.

### Example session

```
Pulumi AI Agent ready. Type your request (or 'exit' to quit):

> Deploy an S3 bucket
```

The agent will:
1. Run `pulumi preview` and show you what will be created
2. Ask for your confirmation before proceeding
3. Run `pulumi up` to deploy
4. Show a summary of the deployment results

## Tech stack

- **Kotlin 2.3** + **JDK 21**
- **Koog 0.6.1** — JetBrains AI agent framework
- **Pulumi (YAML)** — Infrastructure as Code
- **Anthropic Claude** — LLM provider
- **Hoplite** — HOCON configuration
