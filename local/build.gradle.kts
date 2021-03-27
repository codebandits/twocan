tasks {
    register("startServices") {
        doLast {
            exec {
                workingDir = rootDir
                commandLine = shellCommand(
                        """
                        docker-compose -f local/docker-compose.yml up -d
                        docker exec twocan-postgres timeout 10 sh -c "until pg_isready -U postgres; do sleep 1; done"
                        """.trimIndent()
                )
            }
        }
    }
}
