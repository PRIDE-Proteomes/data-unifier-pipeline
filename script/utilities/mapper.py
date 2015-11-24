import csv
import sys

genes = sys.argv[1]
gene_id = sys.argv[2]


def main(genes, gene_id):
    gene_map = {}

    with open(genes, 'r') as f:
        genes_rows = csv.reader(f, delimiter='\t')
        gene = ''
        for row in genes_rows:
            if row[1] == gene_id:  # Gene detection
                gene = row[2]  # Gene
            # We don't update the gene if it is not a parent entry because the information
            #  it not provided for the isoforms in the mapping file

            if not gene in gene_map:
                gene_map[gene] = set()

            gene_map[gene].add(row[0])  # Proteins

    for key, values in gene_map.viewitems():
        print ("{0}\tGENE\t{1}\t{2}".format(key, gene_id,",".join(sorted(values))))

if __name__ == '__main__':
    main(genes, gene_id)
