#!/bin/bash

set -e

# Parse arguments
if [[ -z "$1" ]]; then
	echo "=> No parameter(s) given. Exit."; exit 1 ;
fi
while [[ "$#" -gt 0 ]]; do
	case $1 in
		-r|--read) IN_PATH="$2"; shift ;;
		-w|--write) OUT_PATH="$2"; shift ;;
		*) echo "=> Unknown parameter passed: $1"; exit 1 ;;
	esac
	shift
done

function gips_setup {
    echo "=> Applying GIPS hipe-network.xmi workaround."

    unzip -q -o $JAR "org/emoflon/gips/gipsl/examples/sdr/extended/hipe/engine/hipe-network.xmi"
    unzip -q -o $JAR "org/emoflon/gips/gipsl/examples/sdr/extended/api/gips/gips-model.xmi"
    unzip -q -o $JAR "org/emoflon/gips/gipsl/examples/sdr/extended/api/ibex-patterns.xmi"
}

function run {
    # Execute the program itself and save its output to a logfile
    java -Xmx4g -jar $JAR -i $1 -o $2 2>&1 | tee "./$(date "+%Y%m%d-%H%M%S").log"
}

function clean {
    rm -r ./instances
    rm json-model.xmi
    rm json-model-result.xmi
    rm Gurobi_ILP.log
    rm -r ./org
}

# Set env vars
source env.sh

# Config
export JAR="gips-*.jar"

gips_setup
run $IN_PATH $OUT_PATH
clean

echo " => Done."
