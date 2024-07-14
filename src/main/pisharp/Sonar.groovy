package main.pisharp

def sonarQubeAnalysis(projectKey,sonarHostURL) {
    def SONAR_HOST_URL = sonarHostURL
    stage('SonarQube analysis') {
        script {
            withSonarQubeEnv('SonarQube'){
                // Run SonarQube Scanner inside a Docker container
                sh """
                ${env.SONARQUBE_SCANNER_HOME}/bin/sonar-scanner \
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