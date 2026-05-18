#!/bin/bash

# This script can be used to execute all nine tiny analysis examples.
#
# Example:
# `./run.sh`
#
# If you have any questions, feel free to write us an email.
#
# @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
#

set -e

function run {
    echo "=> RUNNING $f"
    mkdir -p ${PWD}/output/"${f##*/}"
    ./start-args-gips.sh $f ${PWD}/output/"${f##*/}"/"${f##*/}"_solution.json ${PWD}/callback.json ${PWD}/parameter.json | tee ${PWD}/output/"${f##*/}"/run.log
    mv ${PWD}/problem.lp ${PWD}/output/"${f##*/}"/problem.lp
    mv ${PWD}/exports.txt ${PWD}/output/"${f##*/}"/exports.txt
    mv ${PWD}/Gurobi_MILP.log ${PWD}/output/"${f##*/}"/Gurobi_MILP.log
    echo "=> FINISHED $f"
}

find ./resources/paper2_targeted -type f -name "*.json" | while read f; do run "$f"; done
