#!/bin/bash

#
# This script can be used to execute our GIPS-based solution for
# the IHTC 2024 for all instances given.
# It uses different random seeds as parameter.
#
# It assumes that in the current directory there is a folder called
# `ihtc2024_competition_instances` containing all instance files.
#
# Example: `./start-eval-loop-random-seed.sh`
#           (no arguments necessary)
#
# If you have any questions, feel free to write us an email.
#
# @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
#

set -e

echo "#"
echo "# => Eval loop random seed script start."
echo "#"

# iterate over multiple random seeds
for ((randomSeed=1;randomSeed<=5;randomSeed++));
do
    folder=./ihtc2024_competition_instances-random-seed-$randomSeed

    # create a new instances folder for every random seed
    rsync -a ./ihtc2024_competition_instances/* $folder
    for ((i=1;i<=30;i++));
    do
        if [ $i -lt 10 ]; then
            echo "$folder/i0$i.json"
            ./start-args-gips.sh "$folder/i0$i.json" "$folder/i0${i}_solution.json" $randomSeed
        else
            echo "$folder/i$i.json"
            ./start-args-gips.sh "$folder/i$i.json" "$folder/i${i}_solution.json" $randomSeed
        fi
    done

    # move created log directory to include the random seed
    # in its name
    mv ./logs ./logs_random-seed-$randomSeed
done

echo "#"
echo "# => Eval loop random seed script done."
echo "#"

