package uk.ac.ebi.pride.proteomes.pipeline.unifier.utils;

import java.util.ArrayList;

/**
 * User: ntoro
 * Date: 11/10/2013
 * Time: 11:56
 */
public class FastaUtils {

    /*This parser is based on the BioJava parser for FASTA*/
    public static String[] getHeaderValues(String header) {
        String[] data = new String[0];
        ArrayList<String> values = new ArrayList<String>();
        StringBuffer sb = new StringBuffer();
        int index = header.indexOf("length=");
        if (index != -1) {
            data = new String[1];
            data[0] = header.substring(0, index).trim();
            //        System.out.println("accession=" + data[0]);
            return data;
        } else if (!header.startsWith("PDB:")) {
            for (int i = 0; i < header.length(); i++) {
                if (header.charAt(i) == '|') {
                    values.add(sb.toString());
                    sb = new StringBuffer();
                } else if (i == header.length() - 1) {
                    sb.append(header.charAt(i));
                    values.add(sb.toString());
                } else {
                    sb.append(header.charAt(i));
                }

                data = new String[values.size()];
                values.toArray(data);
            }
        } else {
            data = header.split(" ");
        }
        return data;
    }
}
