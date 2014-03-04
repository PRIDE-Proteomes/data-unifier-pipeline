package uk.ac.ebi.pride.proteomes.pipeline.unifier.grouping;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;

import java.util.*;

/**
 * User: ntoro
 * Date: 01/11/2013
 * Time: 11:42
 */
public class ProteinGroupingFieldSetMapper implements FieldSetMapper<Group>, InitializingBean {

    private static final String PROT_GROUP_ID = "id";
    private static final String PROT_GROUP_TYPE = "type";
    private static final String PROT_GROUP_DESCRIPTION = "description";
    private static final String PROT_GROUP_PROTEINS = "proteins";
    private static final String PROTEIN_DELIMITER = ",";

    private Integer taxid;

    public Group mapFieldSet(FieldSet fs) throws BindException {

        Group proteinGroup = new Group();
        proteinGroup.setType(fs.readString(PROT_GROUP_TYPE));
        proteinGroup.setId(fs.readString(PROT_GROUP_ID));
        proteinGroup.setDescription(fs.readString(PROT_GROUP_DESCRIPTION));
        proteinGroup.setTaxid(taxid);

        if(proteinGroup.getProteinAccessions()==null){
            proteinGroup.setProteinAccessions(new HashSet<String>());
        }

        proteinGroup.getProteinAccessions().addAll(Arrays.asList(fs.readString(PROT_GROUP_PROTEINS).split(PROTEIN_DELIMITER)));

        return proteinGroup;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(taxid, "The taxonomy id is required");
    }

    public void setTaxid(Integer taxid) {
        this.taxid = taxid;
    }
}
