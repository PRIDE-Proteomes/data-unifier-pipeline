package uk.ac.ebi.pride.proteomes.pipeline.unifier.protein.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ReaderNotOpenException;
import org.springframework.batch.item.file.*;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import uk.ac.ebi.pride.proteomes.db.core.api.protein.CurationLevel;
import uk.ac.ebi.pride.proteomes.db.core.api.protein.Protein;
import uk.ac.ebi.pride.proteomes.db.core.api.utils.ScoreUtils;
import uk.ac.ebi.pride.proteomes.pipeline.unifier.utils.FastaUtils;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.util.regex.Matcher;

/**
 * Created by IntelliJ IDEA.
 * User: noedelta
 * Date: 10/10/2013
 * Time: 20:35
 */

/**
 * Restartable {@link ItemReader} that reads lines from input {@link #setResource(Resource)}. Line is defined by the
 * FASTA file record and pass the String to the processor using PassThroughLineMapper.
 * If an exception is thrown during line mapping it is rethrown as {@link org.springframework.batch.item.file.FlatFileParseException} adding information
 * about the problematic line and its line number.
 *
 * @author Robert Kasanicky
 */
public class FastaFileItemReader extends AbstractItemCountingItemStreamItemReader<Protein> implements
        ResourceAwareItemReaderItemStream<Protein>, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(FlatFileItemReader.class);

    // default encoding for input files
    public static final String DEFAULT_CHARSET = Charset.defaultCharset().name();
    //Hopefully any FASTA header that is not a comment has the ; in the text
    public static final String END_OF_HEADER = ";";
    private static final int BUFFER = 1000;
    private Resource resource;
    private BufferedReader reader;
    private int lineCount = 0;
    private String[] comments = new String[]{";"};
    private boolean noInput = false;
    private String encoding = DEFAULT_CHARSET;
    private boolean strict = true;
    private BufferedReaderFactory bufferedReaderFactory = new DefaultBufferedReaderFactory();
    private Integer taxid;
    private Boolean contaminant;
    private Boolean isoform;

    public FastaFileItemReader() {
        setName(ClassUtils.getShortName(FlatFileItemReader.class));
    }

    /**
     * Parse the header and set the accession and the description in the protein
     *
     * @param header  of the FASTA file
     * @param protein annotated with the header information of the FASTA file
     */
    private static void addProtDesAndAcWithFastaHeader(String header, Protein protein) throws NotUniprotRecordException {
        //uniptrot
        // tr|Q0TET7|Q0TET7_ECOL5 Putative uncharacterized protein OS=Escherichia coli O6:K15:H31 (strain 536 / UPEC) GN=ECP_2553 PE=4 SV=1
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
                addProteinInformationFromDescription(protein, data[2]);
            }

        } else {
            throw new NotUniprotRecordException();
        }
    }

    //TODO Improve reg ex
    private static void addProteinInformationFromDescription(Protein protein, String description) {
        String altId = "";
        String name = "";
        String species = "";
        String geneSymbol = "";
        Integer proteinEvidence = -1;

        String patternStr = "([A-Z_0-9]+)+\\s(.+)\\s+OS=(.+)\\s+GN=([A-Za-z0-9_\\-\\.]+)(\\sPE=([1-5])*)?(\\sSV=([1-5]).*)?";
        java.util.regex.Pattern regExp = java.util.regex.Pattern.compile(patternStr);
        Matcher matcher = regExp.matcher(description);
        boolean matchFound = matcher.matches();

        altId = description;// just in case the parsing fails
        if (matchFound) {
            String match = matcher.group(1);
            if (match != null && !match.isEmpty()) {
                altId = match;
                match = matcher.group(2);
                if (match != null && !match.isEmpty()) {
                    name = match;
                    match = matcher.group(3);
                    if (match != null && !match.isEmpty()) {
                        //Nowaday we use the translation from the taxonId to display the Organism
                        species = match;
                        match = matcher.group(4);
                        if (match != null && !match.isEmpty()) {
                            geneSymbol = match;
                            match = matcher.group(6);
                            if (match != null && !match.isEmpty()) {
                                try {
                                    proteinEvidence = Integer.parseInt(match);
                                } catch (NumberFormatException e) {
                                    //We return  -1 as a protein evidence if the value is not parsable
                                }
                            }
                        }
                    }
                }
            }
        }

        protein.setAlternativeName(altId);
        protein.setName(name);
        protein.setGeneSymbol(geneSymbol);
        protein.setEvidence(proteinEvidence);

    }


    /**
     * In strict mode the reader will throw an exception on
     * {@link #open(org.springframework.batch.item.ExecutionContext)} if the input resource does not exist.
     *
     * @param strict <code>true</code> by default
     */
    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    /**
     * Setter for the encoding for this input source. Default value is {@link #DEFAULT_CHARSET}.
     *
     * @param encoding a properties object which possibly contains the encoding for this input file;
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Factory for the {@link BufferedReader} that will be used to extract lines from the file. The default is fine for
     * plain text files, but this is a useful strategy for binary files where the standard BufferedReader from java.io
     * is limiting.
     *
     * @param bufferedReaderFactory the bufferedReaderFactory to set
     */
    public void setBufferedReaderFactory(BufferedReaderFactory bufferedReaderFactory) {
        this.bufferedReaderFactory = bufferedReaderFactory;
    }

    /**
     * Public setter for the input resource.
     */
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    protected Protein doRead() throws Exception {
        if (noInput) {
            return null;
        }

        Protein protein = readLine();

        if (protein == null) {
            return null;
        } else {
            return protein;
        }
    }

    /**
     * @return next line (skip comments).getCurrentResource
     */
    private Protein readLine() {

        if (reader == null) {
            throw new ReaderNotOpenException("Reader must be open before it can be read.");
        }

        String line = null;
        Protein prot = null;

        try {
            line = this.reader.readLine();
            if (line == null) {
                return null;
            }
            lineCount++;
            while (isComment(line)) {
                line = reader.readLine();
                if (line == null) {
                    return null;
                }
                lineCount++;
            }

            prot = applyFastaRecordSeparatorPolicy(line);
        } catch (Exception e) {
            // Prevent IOException from recurring indefinitely
            // if client keeps catching and re-calling
            noInput = true;
            throw new NonTransientFlatFileException("Unable to read from resource: [" + resource + "]", e, line,
                    lineCount);
        }
        return prot;
    }

    private boolean isComment(String line) {
        for (String prefix : comments) {
            if (line.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doClose() throws Exception {
        lineCount = 0;
        if (reader != null) {
            reader.close();
        }
    }

    @Override
    protected void doOpen() throws Exception {
        Assert.notNull(resource, "Input resource must be set");

        noInput = true;
        if (!resource.exists()) {
            if (strict) {
                throw new IllegalStateException("Input resource must exist (reader is in 'strict' mode): " + resource);
            }
            logger.warn("Input resource does not exist " + resource.getDescription());
            return;
        }

        if (!resource.isReadable()) {
            if (strict) {
                throw new IllegalStateException("Input resource must be readable (reader is in 'strict' mode): "
                        + resource);
            }
            logger.warn("Input resource is not readable " + resource.getDescription());
            return;
        }

        reader = bufferedReaderFactory.create(resource, encoding);

        noInput = false;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(resource, "The resource to read is required");
    }

    @Override
    protected void jumpToItem(int itemIndex) throws Exception {
        for (int i = 0; i < itemIndex; i++) {
            readLine();
        }
    }

    private Protein applyFastaRecordSeparatorPolicy(String line) throws Exception {

        Protein protein = new Protein();

        if (line.startsWith(">")) {
            //We add the sequence separator to avoid conflicts in the mapper
            //We detect the header of the protein

            try {
                addProtDesAndAcWithFastaHeader(line, protein);
            } catch (NotUniprotRecordException e) {
                throw new java.text.ParseException(e.getMessage(), lineCount);
            }
            protein.setTaxid(taxid);

            //TODO: If we see performance issues we can use other strategy to initialize the score value
            protein.setScore(ScoreUtils.defaultScore());

            line = null;
        }


        String record = "";
        do {
            this.reader.mark(BUFFER);
            line = this.reader.readLine();
            if (line == null) {
                if (StringUtils.hasText(record)) {
                    break;
                }
            } else {
                if (line.startsWith(">")) {
                    break;
                } else {
                    lineCount++;
                }
            }
            record = record + line;
        } while (line != null);
        //Rewind the line
        if (line != null && line.startsWith(">")) {
            this.reader.reset();
            lineCount--;
        }

        protein.setSequence(record);
        //Only from the caller we know if this protein is a contaminant or an isoform
        protein.setContaminant(contaminant);
        protein.setIsoform(isoform);

        return protein;

    }

    public Integer getTaxid() {
        return taxid;
    }

    public void setTaxid(Integer taxid) {
        this.taxid = taxid;
    }

    public Boolean getContaminant() {
        return contaminant;
    }

    public void setContaminant(Boolean contaminant) {
        this.contaminant = contaminant;
    }

    public Boolean getIsoform() {
        return isoform;
    }

    public void setIsoform(Boolean isoform) {
        this.isoform = isoform;
    }
}


