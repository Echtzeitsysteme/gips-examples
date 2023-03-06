#!/bin/bash

set -e

function gips_setup {
    echo "=> Applying GIPS hipe-network.xmi workaround."

    unzip -o $JAR "org/emoflon/gips/gipsl/examples/headlessrunner/hipe/engine/hipe-network.xmi"
    unzip -o $JAR "org/emoflon/gips/gipsl/examples/headlessrunner/api/gips/gips-model.xmi"
    unzip -o $JAR "org/emoflon/gips/gipsl/examples/headlessrunner/api/ibex-patterns.xmi"

    mkdir -p ../org.emoflon.gips.gipsl.examples.headlessrunner/src-gen/
    rsync -a ./org ./bin
    rsync -a ./org ../org.emoflon.gips.gipsl.examples.headlessrunner/src-gen/
    rm -r ./org
}

function run {
    # Execute the program itself and save its output to logfile
    java -Xmx1g -jar $JAR $1 2>&1 | tee "./$(date "+%Y%m%d-%H%M%S").log"
}

# Set env vars
source env.sh

# Config
export JAR="gips-headless.jar"

gips_setup
run "../../org.emoflon.gips.gipsl.examples.headlessrunnermodel/instances/example-model.xmi"

echo " => Done."

