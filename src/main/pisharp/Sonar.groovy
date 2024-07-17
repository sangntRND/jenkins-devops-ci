package main.pisharp

def sonarQubeAnalysis(projectKey,sonarHostURL) {
    def SONAR_HOST_URL = sonarHostURL
    def scannerHome = tool 'SonarQubeScanner';
    stage('Analysis Static Code By SonarQube') {
        script {
            withSonarQubeEnv('SonarQube'){
                // Run SonarQube Scanner inside a Docker container
                sh """
                ${scannerHome}/bin/sonar-scanner \
                -Dsonar.projectKey=${projectKey}-${env.BRANCH_NAME} \
                -Dsonar.sources=. \
                -Dsonar.exclusions=**/tests/** \
                -Dsonar.host.url=${env.SONAR_HOST_URL} \
                -Dsonar.login=${env.SONAR_AUTH_TOKEN} \
                -Dsonar.python.coverage.reportPaths=results/coverage.xml
                """         
            }

        }
    }
    stage('Quality Gate Check') {
        timeout(time: 2, unit: 'MINUTES') {
            waitForQualityGate abortPipeline: true
        }
    }
}