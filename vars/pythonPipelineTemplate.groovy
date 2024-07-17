import main.pisharp.*

def call(serviceName) {
    def imageRegistry = "registry.hub.docker.com"
    def credentialDockerId = "dockerhub-demo-token"
    def namespaceRegistry = "sanghvt"
    def gitopsRepo = 'https://github.com/sangntRND/pisharped-gitops.git'
    def gitopsBranch = 'main'
    def gitCredential = 'github'
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
    // Step 1: Scan all the application to check if we can put any sensitive informations in the source code or not
    trivy.trivyScanSecrets()

    // Step 2: Run the unit test to check function code and show the test result
    global.runPythonUnitTest()
    global.processTestResults()

    // Step 3: Scan the vulnerabilities of each python dependencies
    trivy.trivyScanVulnerabilities()

    // Step 4: Scan static code to check the Code smell, Bug, Vulnerability
    sonar.sonarQubeAnalysis(serviceName, sonarHostURL)

    // Step 5: Install python dependencies
    global.pythonRunInstallDependencies()

    // Step 6: Build docker images with the new tag
    global.buildDockerImages(imageRegistry: imageRegistry, credentialDockerId: credentialDockerId, namespaceRegistry: namespaceRegistry, serviceName: serviceName)
    
    // Step 7: Scan the vulnerabilities of the new image
    trivy.trivyScanDockerImages(imageBuildTag)
    
    // Step 8: Push image to image registry and update the new image tag in the gitops repository
    // and then Argocd can sync the new deployment
    global.pushDockerImages(imageRegistry: imageRegistry, credentialDockerId: credentialDockerId, namespaceRegistry: namespaceRegistry, serviceName: serviceName)
    global.deployToK8S(gitopsRepo: gitopsRepo, gitopsBranch: gitopsBranch, gitCredential: gitCredential, serviceName: serviceName)
}