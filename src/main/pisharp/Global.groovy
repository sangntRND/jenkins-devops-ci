package main.pisharp

def runPythonUnitTest() {
    stage ("Run Unit Tests") {
        sh "mkdir -p results"
        sh 'docker run --rm -v $(pwd):/app python:3.9-slim bash -c "pip install poetry && cd /app && poetry install && poetry run pytest --cov=app --cov-report=xml:results/coverage.xml --junitxml=results/test-results.xml"'
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