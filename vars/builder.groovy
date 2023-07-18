#!/usr/bin/env groovy
// A switch statement.
void call(String flow, String projectName) {
    switch (flow) {
            case 'api':
                echo "$flow"
                echo "$projectName"
                dotnet(flow, projectName)
            break
            case 'frontend':
                react()
            break
            default:
                // no case build
            break
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
