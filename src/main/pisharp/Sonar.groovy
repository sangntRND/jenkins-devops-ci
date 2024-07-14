package main.pisharp

def sonarQubeAnalysis() {
        script {
            // Run SonarQube Scanner inside a Docker container
            sh """
            docker run --rm -v $(pwd):/usr/src -w /usr/src \
            -e SONAR_HOST_URL=${SONAR_HOST_URL} \
            -e SONAR_LOGIN=${SONAR_AUTH_TOKEN} \
            sonarsource/sonar-scanner-cli:latest \
            sonar-scanner \
            -Dsonar.projectKey=projecttemplate-python1-api \
            -Dsonar.sources=. \
            -Dsonar.host.url=${SONAR_HOST_URL} \
            -Dsonar.login=${SONAR_AUTH_TOKEN} \
            -Dsonar.python.coverage.reportPaths=results/coverage.xml
            """
        }
}