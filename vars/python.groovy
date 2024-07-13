import main.pisharp.*

def call() {
    // String projectName = "projecttemplate-api"
    // String runtime = "Microsoft.DSX.ProjectTemplate.API"
    // String publishProject = "ProjectTemplate/Microsoft.DSX.ProjectTemplate.API/Microsoft.DSX.ProjectTemplate.API.csproj"
    // String baseImage     = "pisharpeddemo.azurecr.io/baseimages/ubuntu"
    // String baseTag       = "jammy-dotnet-sdk-6.0.414"
    // String baseSonarTag  = "sonar"
    // String demoRegistry = "pisharpeddemo.azurecr.io"
    // String sonarToken = "sonar-token"
    // String sonarHost = "http://20.42.95.177:9000"
    // String acrCredential = 'acr-demo-token'
    // String k8sCredential = 'aksdemo'
    // String k8scontextName = "demo"
    // String namespace = "demo"
    // String containerName = "jenkins"
    // String rununitTest = "dotnet test --no-build --no-restore -l:trx --collect:'XPlat Code Coverage' --results-directory ./results"
    def trivy = new Trivy()
//========================================================================
//========================================================================

//========================================================================
//========================================================================

    stage ('Prepare Package') {
        script {
            sh "mkdir -p .ci"
            writeFile file: '.ci/deployment.yml', text: libraryResource('deploy/be/deployment.yml')
            writeFile file: '.ci/service.yml', text: libraryResource('deploy/be/service.yml')
            writeFile file: '.ci/html.tpl', text: libraryResource('dev/demo/flows/trivy/html.tpl')
        }
    }

    trivy.trivyScanSecret()

    // stage ("Build Solution") {
    //     withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: acrCredential, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
    //         docker.withRegistry("https://${demoRegistry}", acrCredential ) {
    //             docker.build("${containerName}/${projectName}-sdk:${BUILD_NUMBER}", "--force-rm --no-cache -f ./.ci/Dockerfile.SDK \
    //             --build-arg BASEIMG=${baseImage} --build-arg IMG_VERSION=${baseTag} ${WORKSPACE}")
    //         }
    //     }
    // }

    // stage ('Run Unit Tests') {
    //     sh "mkdir -p results"
    //     sh "docker run -i --rm --volume './results:/src/results' ${containerName}/${projectName}-sdk:${BUILD_NUMBER} $rununitTest"
    // }

    // stage ('Run Integration Tests') {
    //     echo "Run Integration Tests"
    //     sh "ls -la ./results"
    // }

    // stage ('Process Test Results') {
    //     docker.image("${containerName}/${projectName}-sdk:${BUILD_NUMBER}").inside() {
    //         xunit(
    //             testTimeMargin: '600000',
    //             thresholdMode: 1,
    //             thresholds: [failed(), skipped()],
    //             tools: [MSTest(deleteOutputFiles: true, failIfNotNew: true, pattern: "results/*.trx", skipNoTestFiles: false, stopProcessingIfError: true)]
    //         )
    //     }

    //     cobertura coberturaReportFile: "results/*/*.xml"
    // }

    // stage('SonarQube analysis') {
    //     script {
    //         withCredentials([string(credentialsId: sonarToken, variable: 'SONAR_TOKEN')]) {
    //             docker.build("${containerName}/${projectName}-sonar:${BUILD_NUMBER}", "--force-rm --no-cache -f ./.ci/Dockerfile.SonarBuild \
    //             --build-arg BASEIMG=${baseImage} --build-arg IMG_VERSION=${baseSonarTag} --build-arg SONAR_HOST=${sonarHost} --build-arg SONAR_PROJECT=${projectName} --build-arg SONAR_TOKEN=${SONAR_TOKEN} ${WORKSPACE}") 
    //         }
    //     }
    // }

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