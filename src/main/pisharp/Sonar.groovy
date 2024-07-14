package main.pisharp

def sonarQubeAnalysis() {
    stage('SonarQube analysis') {
        script {
            // Run SonarQube Scanner inside a Docker container
            sh "docker run --rm -v $(pwd):/usr/src -w /usr/src -e SONAR_HOST_URL=${SONAR_HOST_URL} -e SONAR_LOGIN=${SONAR_AUTH_TOKEN} sonarsource/sonar-scanner-cli:latest sonar-scanner -Dsonar.projectKey=my-flask-app -Dsonar.sources=. -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.login=${SONAR_AUTH_TOKEN} -Dsonar.python.coverage.reportPaths=results/sonar-coverage.xml"
        }
    }
}