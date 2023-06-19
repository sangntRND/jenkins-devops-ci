#!/usr/bin/env groovy
void call() {
    println 'Loading Configuration...'
    // Validate Build Configuration Exists. If not fail.
    if (fileExists('_demoBuild.yaml')) {
        println 'Found yaml file!'
        demoBuild = readYaml file: '_demoBuild.yaml'
        println "Loaded Configuration: ${demoBuild.name}"
        // loadResourceFiles(demoBuild)
        // println "Loaded Resources: ${demoBuild.name}"
        return demoBuild
    }
}

// void loadResourceFiles (Map build) {
//     buildFlow = build.build.flow.name
//     switch (buildFlow) {
//         case 'simple-backend':
//             // Copy the files required for the build to the workspace.
//             echo "This is $buildFlow"
//             writeFile file: '.ci/Dockerfile.ci', text: libraryResource('dev/demo/flows/backend/docker/Dockerfile.ci')
//             writeFile file: '.ci/Dockerfile.test', text: libraryResource('dev/demo/flows/backend/docker/Dockerfile.test')
//             writeFile file: '.ci/Dockerfile.integrationtest', text: libraryResource('dev/demo/flows/backend/docker/Dockerfile.integrationtest')
//             writeFile file: '.ci/.env.local.json', text: libraryResource('dev/demo/flows/backend/files/.env.local.json')
//             writeFile file: '.ci/default-env.json', text: libraryResource('dev/demo/flows/backend/files/default-env.json')
//             writeFile file: '.ci/default-services.json', text: libraryResource('dev/demo/flows/backend/files/default-services.json')
//             break
//         case 'simple-frontend':
//             // Copy the files required for the build to the workspace.
//             echo "This is $buildFlow"
//             writeFile file: '.ci/Dockerfile.ci', text: libraryResource('dev/demo/flows/frontend/docker/Dockerfile.ci')
//             writeFile file: '.ci/Dockerfile.test', text: libraryResource('dev/demo/flows/frontend/docker/Dockerfile.test')
//             writeFile file: '.ci/Dockerfile.integrationtest', text: libraryResource('dev/demo/flows/frontend/docker/Dockerfile.integrationtest')
//             break
//         default:
//             println 'Defaults Loaded!'
//             break
//     }
// }
//========================================================================
// Demo CI
// Version: v1.0
// Updated: June 13, 2020
//========================================================================
//========================================================================
// Notes:
//
//
//========================================================================
