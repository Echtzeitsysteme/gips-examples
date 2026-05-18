#!/bin/bash

set -e

# LP_FILE=problem.lp
# LOG_FILE=run.log
LOG_FILE=$1

# original rows
rows_orig=$(cat $LOG_FILE | grep -e "Optimize a model with" | grep -o -P '(?<=with\ ).*(?=\ rows)')

# original cols
columns_orig=$(cat $LOG_FILE | grep -e "Optimize a model with" | grep -o -P '(?<=rows,\ ).*(?=\ columns)')

# original nonzeros
nonzeros_orig=$(cat $LOG_FILE | grep -e "Optimize a model with" | grep -o -P '(?<=columns\ and\ ).*(?=\ nonzeros)')

# presolved rows
rows_presolved=$(cat $LOG_FILE | grep -e "Presolve removed" | grep -o -P '(?<=removed\ ).*(?=\ rows)')

# presolved columns
columns_presolved=$(cat $LOG_FILE | grep -e "Presolve removed" | grep -o -P '(?<=rows\ and\ ).*(?=\ columns)')

# presolved nonzeros
nonzeros_presolved="na"

# objective
objective=$(cat $LOG_FILE | grep -e "Overall objective value: " | grep -o -P '(?<=Overall\ objective\ value:\ ).*(?=)')

# gap
gap=$(cat $LOG_FILE | grep -e "Best objective " | grep -o -P '(?<=,\ gap\ ).*(?=%)')

# runtime pre (s)
pre_gips_init=$(cat $LOG_FILE | grep -e "Runtime GIPS init: " | grep -o -P '(?<=Runtime\ GIPS\ init:\ ).*(?=s.)')
pre_gips_update=$(cat $LOG_FILE | grep -e "Runtime GIPS update: " | grep -o -P '(?<=Runtime\ GIPS\ update:\ ).*(?=s.)')

build_total=$(cat $LOG_FILE | grep -e "BUILD_TOTAL: " | grep -o -P '(?<=BUILD_TOTAL:\ ).*(?=s.)')

runtime_pre_all=$(echo $pre_gips_init + $pre_gips_update + $build_total | bc)

# runtime solve (s)
runtime_solve=$(cat $LOG_FILE | grep -e "SOLVE_MILP: " | grep -o -P '(?<=SOLVE_MILP:\ ).*(?=s.)')

# runtime total (s)
runtime_total=$(cat $LOG_FILE | grep -e "Total runtime: " | grep -o -P '(?<=Total\ runtime:\ ).*(?=s.)')

#
# Print line
#

echo $rows_orig, $columns_orig, $nonzeros_orig, $rows_presolved, $columns_presolved, $nonzeros_presolved, $objective, $gap, $runtime_pre_all, $runtime_solve, $runtime_total
