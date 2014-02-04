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
 * This class represents a single 
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
    private int bufferDuration = 500;
    private DecimalFormat numberFormat = new DecimalFormat("#0.00");
    
    static {
        coordinates_mapping.put(0, "X");
        coordinates_mapping.put(1, "Y");
        coordinates_mapping.put(2, "Z");
        coordinates_mapping.put(3, "X+Y");
        //coordinates_mapping.put(3, "|V|");
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
    
    public List<SingleCoordinateSet> getLinearValues() {
        return valuesLinear;
    }
    
    public List<SingleCoordinateSet> getValuesRotated() {
        return valuesRotated;
    }
    
    public List<SingleCoordinateSet> getValuesWithoutGravityRotated() {
        return valuesWithoutGravityRotated;
    }
    
    public List<SingleCoordinateSet> getLinearValuesRotated() {
        return valuesLinearRotated;
    }

    public Batch(List<Sample> samplesAccelerometer, List<Sample> samplesLinear) throws Exception {
        if (samplesAccelerometer.isEmpty()) {
            throw new Exception("No element given for this batch");
        }
        for (int i = 0; i < coordinates_mapping.size(); i++) {
            values.add(new SingleCoordinateSet());
            values.get(i).setTitle(coordinates_mapping.get(i));
            
            valuesWithoutGravity.add(new SingleCoordinateSet());
            valuesWithoutGravity.get(i).setTitle(coordinates_mapping.get(i));
            
            valuesRotated.add(new SingleCoordinateSet());
            valuesRotated.get(i).setTitle(coordinates_mapping.get(i));
            
            valuesWithoutGravityRotated.add(new SingleCoordinateSet());
            valuesWithoutGravityRotated.get(i).setTitle(coordinates_mapping.get(i));
            
            valuesLinear.add(new SingleCoordinateSet());
            valuesLinear.get(i).setTitle(coordinates_mapping.get(i));
            
            valuesLinearRotated.add(new SingleCoordinateSet());
            valuesLinearRotated.get(i).setTitle(coordinates_mapping.get(i));
        }
        
        removeGravity(samplesAccelerometer);
        
        for (int axis = 0; axis < samplesAccelerometer.size(); axis++) {
            Sample sample = samplesAccelerometer.get(axis);
            values.get(0).addValue(new DataTime(sample.getTime(), sample.getValueX(), sample.getStep()));
            values.get(1).addValue(new DataTime(sample.getTime(), sample.getValueY(), sample.getStep()));
            values.get(2).addValue(new DataTime(sample.getTime(), sample.getValueZ(), sample.getStep()));
            values.get(3).addValue(new DataTime(sample.getTime(), sample.getValueXAndYMean(), sample.getStep()));
            //values.get(3).addValue(new DataTime(sample.getTime(), sample.getValueV(), sample.getStep()));
            
            valuesWithoutGravity.get(0).addValue(new DataTime(sample.getTime(), sample.getNoGravityX(), sample.getStep()));
            valuesWithoutGravity.get(1).addValue(new DataTime(sample.getTime(), sample.getNoGravityY(), sample.getStep()));
            valuesWithoutGravity.get(2).addValue(new DataTime(sample.getTime(), sample.getNoGravityZ(), sample.getStep()));
            valuesWithoutGravity.get(3).addValue(new DataTime(sample.getTime(), sample.getNoGravityXAndYMean(), sample.getStep()));
            
            valuesRotated.get(0).addValue(new DataTime(sample.getTime(), sample.getRotatedX(), sample.getStep()));
            valuesRotated.get(1).addValue(new DataTime(sample.getTime(), sample.getRotatedY(), sample.getStep()));
            valuesRotated.get(2).addValue(new DataTime(sample.getTime(), sample.getRotatedZ(), sample.getStep()));
            valuesRotated.get(3).addValue(new DataTime(sample.getTime(), sample.getRotatedXAndYMean(), sample.getStep()));
            
            valuesWithoutGravityRotated.get(0).addValue(new DataTime(sample.getTime(), sample.getRotatedNoGravityX(), sample.getStep()));
            valuesWithoutGravityRotated.get(1).addValue(new DataTime(sample.getTime(), sample.getRotatedNoGravityY(), sample.getStep()));
            valuesWithoutGravityRotated.get(2).addValue(new DataTime(sample.getTime(), sample.getRotatedNoGravityZ(), sample.getStep()));
            valuesWithoutGravityRotated.get(3).addValue(new DataTime(sample.getTime(), sample.getRotatedNoGravityXAndYMean(), sample.getStep()));
        }
        
        if (samplesLinear != null) {
            for (int i = 0; i < samplesLinear.size(); i++) {

                Sample sample = samplesLinear.get(i);
                valuesLinear.get(0).addValue(new DataTime(sample.getTime(), sample.getValueX(), sample.getStep()));
                valuesLinear.get(1).addValue(new DataTime(sample.getTime(), sample.getValueY(), sample.getStep()));
                valuesLinear.get(2).addValue(new DataTime(sample.getTime(), sample.getValueZ(), sample.getStep()));
                valuesLinear.get(3).addValue(new DataTime(sample.getTime(), sample.getValueXAndYMean(), sample.getStep()));

                valuesLinearRotated.get(0).addValue(new DataTime(sample.getTime(), sample.getRotatedX(), sample.getStep()));
                valuesLinearRotated.get(1).addValue(new DataTime(sample.getTime(), sample.getRotatedY(), sample.getStep()));
                valuesLinearRotated.get(2).addValue(new DataTime(sample.getTime(), sample.getRotatedZ(), sample.getStep()));
                valuesLinearRotated.get(3).addValue(new DataTime(sample.getTime(), sample.getRotatedXAndYMean(), sample.getStep()));
            }
        }
        else { throw new Exception("No linear values provided"); }
    }
    
    public Batch(List<Sample> samples) throws Exception {
        this(samples, null);
    }
    
    public void removeGravity(List<Sample> samplesAccelerometer) {
        
        long meanDistanceTimestamp = 0;
        for (int index = 1; index < samplesAccelerometer.size(); index++) {
            meanDistanceTimestamp += (samplesAccelerometer.get(index).getTime() - samplesAccelerometer.get(index-1).getTime());
        }
        meanDistanceTimestamp /= samplesAccelerometer.size();
        
        List<Sample> buffer = new ArrayList<Sample>();
        int bufferSize = bufferDuration / (int)(meanDistanceTimestamp / 1000000);
        boolean bufferFull = false;
        int nextPositionPoint = 0; 

        for (int index = 0; index < samplesAccelerometer.size(); index++) {
            
            Sample sample = samplesAccelerometer.get(index);
            buffer.add(nextPositionPoint, sample);
            nextPositionPoint++;
            
            if (nextPositionPoint == bufferSize) {
                nextPositionPoint = 0;
                bufferFull = true;
            }
            
            if (bufferFull) {
                
                float meanValueX = 0, meanValueY = 0, meanValueZ = 0;
                
                for (int i = 0; i < bufferSize; i++) {
                    meanValueX += buffer.get(i).getValueX();
                    meanValueY += buffer.get(i).getValueY();
                    meanValueZ += buffer.get(i).getValueZ();
                }
                
                meanValueX /= bufferSize;
                meanValueY /= bufferSize;
                meanValueZ /= bufferSize;
                
                sample.hasNoGravityValues();
                sample.setNoGravityX(sample.getValueX() - meanValueX);
                sample.setNoGravityY(sample.getValueY() - meanValueY);
                sample.setNoGravityZ(sample.getValueZ() - meanValueZ);
                
            }
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
