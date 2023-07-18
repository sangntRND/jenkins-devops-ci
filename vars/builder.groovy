#!/usr/bin/env groovy
// A switch statement.
void call(String flow) {
    switch (flow) {
            case 'api':
                echo "api"
            break
            case 'frontend':
                echo "frontend"
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
