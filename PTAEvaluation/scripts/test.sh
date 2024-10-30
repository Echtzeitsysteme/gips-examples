#!/bin/bash
set -e
echo "#"
echo "# => Test start."
echo "#"
mkdir -p ./out
./start-args-gips.sh "./RndEval1.xmi" "./out/out.xmi" "test" "BATCH-A" "./out/out.csv"
echo "#"
echo "# => Test done."
echo "#"