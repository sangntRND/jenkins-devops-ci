#!/usr/bin/env groovy
void call(Map pipelineParams) {

    String flow = "${env.JOB_NAME}".split('/')[-2].split('%2F')[-1]
    String checkBranches = "$env.BRANCH_NAME"
    // String jobName = "${env.JOB_NAME}".split('/')[-2].split('%2F')[-1].split('_')[1]

//========================================================================

//========================================================================

    pipeline {

        agent any

        options {
            disableConcurrentBuilds()
            disableResume()
            timeout(time: 1, unit: 'HOURS')
        }
        
        stages {
            stage ('Load Pipeline') {
                when {
                    allOf {
                        // Condition Check
                        anyOf{
                            // Branch Event: Nornal Flow
                            anyOf {
                                branch 'main'
                                branch 'jenkins'
                                branch 'PR-*'
                            }
                            // Manual Run: Only if checked.
                            allOf{
                                triggeredBy 'UserIdCause'
                            }
                        }
                    }
                }
                steps {
                    script {
                        echo "$flow"
                        echo "${env.JOB_NAME}"    
                        // dotnet()
                    }
                }
            }
        }

        post {
            cleanup {
                cleanWs()
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
