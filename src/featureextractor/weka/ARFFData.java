/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.weka;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Nicola Beghin
 */
public class ARFFData {

    private String title;
    private List<Double> data;
    private final int fraction_digits = 2;

    public ARFFData(String title, List<Double> data) {
        this.title = title;
        this.data = data;
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
        sb.append(title);
        return sb.toString();
    }
}
