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
void call(Map demoBuild, String demoVersion) {
    String demoRegistry = "demotraining.azurecr.io"
    String acrCredential = 'acr-demo-token'
    // Load list of container repositories. (DevOps Controlled)

    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: acrCredential, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
        docker.withRegistry("https://${demoRegistry}", acrCredential ) {
            sh "docker login ${demoRegistry} -u ${USERNAME} -p ${PASSWORD}"
            sh "docker push ${demoRegistry}/demo/${demoBuild.name}:${demoVersion}"
        }
    }
}