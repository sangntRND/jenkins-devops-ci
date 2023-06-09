#!/bin/bash
while getopts u:p:o:s:a:c: flag
do
    case "${flag}" in
        u) username=${OPTARG};;
        p) password=${OPTARG};;
        o) org=${OPTARG};;
        s) space=${OPTARG};;
        a) appName=${OPTARG};;
        c) apicf=${OPTARG};;
    esac
done
cf login -a ${apicf} -u ${username} -p ${password} -o ${org} -s ${space}
cf target -o ${org} -s ${space}

# Create application autoscaler service
# cf create-service autoscaler standard simplemdg-autoscaler

# Deployment
cf deploy hello/mta_archives/hello_0.1.0.mtar --strategy blue-green
