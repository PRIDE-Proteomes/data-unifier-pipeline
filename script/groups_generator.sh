#!/bin/sh
echo -e "\nCleaning previously generated files\n"

rm groups/*.txt
rm proteins/*.fasta

echo -e "\nDownloading uniprot current release id mapping file\n"

curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/idmapping/by_organism/ARATH_3702_idmapping.dat.gz"
curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/idmapping/by_organism/HUMAN_9606_idmapping.dat.gz"
curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/idmapping/by_organism/MOUSE_10090_idmapping.dat.gz"
curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/idmapping/by_organism/RAT_10116_idmapping.dat.gz"

echo -e "\nDownloading uniprot current release proteomes\n"

curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/proteomes/ARATH.fasta.gz"
curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/proteomes/HUMAN.fasta.gz"
curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/proteomes/MOUSE.fasta.gz"
curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/proteomes/RAT.fasta.gz"

echo -e "\nUncompressing gzip files\n"

gzip -d *.gz

echo -e "\nRenaming fasta files to the convention pattern\n"

mv ARATH.fasta uniprot-taxonomy-3702-complete.fasta
mv HUMAN.fasta uniprot-taxonomy-9606-complete.fasta
mv MOUSE.fasta uniprot-taxonomy-10090-complete.fasta
mv RAT.fasta uniprot-taxonomy-10116-complete.fasta

echo -e  "\nGenerating Ensembl genes groups\n"

grep -P "\tEnsemblGenome\t" ARATH_3702_idmapping.dat | sort -k2 | awk -F'\t' 'BEGIN{OFS=FS;}{t=$i;$i=$j;$j=t;$k="GENE\t"$k }1' i=1 j=3 k=2 > gene-groups-3702-sorted.txt
grep -P "\tEnsembl\t" HUMAN_9606_idmapping.dat | sort -k2 | awk -F'\t' 'BEGIN{OFS=FS;}{t=$i;$i=$j;$j=t;$k="GENE\t"$k }1' i=1 j=3 k=2 > gene-groups-9606-sorted.txt
grep -P "\tEnsembl\t" MOUSE_10090_idmapping.dat | sort -k2 | awk -F'\t' 'BEGIN{OFS=FS;}{t=$i;$i=$j;$j=t;$k="GENE\t"$k }1' i=1 j=3 k=2 > gene-groups-10090-sorted.txt
grep -P "\tEnsembl\t" RAT_10116_idmapping.dat | sort -k2 | awk -F'\t' 'BEGIN{OFS=FS;}{t=$i;$i=$j;$j=t;$k="GENE\t"$k }1' i=1 j=3 k=2 > gene-groups-10116-sorted.txt

echo -e "\nGenerating Uniprot entry groups\n"

grep ">" uniprot-taxonomy-3702-complete.fasta | cut -d'|' -f 2,3 | cut -d' ' -f 1 | awk -F'|' 'BEGIN{OFS="\t";}{t=$i;$i=$j;$j=t;aux=split(t,array,"-");$k=array[1]"\tENTRY";}1' i=1 j=3 k=1 | sort -k1 > entry-groups-3702-sorted.txt
grep ">" uniprot-taxonomy-9606-complete.fasta | cut -d'|' -f 2,3 | cut -d' ' -f 1 | awk -F'|' 'BEGIN{OFS="\t";}{t=$i;$i=$j;$j=t;aux=split(t,array,"-");$k=array[1]"\tENTRY";}1' i=1 j=3 k=1 | sort -k1 > entry-groups-9606-sorted.txt
grep ">" uniprot-taxonomy-10090-complete.fasta | cut -d'|' -f 2,3 | cut -d' ' -f 1 | awk -F'|' 'BEGIN{OFS="\t";}{t=$i;$i=$j;$j=t;aux=split(t,array,"-");$k=array[1]"\tENTRY";}1' i=1 j=3 k=1 | sort -k1 > entry-groups-10090-sorted.txt
grep ">" uniprot-taxonomy-10116-complete.fasta | cut -d'|' -f 2,3 | cut -d' ' -f 1 | awk -F'|' 'BEGIN{OFS="\t";}{t=$i;$i=$j;$j=t;aux=split(t,array,"-");$k=array[1]"\tENTRY";}1' i=1 j=3 k=1 | sort -k1 > entry-groups-10116-sorted.txt

echo -e "\nMoving files to the right directory (relative routes)\n"

mv *.txt groups
mv *.fasta proteins

echo -e "\nDeleting *.dat files\n"

rm *.dat
