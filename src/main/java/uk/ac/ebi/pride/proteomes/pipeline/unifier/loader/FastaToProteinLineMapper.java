package uk.ac.ebi.pride.proteomes.pipeline.unifier.loader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import uk.ac.ebi.pride.proteomes.db.core.api.protein.CurationLevel;
import uk.ac.ebi.pride.proteomes.db.core.api.protein.Protein;
import uk.ac.ebi.pride.proteomes.db.core.api.utils.ScoreUtils;
import uk.ac.ebi.pride.proteomes.pipeline.unifier.utils.FastaUtils;

import java.text.ParseException;

/**
 * User: ntoro
 * Date: 11/10/2013
 * Time: 11:18
 */
public class FastaToProteinLineMapper implements LineMapper<Protein>, InitializingBean {

    private static final Log logger = LogFactory.getLog(FastaToProteinLineMapper.class);

    private Integer taxid;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(taxid, "The taxonomy id is required");
    }

    @Override
    public Protein mapLine(String line, int lineNumber) throws Exception {

        String[] splitLine = line.split(FastaFileItemReader.END_OF_HEADER);
        if (splitLine.length != 2) {
            throw new ParseException("The header and the sequence in the Fasta record can not be splitted", lineNumber);
        }

        String header = splitLine[0];
        String sequence = splitLine[1];

        Protein protein = new Protein();

        try {
            addProtDesAndAcWithFastaHeader(header, protein);
        } catch (NotUniprotRecordException e) {
            throw new ParseException(e.getMessage(), lineNumber);
        }
        protein.setTaxid(taxid);
        protein.setSequence(sequence);

        //TODO: If we see performance issues we can use other strategy to initialize the score value
        protein.setScore(ScoreUtils.defaultScore());

        return protein;
    }


    /**
     * Parse the header and set the accession and the description in the protein
     *
     * @param header  of the FASTA file
     * @param protein annotated with the header information of the FASTA file
     */
    private static void addProtDesAndAcWithFastaHeader(String header, Protein protein) throws NotUniprotRecordException {
        //uniptrot
        // tr|Q0TET7|Q0TET7_ECOL5 Putative uncharacterized protein OS=Escherichia coli O6:K15:H31 (strain 536 / UPEC) GN= PE=4 SV=1
        String[] data = FastaUtils.getHeaderValues(header);

        if (data.length == 1) {
            protein.setProteinAccession(data[0]);
        } else if (data[0].equalsIgnoreCase(">sp") || data[0].equalsIgnoreCase(">tr")) {
            // TODO: Maybe in the future is good to know if the protein is a TREMBL one or
            // not by means of a CVTerm, now is a enum type

            if (data[0].equalsIgnoreCase(">sp")) {
                protein.setCurationLevel(CurationLevel.CURATED);
            } else {
                protein.setCurationLevel(CurationLevel.PREDICTED);
            }

            protein.setProteinAccession(data[1]);
            // TODO: Maybe in the future is good to know the data source adding a CVTerm
            // protein.setDataSource(UNIPROT);

            if (data.length > 1) {
                protein.setDescription(data[2]);
            }

        } else {
            throw new NotUniprotRecordException();
        }
    }

    public void setTaxid(Integer taxid) {
        this.taxid = taxid;
    }

    public Integer getTaxid() {
        return taxid;
    }


}
