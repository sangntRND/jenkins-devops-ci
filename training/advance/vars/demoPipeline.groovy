#!/usr/bin/env groovy
void call(Map pipelineParams) {
    Map demoBuild = [:]
    String demoVersion

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
                        // equals expected: false, actual: params.SKIP_BUILD
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
                    beforeOptions true
                    beforeInput true
                    beforeAgent true
                }
                steps {
                    script {
                        echo "Testing"
                        demoBuild = configureLoad()
                        demoVersion = configureVersion(demoBuild)
                        demoBuilder(demoBuild, demoVersion)
                    }
                }
            }
        }

        // post {
        //     cleanup {
        //         cleanWs()
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
