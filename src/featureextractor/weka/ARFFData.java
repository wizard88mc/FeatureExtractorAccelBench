/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.weka;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Nicola Beghin
 */
public class ARFFData {

    private String className;
    private List<Double> data;
    public final int fraction_digits = 2;

    public ARFFData(String title, List<Double> data) {
        this.className = title;
        this.data = data;
    }

    public List<Double> getNormalizedData() {
        List<Double> normalized_data=new ArrayList<Double>();
        for(Double d: data) {
            normalized_data.add(Math.round(d*Math.pow(10, fraction_digits))/Math.pow(10, fraction_digits));
        }
        return normalized_data;
    }

        
    public List<Double> getData() {
        return data;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setGroupingUsed(false);
        nf.setMaximumFractionDigits(fraction_digits);
        for (Double d : this.data) {
            sb.append(nf.format(d.doubleValue()).toString()).append(",");
        }
        sb.append(className);
        return sb.toString();
    }
}
