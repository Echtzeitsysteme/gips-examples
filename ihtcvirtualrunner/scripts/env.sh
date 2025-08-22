#!/bin/bash

#
# This script can be used to set up the necessary environment variables
# for our GIPS-based IHTC 2024 virtual solution.
#
# Please be sure to setup the values according to your installation.
# If you have any questions, feel free to write us an email.
#
# @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
#

export GRB_LICENSE_FILE="/home/mkratz/gurobi.lic"
export GUROBI_HOME="/opt/gurobi1203/linux64/"
export LD_LIBRARY_PATH="/opt/gurobi1203/linux64/lib/"
PATH=$PATH:/opt/gurobi1203/linux64/bin/
