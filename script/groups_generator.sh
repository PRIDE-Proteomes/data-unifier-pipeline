#!/bin/sh

echo -e "\nCreating directories for the files\n"
mkdir groups
mkdir proteins
mkdir output

echo -e "\nCleaning previously generated files\n"

rm groups/*.txt
rm proteins/uniprot-taxonomy-*-complete.fasta

echo -e "\nDownloading uniprot current release proteomes proteins\n"

# New ftp for proteomes in Uniprot
curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000006548_3702.fasta.gz"
curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000005640_9606.fasta.gz"
curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000000589_10090.fasta.gz"
curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000002494_10116.fasta.gz"
curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000000437_7955.fasta.gz"
curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000002311_559292.fasta.gz"
curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000002485_284812.fasta.gz"
curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000000803_7227.fasta.gz"
curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Bacteria/UP000000625_83333.fasta.gz"

curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000006548_3702_additional.fasta.gz"
curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000005640_9606_additional.fasta.gz"
curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000000589_10090_additional.fasta.gz"
curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000002494_10116_additional.fasta.gz"
curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000000437_7955_additional.fasta.gz"
curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000002311_559292_additional.fasta.gz"
curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000002485_284812_additional.fasta.gz"
curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000000803_7227_additional.fasta.gz"
curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Bacteria/UP000000625_83333_additional.fasta.gz"

echo -e "\nDownloading uniprot current release id mapping file\n"
# New ftp for proteomes in Uniprot

# We can not use the mapping file for the uniprot proteomes ftp becasue it doesn't keep the relationship with the entry that allow us
# the mapping to the isoforms. For the moment we will use the mapping file for the whole species.

#curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000006548_3702.idmapping.gz"
#curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000005640_9606.idmapping.gz"
#curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000000589_10090.idmapping.gz"
#curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000002494_10116.idmapping.gz"

curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/idmapping/by_organism/ARATH_3702_idmapping.dat.gz"
curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/idmapping/by_organism/HUMAN_9606_idmapping.dat.gz"
curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/idmapping/by_organism/MOUSE_10090_idmapping.dat.gz"
curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/idmapping/by_organism/RAT_10116_idmapping.dat.gz"
curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/idmapping/by_organism/DANRE_7955_idmapping.dat.gz"
curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/idmapping/by_organism/YEAST_559292_idmapping.dat.gz"
curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/idmapping/by_organism/SCHPO_284812_idmapping.dat.gz"
curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/idmapping/by_organism/DROME_7227_idmapping.dat.gz"
curl -O "ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/idmapping/by_organism/ECOLI_83333_idmapping.dat.gz"


echo -e "\nUncompressing gzip files\n"

gzip -d *.gz

echo -e "\nRenaming fasta files to the convention pattern\n"

mv UP000006548_3702.fasta uniprot-taxonomy-3702-complete.fasta
mv UP000005640_9606.fasta uniprot-taxonomy-9606-complete.fasta
mv UP000000589_10090.fasta uniprot-taxonomy-10090-complete.fasta
mv UP000002494_10116.fasta uniprot-taxonomy-10116-complete.fasta
mv UP000000437_7955.fasta uniprot-taxonomy-7955-complete.fasta
mv UP000002311_559292.fasta uniprot-taxonomy-559292-complete.fasta
mv UP000002485_284812.fasta uniprot-taxonomy-284812-complete.fasta
mv UP000000803_7227.fasta uniprot-taxonomy-7227-complete.fasta
mv UP000000625_83333.fasta uniprot-taxonomy-83333-complete.fasta

cat UP000006548_3702_additional.fasta >> uniprot-taxonomy-3702-complete.fasta
cat UP000005640_9606_additional.fasta >> uniprot-taxonomy-9606-complete.fasta
cat UP000000589_10090_additional.fasta >> uniprot-taxonomy-10090-complete.fasta
cat UP000002494_10116_additional.fasta >> uniprot-taxonomy-10116-complete.fasta
cat UP000000437_7955_additional.fasta >> uniprot-taxonomy-7955-complete.fasta
cat UP000002311_559292_additional.fasta >> uniprot-taxonomy-559292-complete.fasta
cat UP000002485_284812_additional.fasta >> uniprot-taxonomy-284812-complete.fasta
cat UP000000803_7227_additional.fasta >> uniprot-taxonomy-7227-complete.fasta
cat UP000000625_83333_additional.fasta >> uniprot-taxonomy-83333-complete.fasta

echo -e  "\nGenerating Ensembl genes mapping files\n"

grep "Ensembl" ARATH_3702_idmapping.dat > gene-map-3702.txt
grep "Ensembl" HUMAN_9606_idmapping.dat > gene-map-9606.txt
grep "Ensembl" MOUSE_10090_idmapping.dat > gene-map-10090.txt
grep "Ensembl" RAT_10116_idmapping.dat > gene-map-10116.txt
grep "Ensembl" DANRE_7955_idmapping.dat > gene-map-7995.txt
grep "Ensembl" YEAST_559292_idmapping.dat > gene-map-559292.txt
grep "Ensembl" SCHPO_284812_idmapping.dat > gene-map-284812.txt
grep "Ensembl" DROME_7227_idmapping.dat > gene-map-7227.txt
grep "Ensembl" ECOLI_83333_idmapping.dat > gene-map-83333.txt

echo -e "\nGenerating genes groups\n"

python utilities/mapper.py gene-map-3702.txt EnsemblGenome | sort -k1 > gene-groups-3702-sorted.txt
python utilities/mapper.py gene-map-9606.txt Ensembl | sort -k1 > gene-groups-9606-sorted.txt
python utilities/mapper.py gene-map-10090.txt Ensembl | sort -k1 > gene-groups-10090-sorted.txt
python utilities/mapper.py gene-map-10116.txt Ensembl | sort -k1 > gene-groups-10116-sorted.txt
python utilities/mapper.py gene-map-7995.txt Ensembl | sort -k1 > gene-groups-7995-sorted.txt
python utilities/mapper.py gene-map-559292.txt EnsemblGenome | sort -k1 > gene-groups-559292-sorted.txt
python utilities/mapper.py gene-map-284812.txt EnsemblGenome | sort -k1 > gene-groups-284812-sorted.txt
python utilities/mapper.py gene-map-7227.txt EnsemblGenome | sort -k1 > gene-groups-7227-sorted.txt
python utilities/mapper.py gene-map-83333.txt EnsemblGenome | sort -k1 > gene-groups-83333-sorted.txt

echo -e "\nDeleting unused files\n"

rm gene-map*
rm *.dat
#rm *.idmapping

rm UP000006548_3702_additional.fasta
rm UP000005640_9606_additional.fasta
rm UP000000589_10090_additional.fasta
rm UP000002494_10116_additional.fasta
rm UP000000437_7955_additional.fasta
rm UP000002311_559292_additional.fasta
rm UP000002485_284812_additional.fasta
rm UP000000803_7227_additional.fasta
rm UP000000625_83333_additional.fasta

echo -e "\nMoving files to the right directory (relative routes)\n"

mv *.txt groups
mv *.fasta proteins

