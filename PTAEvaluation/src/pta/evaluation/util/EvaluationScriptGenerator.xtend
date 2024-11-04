package pta.evaluation.util

import java.util.Collection

class EvaluationScriptGenerator {
	static def String genEnvScript(String lic, String home, String lib, String bin) {
		return '''
		export GRB_LICENSE_FILE="«lic»"
		export GUROBI_HOME="«home»"
		export LD_LIBRARY_PATH="«lib»"
		PATH=$PATH:~«bin»'''
	}
	static def String genStartScript(String jar, String env) {
		return'''#!/bin/bash
		
		set -e
		
		function setup {
		    # Make sure that folder for hipe-network exists
		    mkdir -p bin
		    mkdir -p logs
		}
		
		function run {
		    # Execute the program itself and save its output to logfile
		    java -Xmx240g -jar $JAR $ARGS 2>&1 | tee "./logs/$RUN_NAME.log"
		}
		
		# Set env vars
		source «env»
		
		# Config
		export JAR="«jar»"
		
		setup
		
		# Example arguments:
		# ./input.xmi ./output.xmi "scenario1" "BATCH-A" ./result.csv
		# $1          $2           $3          $4		 $5
		
		export inputXmi=$1
		export outputXmi=$2
		export scenarioID=$3
		export runnerType=$4
		export outputCsv=$5
		
		# Actual run
		export RUN_NAME="$scenarioID$(date +%Y-%m-%d"_"%H:%M:%S)"
		export ARGS="-i $inputXmi -o $outputXmi -id $scenarioID -r $runnerType -c $outputCsv"
		echo "#"
		echo "# => Using ARGS: $ARGS"
		echo "#"
		run
		# Finished actual run
		
		echo "#"
		echo "# => Arg script done."
		echo "#"'''
	}
	
	static def String genExecutionScript(String runScript, String id, String type, String src, String trg) {
		return '''#!/bin/bash
		set -e
		export csv=$1
		echo "#"
		echo "# => Dispatching evaluation job<«id»>"
		echo "#"
		«runScript» "«src»" «trg»" "«id»" "«type»" $csv
		echo "#"
		echo "# => Job complete<«id»>"
		echo "#"'''
	}
	
	static def String genDispatchScript(String csvPrefix, Collection<String> execScripts) {
		return '''#!/bin/bash
		set -e
		export csvFile="«csvPrefix»$(date +%Y-%m-%d"_"%H:%M:%S)"
		
		# GIPS workaround for all needed xmi files
		echo "=> Applying GIPS XMI workarounds."
				
		# Extract hipe-network.xmi file
		unzip -o $JAR "PTAConstraintConfig*/hipe/engine/hipe-network.xmi"
		unzip -o $JAR "PTAConstraintConfig*/api/gips/gips-model.xmi"
		unzip -o $JAR "PTAConstraintConfig*/api/ibex-patterns.xmi"
		# Finished workaround
		
		echo "#"
		echo "# => Adding jobs to spooler."
		echo "#"
		«FOR script : execScripts»
		tsp «script» csvFile
		«ENDFOR»
		echo "#"
		echo "# => Finished."
		echo "#"'''
	}
}