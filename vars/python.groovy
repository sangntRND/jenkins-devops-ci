import main.pisharp.*

def call() {
    def imageRegistry = "hub.docker.com"
    def credentialDockerId = "dockerhub-demo-token"
    def namespaceRegistry = "sanghvt"
    def serviceName = pwd().tokenize('/').last()
    
    def trivy = new Trivy()
    def global = new Global()
    def sonar = new Sonar()

    stage ('Prepare Package') {
        script {
            sh "mkdir -p .ci"
            writeFile file: '.ci/html.tpl', text: libraryResource('dev/demo/flows/trivy/html.tpl')
        }
    }

    trivy.trivyScanSecrets()
    global.pythonRunInstallDependencies()
    trivy.trivyScanVulnerabilities()
    global.runPythonUnitTest()
    global.processTestResults()
    sonar.sonarQubeAnalysis(serviceName)
    global.buildDockerImages(imageRegistry: imageRegistry, credentialDockerId: credentialDockerId, namespaceRegistry: namespaceRegistry, serviceName: serviceName)

    // stage ("Build Docker Images Run Time") {
    //     withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: acrCredential, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
    //         docker.withRegistry("https://${demoRegistry}", acrCredential ) {
    //             docker.build("${demoRegistry}/${containerName}/${projectName}:${BUILD_NUMBER}", "--force-rm --no-cache -f ./.ci/Dockerfile.Runtime.API \
    //             --build-arg BASEIMG=${containerName}/${projectName}-sdk --build-arg IMG_VERSION=${BUILD_NUMBER} \
    //             --build-arg ENTRYPOINT=${runtime} --build-arg PUBLISH_PROJ=${publishProject} --build-arg RUNIMG=${baseImage} --build-arg RUNVER=${baseTag} .")
    //         }
    //     }
    // }

    // stage ("Trivy Scan Docker Images") {
    //     sh "trivy image --scanners vuln --exit-code 0 --severity HIGH,CRITICAL --format template --template @.ci/html.tpl -o .ci/imagesreport.html ${demoRegistry}/${containerName}/${projectName}:${BUILD_NUMBER}"
    //     publishHTML (target : [allowMissing: true,
    //         alwaysLinkToLastBuild: true,
    //         keepAll: true,
    //         reportDir: '.ci',
    //         reportFiles: 'imagesreport.html',
    //         reportName: 'Trivy Vulnerabilities Images Report',
    //         reportTitles: 'Trivy Vulnerabilities Images Report']
    //     )
    // }

    // stage ("Push Docker Images") {
    //     withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: acrCredential, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
    //         docker.withRegistry("https://${demoRegistry}", acrCredential ) {
    //             sh "docker push ${demoRegistry}/${containerName}/${projectName}:${BUILD_NUMBER}"
    //         }
    //     }
    // }
    // stage ("Deploy To K8S") {
    //     withKubeConfig( caCertificate: '',
    //                     clusterName: "${k8scontextName}",
    //                     contextName: "${k8scontextName}",
    //                     credentialsId: "${k8sCredential}",
    //                     namespace: "${namespace}",
    //                     restrictKubeConfigAccess: false,
    //                     serverUrl: '') {
    //         sh "export acrUrl=${demoRegistry}; export containerName=${containerName}; export projectname=${projectName}; export tag=${BUILD_NUMBER}; \
    //         envsubst < .ci/deployment.yml > deployment.yml; envsubst < .ci/service.yml > service.yml"
    //         sh "kubectl apply -f deployment.yml"
    //         sh "kubectl apply -f service.yml"
    //     }
    // }
}

//========================================================================
// Demo CI
// Version: v1.0
// Updated:
//========================================================================
//========================================================================
// Notes:
//
//
//========================================================================