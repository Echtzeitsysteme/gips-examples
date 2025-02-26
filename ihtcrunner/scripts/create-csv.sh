#!/bin/bash

#
# This script can be used to create the requested submission CSV
# file as per description. The file must contain two columns:
# `name`
# `score`
#
# Example: `./create-csv.sh ./ihtc2024_competition_instances`
#
# If you have any questions, feel free to write us an email.
#
# @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
#

set -e

# configure the path of the validator binary to be used
export validator="./IHTP_Validator"

# configure the output CSV file path
export csv="./scores.csv"

# the first input parameter is the folder to search for solutions in
export inputFolder=$1

# remove existing CSV file
rm -f $csv

# create header in CSV file
echo "name,score" > $csv

# find task JSON files and store their paths in an array
taskFiles=()
while IFS=  read -r -d $'\0'; do
    taskFiles+=("$REPLY")
done < <(find $inputFolder -type f -name "i*.json" -print0)

# sort task file array
IFS=$'\n' sortedTaskFiles=($(sort <<<"${taskFiles[*]}"))
unset IFS

# iterate over all found task JSON files
for i in "${sortedTaskFiles[@]}"
do
    # replace `i` with `sol_i` to get the solution file's name
    solutionFile=$inputFolder/$(echo "$i" | sed -e 's/\(.*\)\/i/\/sol_i/g' | sed -e 's/\///g')

    # get individual cost from the official validator
    cost=$($validator $i $solutionFile | grep "Total cost" | grep -Eo '[0-9]{1,7}')

    # remove leading path of the task file to get the instance name
    instanceName=$(echo "$i" | sed -e 's/\(.*\)\/i/\/i/g' | sed -e 's/\///g')

    # write tuple to the CSV file
    echo "$instanceName,$cost" >> $csv
done

echo "=> Finished writing the CSV file."
exit 0
