fun shellCommand(command: String): List<String> = listOf("sh", "-e", "-u", "-o", "pipefail", "-c", command)
