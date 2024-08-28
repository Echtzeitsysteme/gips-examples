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
    java -Xmx120g -jar $JAR $ARGS 2>&1 | tee "./logs/$RUN_NAME.log"
}

# Set env vars
source env.sh

# Config
export JAR="gips-cra.jar"

setup

# Example arguments:
# ./input.xmi ./output.xmi ./pre.xmi
# $1          $2           $3

export inputXmi=$1
export outputXmi=$2
export preXmi=$3

# GIPS workaround for all needed xmi files
echo "=> Applying GIPS XMI workarounds."

# Extract hipe-network.xmi file
unzip -o $JAR "architecture/cra/gipssolution/hipe/*/hipe-network.xmi"
unzip -o $JAR "architecture/cra/gipssolution/api/*/gips-model.xmi"
unzip -o $JAR "architecture/cra/gipssolution/api/ibex-patterns.xmi"

mkdir -p ../architecture.cra.gipssolution/src-gen/architecture/cra
mkdir -p ./bin/architecture/cra

rsync -a ./architecture/cra/gipssolution ./bin/architecture/cra
rsync -a ./architecture/cra/gipssolution ../architecture.cra.gipssolution/src-gen/architecture/cra
# Finished workaround

# Actual run
export RUN_NAME=$(date +%Y-%m-%d"_"%H:%M:%S)
export ARGS="-i $inputXmi -o $outputXmi -q $preXmi -p"
echo "#"
echo "# => Using ARGS: $ARGS"
echo "#"
run
# Finished actual run

rm -r ./architecture/cra/gipssolution ./bin ../architecture.cra.gipssolution

echo "#"
echo "# => Arg script done."
echo "#"
