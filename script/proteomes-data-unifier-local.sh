#!/bin/sh
# WARNING: Generate the jar with the right profile.
#
#      profiles
#      development: db-pride-pridetst, db-pride-proteomes-prideprot-user
#      production: db-pride-pridepro, db-pride-proteomes-prideprot-user


java -Xmx6000m -jar ../target/pride-proteomes-data-unifier-0.1-SNAPSHOT.jar launch-data-unifier-job.xml proteomesDataUnifierJob 
#java -Xmx1000m -jar ../target/pride-proteomes-data-unifier-0.1-SNAPSHOT.jar launch-data-unifier-job.xml proteomesDataUnifierJob
