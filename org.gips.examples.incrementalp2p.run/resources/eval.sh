#!/bin/bash

set -e

NUM_OF_REP=10
FROM=10
TO=100
STEP_SIZE=5

for (( i=$FROM; i<=$TO; i+=$STEP_SIZE )); do
    for (( rep=1; rep<=$NUM_OF_REP; rep+=1 )); do
        echo "=> Starting repetition $rep with $i clients."
        ./run.sh $i
    done
done

echo "=> Eval script finished.";
