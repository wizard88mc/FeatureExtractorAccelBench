/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 *
 * @author ark0n3
 */
public class SamplesBatch {
    
    private ArrayList<SingleCoordinateValues> values=new ArrayList<SingleCoordinateValues>();
    private static HashMap<Integer,String> coordinates_mapping=new HashMap<Integer,String>();

    static {
        coordinates_mapping.put(0, "X");
        coordinates_mapping.put(1, "Y");
        coordinates_mapping.put(2, "Z");
    }
    
    public SamplesBatch(ArrayList<Sample> samples) {
        for(int i=0; i<3; i++) {
            values.add(new SingleCoordinateValues());
        }
        for (int i = 0; i < samples.size(); i++) {
            values.get(0).addValue(new CoupleTimeData(samples.get(i).time, samples.get(i).valueX));
            values.get(1).addValue(new CoupleTimeData(samples.get(i).time, samples.get(i).valueY));
            values.get(2).addValue(new CoupleTimeData(samples.get(i).time, samples.get(i).valueZ));
        }
    }
    
    public void getFeatures() {
        for(int i=0; i<values.size(); i++) {
            values.get(i).normalize(values);
            System.out.println(coordinates_mapping.get(i)+"\tMEAN: "+values.get(i).getMean()+"\tVARIANCE: "+values.get(i).getVariance()+"\tSTD: "+values.get(i).getStandardDeviation());
        }
    }
    
    public void getFeatures2() {
        for(int i=0; i<values.size(); i++) {
            values.get(i).normalize();
            System.out.println(coordinates_mapping.get(i)+"\tMEAN: "+values.get(i).getMean()+"\tVARIANCE: "+values.get(i).getVariance()+"\tSTD: "+values.get(i).getStandardDeviation());
        }
    }
    
}
