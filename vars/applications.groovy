#!/usr/bin/env groovy
void call(Map demoBuild, String demoVersion) {
    // Credentials
    String orgRegistry = 'demo'
    String dockerHubCredential = 'dockerhub'
    String buildRegistry = 'gitlab.simplemdg.com:5050'
    String gitlabCredential = 'gitlab_simplemdg_com'
    String gitlabTokenCredential = 'gitlab_self_host_token'
    String baseImage     = "${orgRegistry}/simplemdg_package"
    String baseTag       = "16-full"
    // Variables common
    String apiCF = 'https://api.cf.us10-001.hana.ondemand.com'
    String flowName = "${demoBuild.build.flow.name}"
    String artifact = "hello/mta_archives/hello_0.1.0.mtar"
    String space = 'app'
    String org = 'database'
    // Variables branch
    String checkBranches = "$env.BRANCH_NAME"
    String[] deployBranches = ['main', 'develop']

//========================================================================
//========================================================================

//========================================================================
//========================================================================
    if (deployBranches.contains(env.BRANCH_NAME)) {
        //====================================================================
        stage ('Prepare Package') {
            script {
                writeFile file: 'deployment.sh', text: libraryResource('dev/demo/flows/simplemdg/script/deployment.sh')
            }
        }
        //====================================================================
        //====================================================================
        // ====================================================================
        stage('Build') {
            sh 'mbt build -p cf -s hello'
        }
        //====================================================================
        //====================================================================
        // ====================================================================
        //====================================================================
        stage('Deploy') {
            withCredentials([[$class        : 'UsernamePasswordMultiBinding',
                credentialsId   : 'luketa',
                usernameVariable: 'USERNAME',
                passwordVariable: 'PASSWORD']]) {
            //==================================================================== 
                    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: dockerHubCredential, usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD']]) {
                        sh "chmod +x ./deployment.sh"
                        command = "./deployment.sh -u ${USERNAME} -p ${PASSWORD} -o ${org} -s ${space} -c ${apiCF}"
                        sh "docker run -v -i $baseImage:$baseTag $command"
                    }
                }
            }
        }
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