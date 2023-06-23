#!/usr/bin/env groovy
void call() {
    String name = "bookstore"
    String runtime = "BookStore.API.dll"
    String publishProject = "src/BookStore.API/BookStore.API.csproj"
    String baseImage     = "mcr.microsoft.com/dotnet/sdk"
    String baseTag       = "6.0"
    String demoRegistry = "demotraining.azurecr.io"
    String sonarToken = "sonar-token"
    String acrCredential = 'acr-demo-token'
    String rununitTest = "dotnet test --no-build -l:trx -c Release -p:DOTNET_RUNTIME_IDENTIFIER=linux-x64 --collect:'XPlat Code Coverage' --verbosity minimal --results-directory ./results"

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
        }
    }

    stage ("Build Solution") {
        docker.build("demo/${name}-sdk:${BUILD_NUMBER}", "--force-rm --no-cache -f ./.ci/Dockerfile.SDK \
        --build-arg BASEIMG=${baseImage} --build-arg IMG_VERSION=${baseTag} ${WORKSPACE}") 
    }

    stage ('Run Unit Tests') {
        sh "mkdir -p results; docker run -i --rm --volume './results:/src/results' demo/${name}-sdk:${BUILD_NUMBER} $rununitTest"
    }

    stage ('Run Integration Tests') {
        echo "Run Integration Tests"
    }

    stage ('Process Test Results') {
        docker.image("demo/${name}-sdk:${BUILD_NUMBER}").inside() {
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
                    docker.build("demo/${name}-sonar:${BUILD_NUMBER}", "--force-rm --no-cache -f ./.ci/Dockerfile.SonarBuild \
                    --build-arg BASEIMG=${baseImage} --build-arg IMG_VERSION=${baseTag} --build-arg SONAR_PROJECT=${name} --build-arg SONAR_TOKEN=${SONAR_TOKEN} ${WORKSPACE}") 
                }
            }
        }
    }

    stage ("Publish Package") {
        docker.build("${demoRegistry}/demo/${name}:${BUILD_NUMBER}", "--force-rm --no-cache -f ./.ci/Dockerfile.Runtime.API \
        --build-arg BASEIMG=demo/${name}-sdk --build-arg IMG_VERSION=${BUILD_NUMBER} \
        --build-arg ENTRYPOINT=${runtime} --build-arg PUBLISH_PROJ=${publishProject} --build-arg RUNIMG=${baseImage} --build-arg RUNVER=${baseTag} .")
    }

    stage ("Push Docker Images") {
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: acrCredential, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
            docker.withRegistry("https://${demoRegistry}", acrCredential ) {
                sh "docker login ${demoRegistry} -u ${USERNAME} -p ${PASSWORD}"
                sh "docker push ${demoRegistry}/demo/${name}:${BUILD_NUMBER}"
            }
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