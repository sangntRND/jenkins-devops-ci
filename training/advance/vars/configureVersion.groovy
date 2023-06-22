#!/usr/bin/env groovy
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
void call(Map demoBuild) {
    String buildFlow = demoBuild.build.flow.name
    String gvOut = ""
    String demoVersion = ""
    Map gitVersion = [:]
    // Version Format: MajorMinorShortSha-PreReleaseLabel
    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'github', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD']]) {
        gvOut = sh (script: """docker run --rm --volume "\$(pwd):/repo" docker.io/gittools/gitversion:latest /repo /b ${env.BRANCH_NAME} /output json""", returnStdout: true)
    }
    gitVersion = readJSON text: gvOut

    demoVersion = ("${gitVersion.Major}.${gitVersion.Minor}.${gitVersion.Patch}-${gitVersion.PreReleaseLabel.toLowerCase()}-${BUILD_NUMBER}").trim()
    currentBuild.displayName = demoVersion
    return demoVersion
}
docker pull