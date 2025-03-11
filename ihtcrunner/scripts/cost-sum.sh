#!/bin/bash

#
# This script can be used to calculate the total cost of a folder
# containing different solution JSON files. Said folder must
# contain the task instances (e.g., i01.json) as well as the
# solution files with the correct naming scheme (e.g., sol_i01.json).
# Furthermore, the path to the given IHTC validator must be
# configured correctly.
#
# Example: `./cost-sum.sh ./ihtc2024_competition_instances`
#
# If you have any questions, feel free to write us an email.
#
# @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
#

set -e

# configure the path of the validator binary to be used
export validator="./IHTP_Validator"

# the first input parameter is the folder to search for solutions in
export inputFolder=$1

# find solution JSON files and store their paths in an array
solutionFiles=()
while IFS=  read -r -d $'\0'; do
    solutionFiles+=("$REPLY")
done < <(find $inputFolder -type f -name "sol_*.json" -print0)

# iterate over all found solution JSON files
sum=0
for i in "${solutionFiles[@]}"
do
    # replace `sol_` with nothing to get the task file's name
    taskFile=$(echo "$i" | sed -e 's/sol_//g')

    # get individual cost from the official validator
    cost=$($validator $taskFile $i | grep "Total cost" | grep -Eo '[0-9]{1,7}')

    # sum the invidiual costs up
    sum=`expr $sum + $cost`
done

echo "=> Found solutions: ${#solutionFiles[@]}"
echo "=> Total cost: $sum"
echo "=> Average: $(($sum / ${#solutionFiles[@]}))"

exit 0
