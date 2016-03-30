package uk.ac.ebi.pride.proteomes.pipeline.unifier.utils;

import uk.ac.ebi.pride.proteomes.db.core.api.param.Tissue;

import java.util.Iterator;
import java.util.Set;

/**
 * @author ntoro
 * @since 24/03/2016 14:54
 */
public class TissueUtils {
    public static String aggregateTissues(Set<Tissue> tissues) {

        StringBuilder sb = new StringBuilder();
        if (!tissues.isEmpty()) {
            Iterator<Tissue> iterator = tissues.iterator();
            Tissue tissue;
            tissue = iterator.next();
            sb.append(tissue.getCvTerm()).append(" (").append(tissue.getCvName()).append(")");

            while (iterator.hasNext()) {
                tissue = iterator.next();
                sb.append(", ");
                sb.append(tissue.getCvTerm());
                sb.append(" (").append(tissue.getCvName()).append(")");
            }
        } else {
            sb.append("-");
        }

        return sb.toString();
    }
}
