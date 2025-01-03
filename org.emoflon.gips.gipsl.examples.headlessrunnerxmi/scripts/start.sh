#!/bin/bash

set -e

function gips_setup {
    echo "=> Extract needed XMIs from the JAR file"

    unzip -o $JAR "org/emoflon/gips/gipsl/examples/headlessrunnerxmi/hipe/engine/hipe-network.xmi"
    unzip -o $JAR "org/emoflon/gips/gipsl/examples/headlessrunnerxmi/api/gips/gips-model.xmi"
    unzip -o $JAR "org/emoflon/gips/gipsl/examples/headlessrunnerxmi/api/ibex-patterns.xmi"

    rsync -a ./org/emoflon/gips/gipsl/examples/headlessrunnerxmi/hipe/engine/hipe-network.xmi .
    rsync -a ./org/emoflon/gips/gipsl/examples/headlessrunnerxmi/api/gips/gips-model.xmi .
    rsync -a ./org/emoflon/gips/gipsl/examples/headlessrunnerxmi/api/ibex-patterns.xmi .
    rm -r ./org
}

function run {
    # Execute the program itself and save its output to logfile
    java -Xmx4g -jar $JAR $1 2>&1 | tee "./$(date +%Y-%m-%d"_"%H-%M-%S).log"
}

# Set env vars
source env.sh

# Config
export JAR="gips-headless.jar"

gips_setup
run "../../org.emoflon.gips.gipsl.examples.headlessrunnermodel/instances/example-model.xmi"

echo " => Done."
