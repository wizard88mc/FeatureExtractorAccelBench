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

    private List<SingleCoordinateSet> values = new ArrayList<SingleCoordinateSet>();
    private List<SingleCoordinateSet> valuesWithoutGravity = new ArrayList<SingleCoordinateSet>();
    private List<SingleCoordinateSet> valuesRotated = new ArrayList<SingleCoordinateSet>();
    private List<SingleCoordinateSet> valuesWithoutGravityRotated = new ArrayList<SingleCoordinateSet>();
    private List<SingleCoordinateSet> valuesLinear = new ArrayList<SingleCoordinateSet>();
    private List<SingleCoordinateSet> valuesLinearRotated = new ArrayList<SingleCoordinateSet>();
    private static HashMap<Integer, String> coordinates_mapping = new HashMap<Integer, String>();
    private List<IntervalMarker> markers = new ArrayList<IntervalMarker>();
    private String title;
    private String mode;
    private int trunk = 0;
    private int bufferDuration = 2000;
    private DecimalFormat numberFormat = new DecimalFormat("#0.00");
    
    static {
        coordinates_mapping.put(0, "X");
        coordinates_mapping.put(1, "Y");
        coordinates_mapping.put(2, "Z");
        coordinates_mapping.put(3, "|V|");
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
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
    
    public List<SingleCoordinateSet> getValuesWithoutGravity() {
        return valuesWithoutGravity;
    }

    public Batch(List<Sample> samplesAccelerometer, List<Sample> samplesLinear) throws Exception {
        if (samplesAccelerometer.isEmpty()) {
            throw new Exception("No element given for this batch");
        }
        for (int i = 0; i < 4; i++) {
            values.add(new SingleCoordinateSet());
            values.get(i).setTitle(coordinates_mapping.get(i));
        }
        for (int axis = 0; axis < samplesAccelerometer.size(); axis++) {
            Sample sample = samplesAccelerometer.get(axis);
            values.get(0).addValue(new DataTime(sample.getTime(), sample.getValueX(), sample.getStep()));
            values.get(1).addValue(new DataTime(sample.getTime(), sample.getValueY(), sample.getStep()));
            values.get(2).addValue(new DataTime(sample.getTime(), sample.getValueZ(), sample.getStep()));
            values.get(3).addValue(new DataTime(sample.getTime(), sample.getValueV(), sample.getStep()));
        }
    }
    
    public void removeGravity(List<Sample> samplesAccelerometer) {
        
        for (int i = 0; i < values.size(); i++) {
                
            SingleCoordinateSet set = values.get(i);
            
            long meanDistanceTimestamp = 0;
            for (int index = 1; index < set.getValues().size(); index++) {
                meanDistanceTimestamp += (set.getValues().get(index).getTime() - set.getValues().get(index-1).getTime());
            }
            meanDistanceTimestamp /= set.getValues().size();
            
            List<DataTime> buffer = new ArrayList<DataTime>();
            int bufferSize = bufferDuration / (int)(meanDistanceTimestamp / 1000000);
            boolean bufferFull = false;
            int nextPositionPoint = 0; 
            SingleCoordinateSet valuesNoGravity = new SingleCoordinateSet();
            valuesNoGravity.setTitle(set.getTitle());
            
            for (DataTime dataTime : set.getValues()) {
                
                buffer.add(nextPositionPoint, dataTime);
                nextPositionPoint++;
                
                if (nextPositionPoint == bufferSize) {
                    nextPositionPoint = 0;
                    bufferFull = true;
                }
                
                if (bufferFull) {
                    float meanValues = 0;

                    for (int j = 0; j < bufferSize; j++) {
                        meanValues += buffer.get(j).getValue();
                    }
                    
                    meanValues /= bufferSize;
                    
                    System.out.println("Media: " + numberFormat.format(meanValues));
                    
                    valuesNoGravity.addValue(new DataTime(dataTime.getTime(), 
                            dataTime.getValue() - meanValues, dataTime.getStep()));
                }
                else {
                    valuesNoGravity.addValue(new DataTime(dataTime.getTime(), null, dataTime.getStep()));
                }
            }
            valuesWithoutGravity.add(valuesNoGravity);
        }
        
        
    }

    public void printFeatures() {
        List<FeatureSet> features = this.getFeatures();
        System.out.println(StringUtils.join(features, ""));
    }

    public List<FeatureSet> getFeatures() {
        List<FeatureSet> features = new ArrayList<FeatureSet>();
        for (int i = 0; i < values.size(); i++) {
            values.get(i).normalize(values);
            features.add(new FeatureSet(coordinates_mapping.get(i), 
                    values.get(i).getMean(), values.get(i).getVariance(), 
                    values.get(i).getStandardDeviation(), 
                    values.get(i).getMin(), values.get(i).getMax()));
        }
        return features;
    }
    
    public SingleCoordinateSet getRightAxesValues(String name) {
        
        SingleCoordinateSet axesValues = null;
        
        for (SingleCoordinateSet axValues: values) {
            if (axValues.getTitle().equals(name)) {
                axesValues = axValues;
            }
        }
        
        return axesValues;
    }
}
