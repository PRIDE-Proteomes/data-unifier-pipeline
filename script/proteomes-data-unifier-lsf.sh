#!/bin/sh

# WARNING: Generate the jar with the right profile (assembly plugin)
#
#      profiles
#      development: db-pride-pridetst, db-pride-proteomes-prideprot-user
#      production: db-pride-pridepro, db-pride-proteomes-prideprot-user


#!/bin/sh

# Set up the environment

cd /nfs/pride/tmp/pride-proteomes

# Load environment (and make the bsub command available)
. /etc/profile.d/lsf.sh


QUEUE=research-rh6


# Email recipients
JOB_MAIL="ntoro@ebi.ac.uk"
STD_ERR="/nfs/pride/tmp/pride-proteomes/output/proteomes-data-unifier-stderr.txt"
STD_OUT="/nfs/pride/tmp/pride-proteomes/output/proteomes-data-unifier-stdout.txt"
LABEL="proteomes-data-unifier"
COMMAND="/homes/protsrv/jre1.6.0_16/bin/java -Xms1024m -Xmx20000m -jar /nfs/pride/tmp/pride-proteomes/pride-proteomes-data-unifier-0.1-SNAPSHOT.jar launch-data-unifier-job.xml proteomesDataUnifierJob -next"
CPUS=30

#submit to LSF
bsub -q ${QUEUE} -e ${STD_ERR} -o ${STD_OUT} -M 30000 -R "rusage[mem=30000]" -n ${CPUS} -R "span[hosts=1]" -J ${LABEL} -N -u ${JOB_MAIL} ${COMMAND}
