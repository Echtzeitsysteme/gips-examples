#!/bin/bash

set -e

INPUT_FILE=$1

if [[ -z "$INPUT_FILE" ]]; then
	echo "Given input path was null. Aborting."
	exit 1;
fi

searches=(
	"#VirtualShiftToRoster"
	"#VirtualOpTimeToCapacity"
	"#VirtualWorkloadToOperation"
	"#VirtualShiftToWorkloadInit"
	"#VirtualShiftToWorkloadExtend"
	"#TotalVirtualNodes"
)

# print header
echo -n "instance,"
for key in ${searches[@]}; do
	if [ "$key" = "${searches[-1]}" ]; then
		echo -n "$key"
	else
		echo -n "$key,"
	fi
done
echo ""

# find length
last=($(cat $INPUT_FILE | grep "${searches[-1]}" | sed "s/${searches[-1]}:\ //g"))
length=${#last[@]}

# print data
for ((i=0; i<$length; i++)) do
	echo -n "$((i+1)),"
	for key in ${searches[@]}; do
		# yes, I know, this is a very inefficient way to do this ...
		values=$(cat $INPUT_FILE | grep "$key" | sed "s/${key}:\ //g")
		values_array=($values)
		if [ "$key" = "${searches[-1]}" ]; then
			echo -n "${values_array[$i]}"
		else
			echo -n "${values_array[$i]},"
		fi
	done
	echo ""
done
