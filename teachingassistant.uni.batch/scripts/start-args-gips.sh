#!/bin/bash

#
# This script can be used to execute our GIPS-based solution for
# the teaching assistant assignment problem.
#
# Example:
# `./start-args-gips.sh ./input.json ./output.json /tmp/callback.json /tmp/parameter.json`
#
# If you have any questions, feel free to write us an email.
#
# @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
#

set -e

function setup {
    # Make sure that log folders exists
    mkdir -p logs
}

function run {
    # Execute the program itself and save its output to logfile
    java -Xmx240g -jar $JAR $ARGS 2>&1 | tee "./logs/$RUN_NAME.log"
}

# Set env vars
source env.sh

# Config
export JAR="gips-ta.jar"

setup

# Example arguments:
# ./i01.json ./i01_solution.json
# $1         $2
#
# or
#
# ./i01.json ./i01_solution.json ./callback.json ./parameter.json
# $1         #$2                 $4             $5

export inputJson=$1
export outputJson=$2
export callback=$3
export parameter=$4

# Extract needed XMI files
echo "=> Applying GIPS XMI workarounds."

# Extract XMI files
unzip -qq -o $JAR "teachingassistant/uni/batch/hipe/*/hipe-network.xmi"
unzip -qq -o $JAR "teachingassistant/uni/batch/api/*/gips-model.xmi"
unzip -qq -o $JAR "teachingassistant/uni/batch/api/ibex-patterns.xmi"

mkdir -p ../teachingassistant.uni.metamodel/instances

# Actual run
export RUN_NAME=$(date +%Y-%m-%d"_"%H-%M-%S)
if [ ! -z "$parameter" ] && [ ! -z "$callback" ]; then
    export ARGS="-i $inputJson -o $outputJson --verbose --callback $callback --parameter $parameter"
else
    export ARGS="-i $inputJson -o $outputJson --verbose"
fi

echo "#"
echo "# => Using ARGS: $ARGS"
echo "#"
run
# Finished actual run

# Clean up extracted files that are not relevant for the
# produced JSON solution file.
rm -r ./teachingassistant
rm -r ../teachingassistant.uni.metamodel

echo "#"
echo "# => Arg script done."
echo "#"
