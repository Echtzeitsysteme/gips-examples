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
export JAR="gips-cra.jar"

setup

# Example arguments:
# ./input.xmi ./output.xmi
# $1          $2

export inputXmi=$1
export outputXmi=$2

# GIPS workaround for all needed xmi files
echo "=> Applying GIPS XMI workarounds."

# Extract hipe-network.xmi file
unzip -o $JAR "classdiagramtgg3/hipe/*/hipe-network.xmi"
unzip -o $JAR "classdiagramtgg3/api/*/gips-model.xmi"
unzip -o $JAR "classdiagramtgg3/api/ibex-patterns.xmi"

mkdir -p ../classdiagramtgg3/src-gen/
mkdir -p C%3A/Users/mkratz/git/gips-examples/classdiagramtgg3/src-gen

rsync -a ./classdiagramtgg3 ./bin
rsync -a ./classdiagramtgg3 ../classdiagramtgg3/src-gen
rsync -a ./classdiagramtgg3 ./C%3A/Users/mkratz/git/gips-examples/classdiagramtgg3/src-gen
rm -r ./classdiagramtgg3
# Finished workaround

# Actual run
export RUN_NAME=$(date +%Y-%m-%d"_"%H-%M-%S)
export ARGS="-i $inputXmi -o $outputXmi -p"
echo "#"
echo "# => Using ARGS: $ARGS"
echo "#"
run
# Finished actual run

echo "#"
echo "# => Arg script done."
echo "#"
