#!/usr/bin/env groovy
void call(Map demoBuild, String demoVersion) {
    switch (demoBuild.build.flow.name) {
        case 'dotnet':
            dotnet(demoBuild, demoVersion)
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