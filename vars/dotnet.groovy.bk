#!/usr/bin/env groovy
void call(String flow, String projectName) {
    String runtime = "Microsoft.DSX.ProjectTemplate.API"
    String publishProject = "src/BookStore.API/BookStore.API.csproj"
    String baseImage     = "nttraining.azurecr.io/source/dotnet/sdk"
    String baseTag       = "6.0.411-jammy"
    String baseSonarTag  = "6.0.411-sonarqube"
    String demoRegistry = "nttraining.azurecr.io"
    String sonarToken = "sonar-token"
    String acrCredential = 'acr-demo-token'
    String k8sCredential = 'akstest'
    String namespace = "demo"
    String rununitTest = "dotnet test --no-build -l:trx --collect:'XPlat Code Coverage' --results-directory ./results"

//========================================================================
//========================================================================

//========================================================================
//========================================================================

    stage ('Prepare Package') {
        script {
            writeFile file: '.ci/Dockerfile.SDK', text: libraryResource('dev/demo/flows/dotnet/docker/Dockerfile.SDK')
            writeFile file: '.ci/Dockerfile.Runtime.API', text: libraryResource('dev/demo/flows/dotnet/docker/Dockerfile.Runtime.API')
            writeFile file: '.ci/Dockerfile.SonarBuild', text: libraryResource('dev/demo/flows/dotnet/docker/Dockerfile.SonarBuild')
            writeFile file: '.ci/docker_entrypoint.sh', text: libraryResource('dev/demo/flows/dotnet/script/docker_entrypoint.sh')
            writeFile file: '.ci/deployment.yml', text: libraryResource('deploy/be/deployment.yml')
            writeFile file: '.ci/service.yml', text: libraryResource('deploy/be/service.yml')
        }
    }

    stage ("Build Solution") {
        docker.build("demo/${projectName}-sdk:${BUILD_NUMBER}", "--force-rm --no-cache -f ./.ci/Dockerfile.SDK \
        --build-arg BASEIMG=${baseImage} --build-arg IMG_VERSION=${baseTag} ${WORKSPACE}") 
    }

    stage ('Run Unit Tests') {
        sh "mkdir -p results"
        sh "docker run -i --rm --volume './results:/src/results' demo/${projectName}-sdk:${BUILD_NUMBER} $rununitTest"
    }

    stage ('Run Integration Tests') {
        echo "Run Integration Tests"
    }

    stage ('Process Test Results') {
        docker.image("demo/${projectName}-sdk:${BUILD_NUMBER}").inside() {
            xunit(
                testTimeMargin: '600000',
                thresholdMode: 1,
                thresholds: [failed(), skipped()],
                tools: [MSTest(deleteOutputFiles: true, failIfNotNew: true, pattern: "results/*.trx", skipNoTestFiles: false, stopProcessingIfError: true)]
            )
        }

        cobertura coberturaReportFile: "results/*/*.xml"
    }

    stage('SonarQube analysis') {
        script {
            withSonarQubeEnv(credentialsId: sonarToken) {
                withCredentials([string(credentialsId: sonarToken, variable: 'SONAR_TOKEN')]) {
                    docker.build("demo/${projectName}-sonar:${BUILD_NUMBER}", "--force-rm --no-cache -f ./.ci/Dockerfile.SonarBuild \
                    --build-arg BASEIMG=${baseImage} --build-arg IMG_VERSION=${baseSonarTag} --build-arg SONAR_PROJECT=${projectName} --build-arg SONAR_TOKEN=${SONAR_TOKEN} ${WORKSPACE}") 
                }
            }
        }
    }

    stage ("Publish Package") {
        docker.build("${demoRegistry}/demo/${projectName}:${BUILD_NUMBER}", "--force-rm --no-cache -f ./.ci/Dockerfile.Runtime.API \
        --build-arg BASEIMG=demo/${projectName}-sdk --build-arg IMG_VERSION=${BUILD_NUMBER} \
        --build-arg ENTRYPOINT=${runtime} --build-arg PUBLISH_PROJ=${publishProject} --build-arg RUNIMG=${baseImage} --build-arg RUNVER=${baseTag} .")
    }

    stage ("Push Docker Images") {
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: acrCredential, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
            docker.withRegistry("https://${demoRegistry}", acrCredential ) {
                sh "docker login ${demoRegistry} -u ${USERNAME} -p ${PASSWORD}"
                sh "docker push ${demoRegistry}/demo/${projectName}:${BUILD_NUMBER}"
            }
        }
    }
    stage ("Deploy To K8S") {
        kubeconfig(credentialsId: 'akstest', serverUrl: '') {
            sh "export registry=${demoRegistry}; export appname=${projectName}; export tag=${BUILD_NUMBER}; \
            envsubst < .ci/deployment.yml > deployment.yml; envsubst < .ci/service.yml > service.yml"
            sh "kubectl apply -f deployment.yml -n ${namespace}"
            sh "kubectl apply -f service.yml -n ${namespace}"
        }
    }
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