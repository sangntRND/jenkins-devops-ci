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
    String k8sCredential = 'akstest'
    String namespace = "demo"
    // Load list of container repositories. (DevOps Controlled)

    kubeconfig(credentialsId: 'akstest', serverUrl: '') {
        sh "export registry=${demoRegistry}; export appname=${demoBuild.name}; export tag=${demoVersion}; \
        envsubst < .ci/deployment.yml > deployment.yml; envsubst < .ci/service.yml > service.yml"
        sh "kubectl apply -f deployment.yml -n ${namespace}"
        sh "kubectl apply -f service.yml -n ${namespace}"
    }

    // withKubeConfig([credentialsId: 'k8sCredential']) {
    //     sh "export registry=${demoRegistry}; export appname=${demoBuild.name}; export tag=${demoVersion}; \
    //     envsubst < .ci/deployment.yml > deployment.yml; envsubst < .ci/service.yml > service.yml"
    //     sh "kubectl apply -f deployment.yml -n ${namespace}"
    //     sh "kubectl apply -f service.yml -n ${namespace}"
    // }
}