/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.weka;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Nicola Beghin
 */
public class ARFFData {
    private String title;
    private List<Double> data;

    public ARFFData(String title, List<Double> data) {
        this.title = title;
        this.data = data;
    }
    
    @Override
    public String toString() {
        return StringUtils.join(data, ",")+","+title;
    }
    
}
