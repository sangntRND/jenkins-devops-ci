#!/usr/bin/env groovy
void call() {
    String projectName = "projecttemplate-frontend"
    String baseImage     = "pisharpeddemo.azurecr.io/baseimages/ubuntu"
    String baseTag       = "focal-node16-nginx"
    // String baseSonarTag  = "6.0.411-sonarqube"
    String demoRegistry = "pisharpeddemo.azurecr.io"
    // String sonarToken = "sonar-token"
    String acrCredential = "acr-demo-token"
    // String k8sCredential = "akstest"
    // String k8scontextName = "nttraining"
    // String namespace = "demo"
    String containerName = "jenkins"
    String lbbe = "http://20.6.161.46:80"
    String k8sCredential = "akstest"
    String k8scontextName = "k8scontextName"
    String namespace = "demo"
    String checkBranches = "$env.BRANCH_NAME"
    String[] deployBranches = ['main', 'stage', 'develop', 'jenkins', 'pisharped']
//========================================================================
//========================================================================

//========================================================================
//========================================================================

    // switch (checkBranches){
    //     case 'main':
    //         String k8sCredential = "akstest"
    //         String k8scontextName = "k8scontextName"
    //         String namespace = "demo"
    //     break
    //     case 'stage':
    //         String k8sCredential = "aksstage"
    //         String k8scontextName = "nttrainingstage"
    //         String namespace = "demo"
    //     break
    //     case 'develop':
    //         String k8sCredential = "akstest"
    //         String k8scontextName = "nttraining"
    //         String namespace = "demo"
    //     break
    // }

    stage ('Prepare Package') {
        script {
            writeFile file: '.ci/Dockerfile.ci', text: libraryResource('dev/demo/flows/react/docker/Dockerfile.ci')
            writeFile file: '.ci/react_init.sh', text: libraryResource('dev/demo/flows/react/script/react_init.sh')
            writeFile file: '.ci/nginx.conf', text: libraryResource('dev/demo/flows/react/script/nginx.conf')
            writeFile file: '.ci/deployment.yml', text: libraryResource('deploy/fe/deployment.yml')
            writeFile file: '.ci/service.yml', text: libraryResource('deploy/fe/service.yml')
            writeFile file: '.ci/html.tpl', text: libraryResource('dev/demo/flows/trivy/html.tpl')
            // sh "export REACT_APP_API_BASE=${lbbe}; envsubst < .env.jenkins > .env; cat .env"
        }
    }

    stage ("Trivy Scan Secret") {
        script {
            sh "trivy fs . --scanners secret --format template --template @.ci/html.tpl -o .ci/secretreport.html"
            publishHTML (target : [allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: '.ci',
                reportFiles: 'secretreport.html',
                reportName: 'Trivy Secrets Report',
                reportTitles: 'Trivy Secrets Report']
            )
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

    stage ("Trivy Scan Vulnerabilities") {
        script {
            sh "trivy fs . --severity HIGH,CRITICAL --scanners vuln --format template --template @.ci/html.tpl -o .ci/vulnreport.html"
            publishHTML (target : [allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: '.ci',
                reportFiles: 'vulnreport.html',
                reportName: 'Trivy Vulnerabilities Report',
                reportTitles: 'Trivy Vulnerabilities Report']
            )
        }
    }

    stage ('Run Unit Tests') {
        script {
            sh "npm run test"
        }
    }

    if (deployBranches.contains(env.BRANCH_NAME)) {
        stage ('Run Integration Tests') {
            echo "Run Integration Tests"
        }

        // stage ("Trivy Scan Base Images") {
        //     withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: acrCredential, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
        //         docker.withRegistry("https://${demoRegistry}", acrCredential ) {
        //             sh "trivy image --exit-code 1 --severity CRITICAL ${baseImage}:${baseTag}"
        //         }
        //     }
        // }

        stage ("Build Docker Images") {
            withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: acrCredential, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
                docker.withRegistry("https://${demoRegistry}", acrCredential ) {
                    docker.build("${demoRegistry}/jenkins/${projectName}:${BUILD_NUMBER}", "--force-rm --no-cache -f ./.ci/Dockerfile.ci \
                    --build-arg BASEIMG=${baseImage} --build-arg IMG_VERSION=${baseTag} .")
                }
            }
        }

        stage ("Trivy Scan Docker Images") {
            sh "trivy image --scanners vuln,config --exit-code 1 --severity HIGH,CRITICAL --format template --template @.ci/html.tpl -o .ci/imagesreport.html ${demoRegistry}/jenkins/${projectName}:${BUILD_NUMBER}"
            publishHTML (target : [allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: '.ci',
                reportFiles: 'imagesreport.html',
                reportName: 'Trivy Vulnerabilities Images Report',
                reportTitles: 'Trivy Vulnerabilities Images Report']
            )
        }

        stage ("Push Docker Images") {
            withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: acrCredential, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
                docker.withRegistry("https://${demoRegistry}", acrCredential ) {
                    sh "docker push ${demoRegistry}/jenkins/${projectName}:${BUILD_NUMBER}"
                }
            }
        }

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