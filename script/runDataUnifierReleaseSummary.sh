#!/bin/sh


##### VARIABLES
# the name to give to the LSF job (to be extended with additional info)
JOB_NAME="proteomes-data-unifier-release-summary"
# the job parameters that are going to be passed on to the job (build below)
JOB_PARAMETERS="-next"
# memory limit
MEMORY_LIMIT=1000
# LSF email notification
JOB_EMAIL="ntoro@ebi.ac.uk"
# LSF command to run
COMMAND="-jar ${project.build.finalName}.jar launch-data-unifier-release-summary-job.xml proteomesDataUnifierReleaseSummaryJob"

INDEX_ENV=""

DB_ENV=""

# OUTPUT
STD_ERR="output/release-summary-stderr.txt"
STD_OUT="output/release-summary-stdout.txt"

# QUEUE
QUEUE="production-rh6"

#CPUS
CPUS=9


##### FUNCTIONS
printUsage() {
    echo "Description: Data provider pipeline extracts all the peptiforms from the PRIDE Cluster resource and write them in the PRIDE Proteomes pipeline after and enrichment phase."
    echo ""
    echo "Usage: ./runDataUnifierReleaseSummary.sh -d|--database [-e|--email] [-j|--job-params]"

    echo "     Example: ./runDataUnifierReleaseSummary.sh -e ntoro@ebi.ac.uk -d test"
    echo "     (required) database: Proteomes database environment -> prod | dev | test"
    echo "     (optional) email: Email to send LSF notification"
    echo "     (optional) job-params: Allows to add the run.id to relaunch a previous unfinished job (e.g. run.id(long)=4 )"

}


##### PARSE the provided parameters
while [ "$1" != "" ]; do
    case $1 in
      "-e" | "--email")
        shift
        JOB_EMAIL=$1
        ;;
      "-d" | "--database")
        shift
        DB_ENV=$1
        ;;
      "-j" | "--job-params")
        shift
        JOB_PARAMETERS=$1
        ;;
    esac
    shift
done


##### CHECK the provided arguments
if [ -z ${DB_ENV} ]; then
         echo "Need to enter a valid proteomes environment"
         echo ""
         printUsage
         exit 1
else
    case ${DB_ENV} in
    "prod")
        echo "Proteomes db environment selected: prod"
        ;;
    "dev")
        echo "Proteomes db environment selected: dev"
        ;;
    "test")
        echo "Proteomes db environment selected: test"
        ;;
    * )
        echo "Need to enter a valid proteomes db environment"
        printUsage
        exit 1
    esac
fi

##### RUN it on the production LSF cluster #####
bsub  -q ${QUEUE} -e ${STD_ERR} -o ${STD_OUT} -M ${MEMORY_LIMIT} -R rusage[mem=${MEMORY_LIMIT}] -R span[hosts=1] -n ${CPUS} -J ${JOB_NAME} -N -u ${JOB_EMAIL} /nfs/pride/work/java/jdk1.8.0_65/bin/java -Xmx${MEMORY_LIMIT}m -DDB_ENVIRONMENT=${DB_ENV} ${COMMAND} ${JOB_PARAMETERS}
