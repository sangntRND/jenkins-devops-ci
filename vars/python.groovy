import main.pisharp.*

def call() {
    def imageRegistry = "registry.hub.docker.com"
    def credentialDockerId = "dockerhub-demo-token"
    def namespaceRegistry = "sanghvt"
    def serviceName = pwd().tokenize('/').tokenize('_').first()
    def imageBuildTag = "${imageRegistry}/${namespaceRegistry}/${serviceName}:${BUILD_NUMBER}"
    
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
    trivy.trivyScanDockerImages(imageBuildTag)
    global.pushDockerImages(imageRegistry: imageRegistry, credentialDockerId: credentialDockerId, namespaceRegistry: namespaceRegistry, serviceName: serviceName)

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