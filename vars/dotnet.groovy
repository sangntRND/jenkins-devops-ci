#!/usr/bin/env groovy
void call(Map demoBuild, String demoVersion) {
    // Credentials
    String flowName = "${demoBuild.build.flow.name}"
    // String baseImage     = "demotraining.azurecr.io/source/dotnet"
    String baseImage     = "mcr.microsoft.com/dotnet/sdk"
    String baseTag       = "6.0"
    String demoRegistry = "demotraining.azurecr.io"
    // Variables branch
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
            writeFile file: '.ci/docker_entrypoint.sh', text: libraryResource('dev/demo/flows/dotnet/script/docker_entrypoint.sh')
            writeFile file: '.ci/deployment.yml', text: libraryResource('deploy/aks/deployment.yml')
            writeFile file: '.ci/service.yml', text: libraryResource('deploy/aks/service.yml')
        }
    }

    switch (flowName) {
        case 'dotnet':
            stage("Build Solution") {
                docker.build("demo/${demoBuild.name}-sdk:${demoVersion}", "--force-rm --no-cache -f ./.ci/Dockerfile.SDK \
                --build-arg BASEIMG=${baseImage} --build-arg IMG_VERSION=${baseTag} ${WORKSPACE}") 
            }
            stage("Run Unit Integration Tests") {
                testUnitIntegrationTests(demoBuild, demoVersion)
            }
            
            stage("Publish Package") {
                docker.build("${demoRegistry}/demo/${demoBuild.name}:${demoVersion}", "--force-rm --no-cache -f ./.ci/Dockerfile.Runtime.API \
                --build-arg BASEIMG=demo/${demoBuild.name}-sdk --build-arg IMG_VERSION=${demoVersion} \
                --build-arg ENTRYPOINT=${demoBuild.build.runtime.name} --build-arg RUNIMG=${baseImage} --build-arg RUNVER=${baseTag} .")
            }
    }
    // stage ('Publish Images') {
    //     script {
    //         pushImages(demoBuild, demoVersion)
    //     }
    // }
    // stage ('Deploy to K8S') {
    //     script {
    //         deploytok8s(demoBuild, demoVersion)
    //     }
    // }
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