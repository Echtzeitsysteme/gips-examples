#!/bin/bash

#
# This script can be used to execute our GIPS-based solution for
# the IHTC 2024 with only one argument, i.e., the input model to
# load.
#
# Example:
# `./start-args-gips.sh ./i01.json 0 /tmp/callback.json /tmp/parameter.json`
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
export JAR="gips-ihtc.jar"

setup

# Example arguments:
# ./i01.json
# $1

export inputXmi=$1
export randomSeed=$2
export callback=$3
export parameter=$4

# Extract needed XMI files
echo "=> Applying GIPS XMI workarounds."

# Extract XMI files
unzip -qq -o $JAR "ihtcvirtualgipssolution/hipe/*/hipe-network.xmi"
unzip -qq -o $JAR "ihtcvirtualgipssolution/api/*/gips-model.xmi"
unzip -qq -o $JAR "ihtcvirtualgipssolution/api/ibex-patterns.xmi"

# Actual run
export RUN_NAME=$(date +%Y-%m-%d"_"%H-%M-%S)
if [ ! -z "$randomSeed" ]; then
    if [ ! -z "$parameter" ]; then
        export ARGS="-i $inputXmi --verbose --randomseed $randomSeed --callback $callback --parameter $parameter"
    else
        export ARGS="-i $inputXmi --verbose --randomseed $randomSeed"
    fi
else
    export ARGS="-i $inputXmi --verbose"
fi

echo "#"
echo "# => Using ARGS: $ARGS"
echo "#"
run
# Finished actual run

# Clean up extracted files that are not relevant for the
# produced JSON solution file.
rm -r ./ihtcvirtualgipssolution

echo "#"
echo "# => Arg script done."
echo "#"
