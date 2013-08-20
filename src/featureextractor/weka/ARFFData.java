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
 * @author ark0n3
 */
public class ARFFData {
    private String title;
    private List<Object> data;

    public ARFFData(String title, List<Object> data) {
        this.title = title;
        this.data = data;
    }
    
    @Override
    public String toString() {
        return StringUtils.join(data, ",")+title;
    }
    
}
