package main.pisharp

def pythonRunInstallDependencies(){
    stage ("Run Install Dependencies ") {
        sh "mkdir -p results"
        sh 'docker run --rm -v $(pwd):/app python:3.9-slim bash -c "pip install poetry && cd /app && poetry config virtualenvs.in-project true && poetry install"'
    }    
}

def runPythonUnitTest() {
    stage ("Run Unit Tests") {
        sh "mkdir -p results"
        sh 'docker run --rm -v $(pwd):/app python:3.9-slim bash -c "pip install poetry && cd /app && poetry run pytest --cov=app --cov-report=xml:results/coverage.xml --junitxml=results/test-results.xml"'
    }
}

def processTestResults(){
    stage ('Process Test Results') {
        xunit thresholds: [
            failed(unstableThreshold: '0'),
            skipped()
        ], tools: [
            JUnit(deleteOutputFiles: true, failIfNotNew: false, pattern: 'results/test-results.xml', skipNoTestFiles: true, stopProcessingIfError: true)
        ]
        cobertura autoUpdateHealth: false, autoUpdateStability: false, coberturaReportFile: "results/coverage.xml", conditionalCoverageTargets: '70, 0, 0', failUnhealthy: true, failUnstable: true, maxNumberOfBuilds: 0, onlyStable: false, sourceEncoding: 'ASCII', zoomCoverageChart: false
    }
}

def buildDockerImages(args){
    def imageRegistry = args.imageRegistry
    def credentialDockerId = args.credentialDockerId
    def namespaceRegistry = args.namespaceRegistry
    def serviceName = args.serviceName
    stage ("Build Docker Images") {
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialId: credentialDockerId, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
            docker.withRegistry("https://${imageRegistry}", credentialId ) {
                docker.build("${imageRegistry}/${namespaceRegistry}/${serviceName}:${BUILD_NUMBER}", "--force-rm --no-cache -f Dockerfile .")
            }
        }
    }
}