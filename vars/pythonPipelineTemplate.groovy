import main.pisharp.*

def call(serviceName) {
    def imageRegistry = "registry.hub.docker.com"
    def credentialDockerId = "dockerhub-demo-token"
    def namespaceRegistry = "sanghvt"
    def gitopsRepo = 'https://github.com/sangntRND/pisharped-gitops.git'
    def gitCredential = 'github'
    def serviceName = serviceName
    def imageBuildTag = "${imageRegistry}/${namespaceRegistry}/${serviceName}:${BRANCH_NAME}-${BUILD_NUMBER}"
    def sonarHostURL = 'http://13.213.249.3:9000/'
    def trivy = new Trivy()
    def global = new Global()
    def sonar = new Sonar()

    stage ('Prepare Package') {
        script {
            sh "mkdir -p .ci"
            writeFile file: '.ci/html.tpl', text: libraryResource('trivy/html.tpl')
        }
    }

    trivy.trivyScanSecrets()
    global.pythonRunInstallDependencies()
    trivy.trivyScanVulnerabilities()
    global.runPythonUnitTest()
    global.processTestResults()
    sonar.sonarQubeAnalysis(serviceName, sonarHostURL)
    global.buildDockerImages(imageRegistry: imageRegistry, credentialDockerId: credentialDockerId, namespaceRegistry: namespaceRegistry, serviceName: serviceName)
    trivy.trivyScanDockerImages(imageBuildTag)
    global.pushDockerImages(imageRegistry: imageRegistry, credentialDockerId: credentialDockerId, namespaceRegistry: namespaceRegistry, serviceName: serviceName)
    global.deployToK8S(gitopsRepo: gitopsRepo, gitCredential: gitCredential, serviceName: serviceName)
}