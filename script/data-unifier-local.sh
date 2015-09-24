#!/bin/sh
# WARNING: Generate the jar with the right profile.



#java -Xmx6000m -jar ../target/${project.build.finalName}.jar launch-data-unifier-job.xml proteomesDataUnifierJob -next
java -Xmx6000m -jar ../target/pride-proteomes-data-unifier-1.0.1-SNAPSHOT.jar launch-data-unifier-job.xml proteomesDataUnifierJob -next
