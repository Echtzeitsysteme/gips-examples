#!/bin/bash

set -e

echo "#"
echo "# => Eval loop script start."
echo "#"

mkdir -p ./metrics

for ((i=1;i<=10;i++));
do
    ./start-args-gips.sh $1 $2 $3 "./metrics/metrics_run_$i.csv"
done

echo "#"
echo "# => Eval loop script done."
echo "#"
