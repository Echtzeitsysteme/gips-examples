#!/bin/bash

#
# This script can be used to execute our GIPS-based solution for
# the IHTC 2024 for all test instances given.
#
# It assumes that in the current directory there is a folder called
# `ihtc2024_test_dataset` containing all instance files.
#
# Example: `./start-test-loop.sh` (no arguments necessary)
#
# If you have any questions, feel free to write us an email.
#
# @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
#

set -e

echo "#"
echo "# => Eval test script start."
echo "#"

for ((i=1;i<=10;i++));
do
    if [ $i -lt 10 ]; then
        echo "./ihtc2024_test_dataset/test0$i.json"
        ./start-args-gips.sh "./ihtc2024_test_dataset/test0$i.json" "./ihtc2024_test_dataset/test0${i}_solution.json"
    else
        echo "./ihtc2024_test_dataset/test$i.json"
        ./start-args-gips.sh "./ihtc2024_test_dataset/test$i.json" "./ihtc2024_test_dataset/test${i}_solution.json"
    fi
done

echo "#"
echo "# => Eval test script done."
echo "#"
