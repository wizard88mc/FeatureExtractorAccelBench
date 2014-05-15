package featureextractor.model;

import featureextractor.App;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jfree.chart.plot.IntervalMarker;

/**
 * This class represents a single batch
 * @author Nicola Beghin
 */
public class Batch {

    private final List<SingleCoordinateSet> values = new ArrayList<SingleCoordinateSet>();
    private final List<SingleCoordinateSet> valuesWithoutGravityRotated = new ArrayList<SingleCoordinateSet>();
    private final List<SingleCoordinateSet> valuesLinear = new ArrayList<SingleCoordinateSet>();
    private final List<SingleCoordinateSet> valuesLinearRotated = new ArrayList<SingleCoordinateSet>();
    private final List<SingleCoordinateSet> valuesPMitzell = new ArrayList<SingleCoordinateSet>();
    private final List<SingleCoordinateSet> valuesHMitzell = new ArrayList<SingleCoordinateSet>();
    private static HashMap<Integer, String> coordinates_mapping = new HashMap<Integer, String>();
    private List<IntervalMarker> markers = new ArrayList<IntervalMarker>();
    private String title;
    private String sex;
    private String height;
    private String shoes; 
    private String mode;
    private String action;
    private int trunk = 0;
    private int bufferDuration = 500000000;
    private DecimalFormat numberFormat = new DecimalFormat("#0.00");
    
    static {
        coordinates_mapping.put(0, "X");
        coordinates_mapping.put(1, "Y");
        coordinates_mapping.put(2, "Z");
        
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
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getAction() {
        return this.action;
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
    
    public List<SingleCoordinateSet> getLinearValues() {
        return valuesLinear;
    }
    
    public List<SingleCoordinateSet> getValuesWithoutGravityRotated() {
        return valuesWithoutGravityRotated;
    }
    
    public List<SingleCoordinateSet> getLinearValuesRotated() {
        return valuesLinearRotated;
    }
    
    public List<SingleCoordinateSet> getPVectorMitzell() {
        return valuesPMitzell;
    }
    
    public List<SingleCoordinateSet> getHVectorMitzell() {
        return valuesHMitzell;
    }

    public Batch(String sex, String height, String shoes, 
            List<Sample> samplesAccelerometer, List<Sample> samplesLinear) throws Exception {
        
        if (samplesAccelerometer.isEmpty()) {
            throw new Exception("No element given for this batch");
        }
        for (int i = 0; i < coordinates_mapping.size(); i++) {
            values.add(new SingleCoordinateSet());
            values.get(i).setTitle(coordinates_mapping.get(i));
            
            valuesWithoutGravityRotated.add(new SingleCoordinateSet());
            valuesWithoutGravityRotated.get(i).setTitle(coordinates_mapping.get(i));
            
            valuesLinear.add(new SingleCoordinateSet());
            valuesLinear.get(i).setTitle(coordinates_mapping.get(i));
            
            valuesLinearRotated.add(new SingleCoordinateSet());
            valuesLinearRotated.get(i).setTitle(coordinates_mapping.get(i));
        }
        
        for (int i = 0; i < 3; i++) {
            valuesPMitzell.add(new SingleCoordinateSet(coordinates_mapping.get(i)));
            valuesHMitzell.add(new SingleCoordinateSet(coordinates_mapping.get(i)));
        }
        
        removeGravity(samplesAccelerometer);
        
        for (int axis = 0; axis < samplesAccelerometer.size(); axis++) {
            Sample sample = samplesAccelerometer.get(axis);
            values.get(0).addValue(new DataTime(sample.getTime(), sample.getValueX(), sample.getStep()));
            values.get(1).addValue(new DataTime(sample.getTime(), sample.getValueY(), sample.getStep()));
            values.get(2).addValue(new DataTime(sample.getTime(), sample.getValueZ(), sample.getStep()));
            
            if (sample.getHasNoGravityValues()) {
                valuesWithoutGravityRotated.get(0).addValue(new DataTime(sample.getTime(), sample.getRotatedNoGravityX(), sample.getStep()));
                valuesWithoutGravityRotated.get(1).addValue(new DataTime(sample.getTime(), sample.getRotatedNoGravityY(), sample.getStep()));
                valuesWithoutGravityRotated.get(2).addValue(new DataTime(sample.getTime(), sample.getRotatedNoGravityZ(), sample.getStep()));
            }
        }
        
        if (samplesLinear != null) {
            for (int i = 0; i < samplesLinear.size(); i++) {

                Sample sample = samplesLinear.get(i);
                valuesLinear.get(0).addValue(new DataTime(sample.getTime(), sample.getValueX(), sample.getStep()));
                valuesLinear.get(1).addValue(new DataTime(sample.getTime(), sample.getValueY(), sample.getStep()));
                valuesLinear.get(2).addValue(new DataTime(sample.getTime(), sample.getValueZ(), sample.getStep()));

                valuesLinearRotated.get(0).addValue(new DataTime(sample.getTime(), sample.getRotatedX(), sample.getStep()));
                valuesLinearRotated.get(1).addValue(new DataTime(sample.getTime(), sample.getRotatedY(), sample.getStep()));
                valuesLinearRotated.get(2).addValue(new DataTime(sample.getTime(), sample.getRotatedZ(), sample.getStep()));
            }
        }
        else { throw new Exception("No linear values provided"); }
    }
    
    public Batch(String sex, String height, String shoes, List<Sample> samples) throws Exception {
        this(sex, height, shoes, samples, null);
    }
    
    public void removeGravity(List<Sample> samplesAccelerometer) {
        
        List<Sample> buffer = new ArrayList<Sample>();
        boolean bufferFull = false;

        for (int index = 0; index < samplesAccelerometer.size(); index++) {
            
            Sample sample = samplesAccelerometer.get(index);
            
            if (buffer.size() > 0 && (sample.getTime() - buffer.get(0).getTime()) > 
                    bufferDuration) {
                bufferFull = true;
            }
            else {
                bufferFull = false;
                buffer.add(sample);
            }
            
            if (bufferFull) {
                
                float meanValueX = 0, meanValueY = 0, meanValueZ = 0;
                
                for (int i = 0; i < buffer.size(); i++) {
                    meanValueX += buffer.get(i).getValueX();
                    meanValueY += buffer.get(i).getValueY();
                    meanValueZ += buffer.get(i).getValueZ();
                }
                
                meanValueX /= buffer.size();
                meanValueY /= buffer.size();
                meanValueZ /= buffer.size();
                
                sample.hasNoGravityValues();
                sample.setNoGravityX(sample.getValueX() - meanValueX);
                sample.setNoGravityY(sample.getValueY() - meanValueY);
                sample.setNoGravityZ(sample.getValueZ() - meanValueZ);
                
                buffer.remove(0);
                buffer.add(sample);
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
                    values.get(i).getMean(-1), values.get(i).getVariance(-1), 
                    values.get(i).getStandardDeviation(-1), 
                    values.get(i).getMin(-1), values.get(i).getMax(-1)));
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
    
    /**
     * Retrieves the index of the value List<values> with the required
     * timestamp value
     * 
     * @param time: the searched timestamp
     * @return the index in the values list with the right timestamp
     */
    public int getRealIndexForTimestamp(double time) {
        
        int index = -1;
        boolean found = false;
        
        for (int i = 0; i < values.get(0).getValues().size() && !found; i++) {
            if (values.get(0).getValues().get(i).getTime() == time) {
                index = i;
                found = true;
            }
        }
        
        return index;
    }
    
    public boolean isSomeStairs() {
        return (this.action.equals(App.STAIR_DOWNSTAIRS) || this.action.equals(App.STAIR_UPSTAIRS));
    }
}
