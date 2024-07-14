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

def trivyScanVulnerabilities() {
    stage ("Trivy Scan Vulnerabilities") {
        script {
            sh "trivy fs . --severity HIGH,CRITICAL --scanners vuln --exit-code 0 --format template --template @.ci/html.tpl -o .ci/vulnreport.html"
            publishHTML (target : [allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: '.ci',
                reportFiles: 'vulnreport.html',
                reportName: 'Trivy Vulnerabilities Report',
                reportTitles: 'Trivy Vulnerabilities Report']
            )
        }
    }
}

def trivyScanImages(image){
    stage ("Trivy Scan Docker Images") {
        sh "trivy image --scanners vuln,config --exit-code 0 --severity HIGH,CRITICAL --format template --template @.ci/html.tpl -o .ci/imagesreport.html ${image}"
        publishHTML (target : [allowMissing: true,
            alwaysLinkToLastBuild: true,
            keepAll: true,
            reportDir: '.ci',
            reportFiles: 'imagesreport.html',
            reportName: 'Trivy Vulnerabilities Images Report',
            reportTitles: 'Trivy Vulnerabilities Images Report']
        )
    }
}



