#!/bin/bash

set -e

function setup {
    # Make sure that folder for hipe-network exists
    mkdir -p bin

    # Extract hipe-network.xmi file
#    unzip -o $JAR "network/model/rules/*/hipe-network.xmi"
#    rsync -a ./network ./bin
#    rm -r ./network

    mkdir -p logs
}

function run {
    # Execute the program itself and save its output to logfile
    java -Xmx240g -jar $JAR $ARGS 2>&1 | tee "./logs/$RUN_NAME.log"
}

# Set env vars
source env.sh

# Config
export JAR="test.jar"

setup

# Example arguments:
# ./input.xmi ./output.xmi "scenario1" "BATCH-A" ./result.csv
# $1          $2           $3          $4		 $5

export inputXmi=$1
export outputXmi=$2
export scenarioID=$3
export runnerType=$4
export outputCsv=$5

# GIPS workaround for all needed xmi files
echo "=> Applying GIPS XMI workarounds."

# Extract hipe-network.xmi file
unzip -o $JAR "PTAConstraintConfig*/hipe/engine/hipe-network.xmi"
unzip -o $JAR "PTAConstraintConfig*/api/gips/gips-model.xmi"
unzip -o $JAR "PTAConstraintConfig*/api/ibex-patterns.xmi"
# TODO: Add models for incremental and incremental2
# Finished workaround

# Actual run
export RUN_NAME="$scenarioID$(date +%Y-%m-%d"_"%H:%M:%S)"
export ARGS="-i $inputXmi -o $outputXmi -id $scenarioID -r $runnerType -c $outputCsv"
echo "#"
echo "# => Using ARGS: $ARGS"
echo "#"
run
# Finished actual run

echo "#"
echo "# => Arg script done."
echo "#"