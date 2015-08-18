#!/bin/sh

# WARNING: Generate the jar with the right profile (assembly plugin)
#
#      profiles
#      development: pride-proteomes-dev
#      production: pride-proteomes

# Set up the environment

cd /nfs/pride/work/proteomes/data-unifier

# Load environment (and make the bsub command available)
. /etc/profile.d/lsf.sh


QUEUE=production-rh6


# Email recipients
JOB_MAIL="ntoro@ebi.ac.uk"
STD_ERR="output/data-unifier-stderr.txt"
STD_OUT="output/data-unifier-stdout.txt"
LABEL="proteomes-data-unifier"
COMMAND="java -Xms1024m -Xmx4096m -jar ${project.build.finalName}.jar launch-data-unifier-job.xml proteomesDataUnifierJob -next"
CPUS=30

#submit to LSF
bsub -q ${QUEUE} -e ${STD_ERR} -o ${STD_OUT} -M 5000 -R "rusage[mem=5000]" -n ${CPUS} -R "span[hosts=1]" -J ${LABEL} -N -u ${JOB_MAIL} ${COMMAND}
