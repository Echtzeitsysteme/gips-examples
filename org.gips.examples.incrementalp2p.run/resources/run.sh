#!/bin/bash

set -e

# Parse argument
if [[ -z "$1" ]]; then
	echo "=> No parameter(s) given. Exit."; exit 1 ;
fi

function gips_setup {
    echo "=> Applying GIPS hipe-network.xmi workaround."

    unzip -q -o $JAR "org/gips/examples/incrementalp2p/gips/incrementaldistribution/api/ibex-patterns.xmi"
    unzip -q -o $JAR "org/gips/examples/incrementalp2p/gips/incrementaldistribution/api/gips/gips-model.xmi"
    unzip -q -o $JAR "org/gips/examples/incrementalp2p/gips/incrementaldistribution/hipe/engine/hipe-network.xmi"

    unzip -q -o $JAR "org/gips/examples/incrementalp2p/repository/api/ibex-patterns.xmi"
    unzip -q -o $JAR "org/gips/examples/incrementalp2p/repository/hipe/engine/hipe-network.xmi"

    mkdir -p ../org.gips.examples.incrementalp2p.repository/src-gen/
    mkdir -p ../org.gips.examples.incrementalp2p.gips.incrementaldistribution/src-gen/

    rsync -a ./org ./bin

    rsync -a ./org ../org.gips.examples.incrementalp2p.repository/src-gen/
    rsync -a ./org ../org.gips.examples.incrementalp2p.gips.incrementaldistribution/src-gen/

    # ws-related gips-model file
    mkdir -p /home/mkratz/git/gips-gcm-2023-example/org.gips.examples.incrementalp2p.gips.incrementaldistribution/src-gen/org/gips/examples/incrementalp2p/gips/incrementaldistribution/api/gips/
    cp org/gips/examples/incrementalp2p/gips/incrementaldistribution/api/gips/gips-model.xmi /home/mkratz/git/gips-gcm-2023-example/org.gips.examples.incrementalp2p.gips.incrementaldistribution/src-gen/org/gips/examples/incrementalp2p/gips/incrementaldistribution/api/gips/

    rm -r ./org
}

function run {
    # Execute the program itself and save its output to a logfile
    echo "Running $1 clients"
    java -Xmx4g -jar $JAR 0 $1 2>&1 | tee "./$(date "+%Y%m%d-%H%M%S").log"
}

function clean {
    rm -r ./bin
    rm -r ../org.gips.examples.incrementalp2p.repository
    rm -r ../org.gips.examples.incrementalp2p.gips.incrementaldistribution
    rm -r ./src-sim
    rm -r /home/mkratz/git/gips-gcm-2023-example/org.gips.examples.incrementalp2p.gips.incrementaldistribution/src-gen/org/gips/examples/incrementalp2p/gips/incrementaldistribution/api/gips/
}

# Config
export JAR="gips-*.jar"

gips_setup
run $1
clean

echo " => Done."
