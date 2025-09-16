#!/bin/bash

#
# This script can be used to execute our GIPS-based solution for
# the IHTC 2024 for all instances given.
#
# It assumes that in the current directory there is a folder called
# `ihtc2024_competition_instances` containing all instance files.
#
# Example: `./start-eval-loop.sh /tmp/callback.json /tmp/parameter.json`
#
# If you have any questions, feel free to write us an email.
#
# @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
#

set -e

export callback=$1
export parameter=$2

echo "#"
echo "# => Eval loop script start."
echo "# Arguments:"
echo "#     callback: $callback"
echo "#     parameter: $parameter"
echo "#"

for ((i=1;i<=30;i++));
do
    if [ $i -lt 10 ]; then
        echo "./ihtc2024_competition_instances/i0$i.json"
        ./start-args-gips.sh "./ihtc2024_competition_instances/i0$i.json" "./ihtc2024_competition_instances/i0${i}_solution.json" 0 $callback $parameter
    else
        echo "./ihtc2024_competition_instances/i$i.json"
        ./start-args-gips.sh "./ihtc2024_competition_instances/i$i.json" "./ihtc2024_competition_instances/i${i}_solution.json" 0 $callback $parameter
    fi
done

echo "#"
echo "# => Eval loop script done."
echo "#"
