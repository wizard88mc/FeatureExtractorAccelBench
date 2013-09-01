/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.model;
import featureextractor.utils.SamplesUtils;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jfree.chart.plot.IntervalMarker;

/**
 *
 * @author Nicola Beghin
 */
public class Batch {
    
    private List<SingleCoordinateSet> values=new ArrayList<SingleCoordinateSet>();
    private static HashMap<Integer,String> coordinates_mapping=new HashMap<Integer,String>();
    private List<IntervalMarker> markers=new ArrayList<IntervalMarker>();
    private String title;
    private int trunk = 0;

    static {
        coordinates_mapping.put(0, "X");
        coordinates_mapping.put(1, "Y");
        coordinates_mapping.put(2, "Z");
        coordinates_mapping.put(3, "|V|");
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public int getTrunk() {
        return trunk;
    }

    public void setTrunk(int trunk) {
        this.trunk = trunk;
    }
    
    
    public int size() {
        return values.get(0).size();
    }

    public List<IntervalMarker> getMarkers() {
        return markers;
    }

    public void setMarkers(List<IntervalMarker> markers) {
        this.markers.addAll(markers);
    }

    public List<SingleCoordinateSet> getValues() {
        return values;
    }
    
    public Batch(List<Sample> samples) throws Exception {
        if (samples.isEmpty()) throw new Exception("No element given for this batch");
        for(int i=0; i<4; i++) {
            values.add(new SingleCoordinateSet());
            values.get(i).setTitle(coordinates_mapping.get(i));
        }
        for (int axis = 0; axis < samples.size(); axis++) {
            Sample sample=samples.get(axis);
            values.get(0).addValue(new DataTime(sample.getTime(), sample.getValueX(), sample.getStep()));
            values.get(1).addValue(new DataTime(sample.getTime(), sample.getValueY(), sample.getStep()));
            values.get(2).addValue(new DataTime(sample.getTime(), sample.getValueZ(), sample.getStep()));
            values.get(3).addValue(new DataTime(sample.getTime(), sample.getValueV(), sample.getStep()));
        }
    }
    
    public void printFeatures() {
        List<FeatureSet> features=this.getFeatures();
        System.out.println(StringUtils.join(features, ""));
    }
    
    public List<FeatureSet> getFeatures() {
        List<FeatureSet> features=new ArrayList<FeatureSet>();
        for(int i=0; i<values.size(); i++) {
            values.get(i).normalize(values);
            features.add(new FeatureSet(coordinates_mapping.get(i), values.get(i).getMean(), values.get(i).getVariance(), values.get(i).getStandardDeviation()));
        }
        return features;
    }
    
}
