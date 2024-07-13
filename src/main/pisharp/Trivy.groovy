package main.pisharp

def trivyScanLocal() {
    stage ("Trivy Scan local") {
        script {
            sh "trivy fs . --format template --template @.ci/html.tpl -o .ci/secretreport.html"
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



