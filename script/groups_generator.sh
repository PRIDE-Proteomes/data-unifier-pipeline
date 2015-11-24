#!/bin/sh
echo -e "\nCleaning previously generated files\n"

rm groups/*.txt
rm proteins/uniprot-taxonomy-*-complete.fasta

echo -e "\nDownloading uniprot current release proteomes proteins\n"

# New ftp for proteomes in Uniprot
curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000006548_3702_additional.fasta.gz"
curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000005640_9606_additional.fasta.gz"
curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000000589_10090_additional.fasta.gz"
curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000002494_10116_additional.fasta.gz"

curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000006548_3702.fasta.gz"
curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000005640_9606.fasta.gz"
curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000000589_10090.fasta.gz"
curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000002494_10116.fasta.gz"

echo -e "\nDownloading uniprot current release id mapping file\n"
# New ftp for proteomes in Uniprot

# We can not use the mapping file for the uniprot proteomes ftp becasue it doesn't keep the relatioship with the entry that allow us
# the mapping to the isoforms. For the moment we will use the mapping file for the whole species.

#curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000006548_3702.idmapping.gz"
#curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000005640_9606.idmapping.gz"
#curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000000589_10090.idmapping.gz"
#curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000002494_10116.idmapping.gz"

curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/idmapping/by_organism/ARATH_3702_idmapping.dat.gz"
curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/idmapping/by_organism/HUMAN_9606_idmapping.dat.gz"
curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/idmapping/by_organism/MOUSE_10090_idmapping.dat.gz"
curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/idmapping/by_organism/RAT_10116_idmapping.dat.gz"

echo -e "\nUncompressing gzip files\n"

gzip -d *.gz

echo -e "\nRenaming fasta files to the convention pattern\n"

mv UP000006548_3702.fasta uniprot-taxonomy-3702-complete.fasta
mv UP000005640_9606.fasta uniprot-taxonomy-9606-complete.fasta
mv UP000000589_10090.fasta uniprot-taxonomy-10090-complete.fasta
mv UP000002494_10116.fasta uniprot-taxonomy-10116-complete.fasta

cat UP000006548_3702_additional.fasta >> uniprot-taxonomy-3702-complete.fasta
cat UP000005640_9606_additional.fasta >> uniprot-taxonomy-9606-complete.fasta
cat UP000000589_10090_additional.fasta >> uniprot-taxonomy-10090-complete.fasta
cat UP000002494_10116_additional.fasta >> uniprot-taxonomy-10116-complete.fasta

echo -e  "\nGenerating Ensembl genes mapping files\n"

grep "Ensembl" ARATH_3702_idmapping.dat > gene-map-3702.txt
grep "Ensembl" HUMAN_9606_idmapping.dat > gene-map-9606.txt
grep "Ensembl" MOUSE_10090_idmapping.dat > gene-map-10090.txt
grep "Ensembl" RAT_10116_idmapping.dat > gene-map-10116.txt

echo -e "\nGenerating genes groups\n"

python utilities/mapper.py gene-map-3702.txt EnsemblGenome | sort -k1 > gene-groups-3702-sorted.txt
python utilities/mapper.py gene-map-9606.txt Ensembl | sort -k1 > gene-groups-9606-sorted.txt
python utilities/mapper.py gene-map-10090.txt Ensembl | sort -k1 > gene-groups-10090-sorted.txt
python utilities/mapper.py gene-map-10116.txt Ensembl | sort -k1 > gene-groups-10116-sorted.txt

echo -e "\nDeleting unused files\n"

rm gene-map*
rm *.dat
#rm *.idmapping

rm UP000006548_3702_additional.fasta
rm UP000005640_9606_additional.fasta
rm UP000000589_10090_additional.fasta
rm UP000002494_10116_additional.fasta


echo -e "\nMoving files to the right directory (relative routes)\n"

mv *.txt groups
mv *.fasta proteins

