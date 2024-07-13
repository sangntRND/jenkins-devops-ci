package main.pisharp

def trivyScanSecrets() {
    stage ("Trivy Scan Secrets") {
        script {
            sh "trivy fs . --scanners secret --format template --template @.ci/html.tpl -o .ci/secretreport.html"
            publishHTML (target : [allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: '.ci',
                reportFiles: 'secretreport.html',
                reportName: 'Trivy Secrets Report',
                reportTitles: 'Trivy Secrets Report']
            )
        }
    }
}



