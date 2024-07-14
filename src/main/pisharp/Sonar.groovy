package main.pisharp

def sonarQubeAnalysis(projectKey,sonarHostURL) {
    def SONAR_HOST_URL = sonarHostURL
    stage('SonarQube analysis') {
        script {
            withSonarQubeEnv('SonarQube'){
                // Run SonarQube Scanner inside a Docker container
                sh """
                docker run --rm -v ./:/usr/src -w /usr/src \
                -e SONAR_HOST_URL=${env.SONAR_HOST_URL} \
                -e SONAR_LOGIN=${env.SONAR_AUTH_TOKEN} \
                sonarsource/sonar-scanner-cli:latest \
                sonar-scanner \
                -Dsonar.projectKey=${projectKey} \
                -Dsonar.sources=. \
                -Dsonar.exclusions=**/tests/** \
                -Dsonar.host.url=${env.SONAR_HOST_URL} \
                -Dsonar.login=${env.SONAR_AUTH_TOKEN} \
                -Dsonar.python.coverage.reportPaths=results/coverage.xml
                """         
            }

        }
    }
    stage('Quality Gate') {
        timeout(time: 1, unit: 'HOURS') {
            waitForQualityGate abortPipeline: true
        }
    }
}