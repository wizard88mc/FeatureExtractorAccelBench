/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.model;
import featureextractor.SamplesUtils;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Nicola Beghin
 */
public class Batch {
    
    private ArrayList<SingleCoordinateSet> values=new ArrayList<SingleCoordinateSet>();
    private static HashMap<Integer,String> coordinates_mapping=new HashMap<Integer,String>();

    static {
        coordinates_mapping.put(0, "X");
        coordinates_mapping.put(1, "Y");
        coordinates_mapping.put(2, "Z");
        coordinates_mapping.put(3, "|V|");
    }
    
    public int size() {
        return values.get(0).size();
    }
    
    public Batch(List<Sample> samples) {
        for(int i=0; i<4; i++) {
            values.add(new SingleCoordinateSet());
        }
        for (int i = 0; i < samples.size(); i++) {
            values.get(0).addValue(new DataTime(samples.get(i).time, samples.get(i).valueX));
            values.get(1).addValue(new DataTime(samples.get(i).time, samples.get(i).valueY));
            values.get(2).addValue(new DataTime(samples.get(i).time, samples.get(i).valueZ));
            values.get(3).addValue(new DataTime(samples.get(i).time, samples.get(i).valueV));
        }
    }
    
    public void printFeatures() {
        List<FeatureSet> features=this.getFeatures();
        System.out.println(StringUtils.join(features, ""));
    }
    
    public List<FeatureSet> getFeatures() {
        List<FeatureSet> features=new ArrayList<FeatureSet>();
        for(int i=0; i<values.size(); i++) {
            values.get(i).normalize();
            features.add(new FeatureSet(coordinates_mapping.get(i), values.get(i).getMean(), values.get(i).getVariance(), values.get(i).getStandardDeviation()));
        }
        return features;
    }
    
}
