set INPUTS=inputs
set VALIDATOR_HOME=.
set VALIDATOR_CONFIG=config
chcp 65001
java -Xms1g -cp target/ocl_validator-3.0-jar-with-all-dependencies.jar ocl.OCLEvaluator %INPUTS%
PAUSE

