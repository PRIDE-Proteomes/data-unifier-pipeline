package uk.ac.ebi.pride.proteomes.pipeline.unifier.grouping;

import uk.ac.ebi.pride.proteomes.db.core.api.protein.groups.ProteinGroup;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * User: ntoro
 * Date: 17/10/2013
 * Time: 11:14
 */
public class Group extends ProteinGroup {

    @NotNull
    private Set<String> proteinAccessions;

    @NotNull
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<String> getProteinAccessions() {
        return proteinAccessions;
    }

    public void setProteinAccessions(Set<String> proteinAccessions) {
        this.proteinAccessions = proteinAccessions;
    }

    @Override
    public String toString() {
        return "Group{" +
                super.toString() +
                ", proteinAccessions=" + proteinAccessions +
                ", type='" + type + '\'' +
                '}';
    }
}
