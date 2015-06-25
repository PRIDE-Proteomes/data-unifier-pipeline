#!/bin/sh

# WARNING: Generate the jar with the right profile (assembly plugin)
#
#      profiles
#      development: db-pride-pridetst, db-pride-proteomes-prideprot-user
#      production: db-pride-pridepro, db-pride-proteomes-prideprot-user

# Set up the environment

cd /nfs/pride/work/proteomes/data-unifier

# Load environment (and make the bsub command available)
. /etc/profile.d/lsf.sh


QUEUE=research-rh6


# Email recipients
JOB_MAIL="ntoro@ebi.ac.uk"
STD_ERR="output/proteomes-data-unifier-stderr.txt"
STD_OUT="output/proteomes-data-unifier-stdout.txt"
LABEL="proteomes-data-unifier"
COMMAND="java -Xms1024m -Xmx20000m -jar ${project.build.finalName}.jar launch-data-unifier-job.xml proteomesDataUnifierJob -next"
CPUS=30

#submit to LSF
bsub -q ${QUEUE} -e ${STD_ERR} -o ${STD_OUT} -M 30000 -R "rusage[mem=30000]" -n ${CPUS} -R "span[hosts=1]" -J ${LABEL} -N -u ${JOB_MAIL} ${COMMAND}
