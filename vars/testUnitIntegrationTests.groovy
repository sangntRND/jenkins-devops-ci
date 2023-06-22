#!/usr/bin/env groovy
//========================================================================
// Demo CI
// Version: v1.0
// Updated:
//========================================================================
//========================================================================
// Notes: docker.inside user must be root.
//
//
//========================================================================
void call(Map demoBuild, String demoVersion) {
    String[] intTest = [ "jenkins" ]
    String[] unitTest = ["main", "jenkins"]
    String prTests = "PR"
    String flowName = "${demoBuild.build.flow.name}"
    Boolean runUnit = false
    Boolean runIntegration = false
    String rununitTest = "dotnet test --no-build -l:trx -c Release -p:DOTNET_RUNTIME_IDENTIFIER=linux-x64 --collect:'XPlat Code Coverage' --verbosity minimal --results-directory ./results"
    if (demoBuild.build.testing.enabled){
        if ( unitTest.contains(env.BRANCH_NAME) ) {
            runUnit = true
        }
        if ( intTest.contains(env.BRANCH_NAME) ) {
            runIntegration = true
        }
        if ( env.BRANCH_NAME.contains(prTests) ) {
            runUnit = true
        }
        switch (flowName) {
            case 'dotnet':
                if (runUnit){
                    stage('Run Unit Tests'){
                        sh "mkdir -p results"
                        sh "docker run -i --rm --volume './results:/results' demo/${demoBuild.name}-sdk:${demoVersion} $rununitTest"
                    }
                }
                if (runIntegration){
                    stage('Run Integration Tests'){
                            echo "Run Integration Tests"
                    }
                } 

                if ( runIntegration || runUnit ){
                    stage('Process Test Results'){
                        docker.image("demo/${demoBuild.name}-sdk:${demoVersion}").inside() {
                            xunit(
                                testTimeMargin: '600000',
                                thresholdMode: 1,
                                thresholds: [failed(), skipped()],
                                tools: [MSTest(deleteOutputFiles: true, failIfNotNew: true, pattern: "results/*.trx", skipNoTestFiles: false, stopProcessingIfError: true)]
                            )
                        }

                        cobertura coberturaReportFile: "results/*/*.xml"
                    }
                }
                break
            default:
                echo "Testing not supported for flow: ${flowName}"
                break
        }
    }
}
