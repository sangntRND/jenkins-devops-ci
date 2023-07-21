#!/usr/bin/env groovy
void call() {
    String projectName = "projecttemplate-frontend"
    String baseImage     = "nttraining.azurecr.io/source/node/focal"
    String baseTag       = "node16-nginx"
    String baseSonarTag  = "6.0.411-sonarqube"
    String demoRegistry = "nttraining.azurecr.io"
    String sonarToken = "sonar-token"
    String acrCredential = 'acr-demo-token'
    String k8sCredential = 'akstest'
    String namespace = "demo"

//========================================================================
//========================================================================

//========================================================================
//========================================================================

    stage ('Prepare Package') {
        script {
            writeFile file: '.ci/Dockerfile.ci', text: libraryResource('dev/demo/flows/react/docker/Dockerfile.ci')
            writeFile file: 'react_init.sh', text: libraryResource('dev/demo/flows/react/script/react_init.sh')
            writeFile file: '.ci/deployment.yml', text: libraryResource('deploy/fe/deployment.yml')
            writeFile file: '.ci/service.yml', text: libraryResource('deploy/fe/service.yml')
        }
    }

    stage ("Run Install") {
        script {
            sh "npm ci"
        }
    }

    stage ('Run Build') {
        script {
            sh "npm run build"
        }
    }

    stage ('Run Unit Tests') {
        script {
            sh "npm run test"
        }
    }

    stage ('Run Integration Tests') {
        echo "Run Integration Tests"
    }

    stage ("Build Docker Images") {
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: acrCredential, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
            docker.withRegistry("https://${demoRegistry}", acrCredential ) {
                sh "docker login ${demoRegistry} -u ${USERNAME} -p ${PASSWORD}"
                docker.build("${demoRegistry}/jenkins/${projectName}:${BUILD_NUMBER}", "--force-rm --no-cache -f ./.ci/Dockerfile.ci \
                --build-arg BASEIMG=${baseImage} --build-arg IMG_VERSION=${baseTag} .")
            }
        }
    }

    stage ("Push Docker Images") {
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: acrCredential, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
            docker.withRegistry("https://${demoRegistry}", acrCredential ) {
                sh "docker push ${demoRegistry}/jenkins/${projectName}:${BUILD_NUMBER}"
            }
        }
    }
    // stage ("Deploy To K8S") {
    //     kubeconfig(credentialsId: 'akstest', serverUrl: '') {
    //         sh "export registry=${demoRegistry}; export appname=${projectName}; export tag=${BUILD_NUMBER}; \
    //         envsubst < .ci/deployment.yml > deployment.yml; envsubst < .ci/service.yml > service.yml"
    //         sh "kubectl apply -f deployment.yml -n ${namespace}"
    //         sh "kubectl apply -f service.yml -n ${namespace}"
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