#!/bin/sh


##### VARIABLES
# the name to give to the LSF job (to be extended with additional info)
JOB_NAME="proteomes-data-unifier"
# the job parameters that are going to be passed on to the job (build below)
JOB_PARAMETERS="-next"
# memory limit
MEMORY_LIMIT=15000
# LSF email notification
JOB_EMAIL="ntoro@ebi.ac.uk"
# LSF command to run
COMMAND="-jar ${project.build.finalName}.jar launch-data-unifier-job.xml proteomesDataUnifierJob"

INDEX_ENV=""

DB_ENV=""

# OUTPUT
STD_ERR="output/unifier-stderr.txt"
STD_OUT="output/unifier-stdout.txt"

# QUEUE
QUEUE="production-rh6"

#CPUS
CPUS=9


##### FUNCTIONS
printUsage() {
    echo "Description: Stops data unifier pipeline."
    echo ""
    echo "Usage: ./stopDataUnifier.sh [-e|--email] [-j|--job-params]"

    echo "     Example: ./stopDataUnifier.sh -e ntoro@ebi.ac.uk -d test -j 1"
    echo "     (required) job-params: -stop, -abandon or -restart together with the name of the job or the id of a job execution."

    echo "     (optional) email: Email to send LSF notification"

}


##### PARSE the provided parameters
while [ "$1" != "" ]; do
    case $1 in
      "-e" | "--email")
        shift
        JOB_EMAIL=$1
        ;;
      "-j" | "--job-params")
        shift
        JOB_PARAMETERS=$1
        ;;
    esac
    shift
done


##### CHECK the provided arguments
if [ -z ${JOB_PARAMETERS} ]; then
         echo "Need to enter a valid command: -stop, -abandon, -restart and the job name or job id"
         echo ""
         printUsage
         exit 1
fi


##### RUN it on the production LSF cluster #####
bsub  -q ${QUEUE} -e ${STD_ERR} -o ${STD_OUT} -M ${MEMORY_LIMIT} -R rusage[mem=${MEMORY_LIMIT}] -R span[hosts=1] -n ${CPUS} -J ${JOB_NAME} -N -u ${JOB_EMAIL} /nfs/pride/work/java/jdk1.8.0_65/bin/java -Xmx${MEMORY_LIMIT}m ${COMMAND} ${JOB_PARAMETERS}
