package main.pisharp

def trivyScanSecrets() {
    stage ("Scan Secrets By Trivy ") {
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
    stage ("Scan Vulnerabilities By Trivy ") {
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

def trivyScanDockerImages(image){
    stage ("Scan Docker Images By Trivy") {
        sh "trivy image --scanners vuln --exit-code 0 --severity HIGH,CRITICAL --format template --template @.ci/html.tpl -o .ci/imagesreport.html ${image}"
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


