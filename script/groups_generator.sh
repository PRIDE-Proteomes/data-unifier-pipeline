#!/bin/sh
echo -e "\nCleaning previously generated files\n"

rm groups/*.txt
rm proteins/*.fasta

echo -e "\nDownloading uniprot current release proteomes\n"

# New ftp for proteomes in Uniprot
curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000006548_3702_additional.fasta.gz"
curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000005640_9606_additional.fasta.gz"
curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000000589_10090_additional.fasta.gz"
curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000002494_10116_additional.fasta.gz"

echo -e "\nDownloading uniprot current release id mapping file\n"
# New ftp for proteomes in Uniprot

curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000006548_3702.idmapping.gz"
curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000005640_9606.idmapping.gz"
curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000000589_10090.idmapping.gz"
curl -O "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/reference_proteomes/Eukaryota/UP000002494_10116.idmapping.gz"

echo -e "\nUncompressing gzip files\n"

gzip -d *.gz

echo -e "\nRenaming fasta files to the convention pattern\n"

mv UP000006548_3702_additional.fasta uniprot-taxonomy-3702-complete.fasta
mv UP000005640_9606_additional.fasta uniprot-taxonomy-9606-complete.fasta
mv UP000000589_10090_additional.fasta uniprot-taxonomy-10090-complete.fasta
mv UP000002494_10116_additional.fasta uniprot-taxonomy-10116-complete.fasta

echo -e  "\nGenerating Ensembl genes groups\n"

grep -P "\tEnsemblGenome\t" UP000006548_3702.idmapping | sort -k2 | awk -F'\t' 'BEGIN{OFS=FS;}{t=$i;$i=$j;$j=t;$k="GENE\t"$k }1' i=1 j=3 k=2 > gene-groups-3702-sorted.txt
grep -P "\tEnsembl\t" UP000005640_9606.idmapping | sort -k2 | awk -F'\t' 'BEGIN{OFS=FS;}{t=$i;$i=$j;$j=t;$k="GENE\t"$k }1' i=1 j=3 k=2 > gene-groups-9606-sorted.txt
grep -P "\tEnsembl\t" UP000000589_10090.idmapping | sort -k2 | awk -F'\t' 'BEGIN{OFS=FS;}{t=$i;$i=$j;$j=t;$k="GENE\t"$k }1' i=1 j=3 k=2 > gene-groups-10090-sorted.txt
grep -P "\tEnsembl\t" UP000002494_10116.idmapping | sort -k2 | awk -F'\t' 'BEGIN{OFS=FS;}{t=$i;$i=$j;$j=t;$k="GENE\t"$k }1' i=1 j=3 k=2 > gene-groups-10116-sorted.txt

echo -e "\nGenerating Uniprot entry groups\n"

grep ">" uniprot-taxonomy-3702-complete.fasta | cut -d'|' -f 2,3 | cut -d' ' -f 1 | awk -F'|' 'BEGIN{OFS="\t";}{t=$i;$i=$j;$j=t;aux=split(t,array,"-");$k=array[1]"\tENTRY";}1' i=1 j=3 k=1 | sort -k1 > entry-groups-3702-sorted.txt
grep ">" uniprot-taxonomy-9606-complete.fasta | cut -d'|' -f 2,3 | cut -d' ' -f 1 | awk -F'|' 'BEGIN{OFS="\t";}{t=$i;$i=$j;$j=t;aux=split(t,array,"-");$k=array[1]"\tENTRY";}1' i=1 j=3 k=1 | sort -k1 > entry-groups-9606-sorted.txt
grep ">" uniprot-taxonomy-10090-complete.fasta | cut -d'|' -f 2,3 | cut -d' ' -f 1 | awk -F'|' 'BEGIN{OFS="\t";}{t=$i;$i=$j;$j=t;aux=split(t,array,"-");$k=array[1]"\tENTRY";}1' i=1 j=3 k=1 | sort -k1 > entry-groups-10090-sorted.txt
grep ">" uniprot-taxonomy-10116-complete.fasta | cut -d'|' -f 2,3 | cut -d' ' -f 1 | awk -F'|' 'BEGIN{OFS="\t";}{t=$i;$i=$j;$j=t;aux=split(t,array,"-");$k=array[1]"\tENTRY";}1' i=1 j=3 k=1 | sort -k1 > entry-groups-10116-sorted.txt

echo -e "\nMoving files to the right directory (relative routes)\n"

mv *.txt groups
mv *.fasta proteins

echo -e "\nDeleting *.idmapping files\n"

rm *.idmapping
