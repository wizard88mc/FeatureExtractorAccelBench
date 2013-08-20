/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.weka;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author ark0n3
 */
public class ARFF {
    private String title="StairDetection";
    private List<ARFFAttribute> attributes=new ArrayList<ARFFAttribute>();
    private List<String> classes=new ArrayList<String>();
    private List<ARFFData> data=new ArrayList<ARFFData>();
    
    public ARFF(String title, List<String> classes, List<ARFFAttribute> attributes) {
        this.title=title;
        this.classes=classes;
        this.attributes=attributes;
    }

    
    public void writeToFile(File file) throws IOException {
        if (file.exists()) file.delete();
        FileUtils.writeStringToFile(file, this.toString());
    }
    
    public void addData(List<ARFFData> data) {
        this.data.addAll(data);
    }
    
    public void addData(ARFFData data) {
        this.data.add(data);
    }
    
    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append("@RELATION "+title);
        for(ARFFAttribute attribute: attributes) {
            sb.append("\n"+attribute);
        }
        sb.append("\n@ATTRIBUTE class {"+StringUtils.join(classes)+"}");
        for (ARFFData row: data) {
            sb.append("\n"+row);
        }
        return sb.toString();
    }    
    
}
