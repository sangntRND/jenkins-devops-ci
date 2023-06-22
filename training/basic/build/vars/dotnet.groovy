#!/usr/bin/env groovy
void call() {
    String name = "bookstore"
    String runtime = "BookStore.API.dll"
    String baseImage     = "mcr.microsoft.com/dotnet/sdk"
    String baseTag       = "6.0"
    String demoRegistry = "demotraining.azurecr.io"
    String checkBranches = "$env.BRANCH_NAME"
    String[] deployBranches = ['main', 'jenkins']

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
            writeFile file: '.ci/deployment.yml', text: libraryResource('deploy/aks/deployment.yml')
            writeFile file: '.ci/service.yml', text: libraryResource('deploy/aks/service.yml')
        }
    }

    stage ("Build Solution") {
        docker.build("demo/${name}-sdk:${BUILD_NUMBER}", "--force-rm --no-cache -f ./.ci/Dockerfile.SDK \
        --build-arg BASEIMG=${baseImage} --build-arg IMG_VERSION=${baseTag} ${WORKSPACE}") 
    }
    
    stage ("Publish Package") {
        docker.build("${demoRegistry}/demo/${name}:${BUILD_NUMBER}", "--force-rm --no-cache -f ./.ci/Dockerfile.Runtime.API \
        --build-arg BASEIMG=demo/${name}-sdk --build-arg IMG_VERSION=${BUILD_NUMBER} \
        --build-arg ENTRYPOINT=${runtime} --build-arg RUNIMG=${baseImage} --build-arg RUNVER=${baseTag} .")
    }
}
// }
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