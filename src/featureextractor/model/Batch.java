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
import featureextractor.comparator.MeanComparator;

/**
 *
 * @author Nicola Beghin
 */
public class Batch {
    
    public enum STAIR_TYPE {
        DOWNSTAIRS,
        UPSTAIRS
    };
    private List<SingleCoordinateSet> values=new ArrayList<SingleCoordinateSet>();
    private List<SingleCoordinateSet> normalizedValues = new ArrayList<SingleCoordinateSet>();
    private static final HashMap<Integer,String> coordinates_mapping=new HashMap<Integer,String>();
    private List<IntervalMarker> markers=new ArrayList<IntervalMarker>();
    private String title;
    private int trunk = 0;

    static {
        coordinates_mapping.put(0, "X");
        coordinates_mapping.put(1, "Y");
        coordinates_mapping.put(2, "Z");
        //coordinates_mapping.put(3, "|V|");
        //coordinates_mapping.put(4, "Delta");
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
        for(int i=0; i < coordinates_mapping.size(); i++) {
            values.add(new SingleCoordinateSet());
            values.get(i).setTitle(coordinates_mapping.get(i));
            normalizedValues.add(new SingleCoordinateSet());
            normalizedValues.get(i).setTitle(coordinates_mapping.get(i));
        }
        
        for (int axis = 0; axis < samples.size(); axis++) {
            Sample sample=samples.get(axis);
            values.get(0).addValue(new DataTime(sample.getTime(), sample.getValueX(), sample.getStep()));
            normalizedValues.get(0).addValue(new DataTime(sample.getTime(), sample.getValueX(), sample.getStep()));
            values.get(1).addValue(new DataTime(sample.getTime(), sample.getValueY(), sample.getStep()));
            normalizedValues.get(1).addValue(new DataTime(sample.getTime(), sample.getValueY(), sample.getStep()));
            values.get(2).addValue(new DataTime(sample.getTime(), sample.getValueZ(), sample.getStep()));
            normalizedValues.get(2).addValue(new DataTime(sample.getTime(), sample.getValueZ(), sample.getStep()));
            //values.get(3).addValue(new DataTime(sample.getTime(), sample.getValueV(), sample.getStep()));
            //values.get(4).addValue(new DataTime(sample.getTime(), sample.getValueDelta(), sample.getStep()));
        }
        
        this.calculateNormalizedValues();
    }
    
    private void calculateNormalizedValues() {
        
        for (int i = 0; i < 3 ; i++) { /* 3 = # of axis */ 
            normalizedValues.get(i).normalize();
	}
	//this.recalculateVAndDelta();
        
    }
    
    public List<FeatureSet> calculateVAndDeltaFeatures(boolean normalized) {
        
        List<SingleCoordinateSet> valuesToUse = this.values;
        if (normalized) {
            valuesToUse = this.normalizedValues;
        }
        
        List<DataTime> valuesX = valuesToUse.get(0).getValues();
        List<DataTime> valuesY = valuesToUse.get(1).getValues();
        List<DataTime> valuesZ = valuesToUse.get(2).getValues();
        
        for (int i = 0; i < valuesX.size(); i++) {
            
            valuesToUse.get(3).addValue(new DataTime(valuesX.get(0).getTime(), Sample.calculateV(valuesX.get(i).getValue(), valuesY.get(i).getValue(), valuesZ.get(i).getValue()), valuesX.get(0).getStep()));
            valuesToUse.get(4).addValue(new DataTime(valuesX.get(0).getTime(), Sample.calculateDelta(valuesToUse.get(3).getValues().get(i).getValue()), valuesX.get(0).getStep()));
        }
        
        List<FeatureSet> features = new ArrayList<FeatureSet>();
        
        features.add(new FeatureSet(coordinates_mapping.get(3), valuesToUse.get(3).getMean(), valuesToUse.get(3).getVariance(), valuesToUse.get(3).getStandardDeviation(), valuesToUse.get(3).getMin(), valuesToUse.get(3).getMax()));
        features.add(new FeatureSet(coordinates_mapping.get(4), valuesToUse.get(4).getMean(), valuesToUse.get(4).getVariance(), valuesToUse.get(4).getStandardDeviation(), valuesToUse.get(4).getMin(), valuesToUse.get(4).getMax()));
        
        return features;
    }
    
    private void recalculateVAndDelta() {
	
        for (int i = 0; i < values.get(0).getValues().size(); i++) {
			
            List<DataTime> valuesX = normalizedValues.get(0).getValues();
            List<DataTime> valuesY = normalizedValues.get(1).getValues();
            List<DataTime> valuesZ = normalizedValues.get(2).getValues();
			
            /**
            * Recalculate V and Delta values using normalized data
            */
            normalizedValues.get(3).getValues().add(new DataTime(valuesX.get(i).getTime() ,Sample.calculateV(valuesX.get(i).getValue(), valuesY.get(i).getValue(), valuesZ.get(i).getValue()), valuesX.get(i).getStep()));
            normalizedValues.get(4).getValues().add(new DataTime(valuesX.get(i).getTime(), Sample.calculateDelta(normalizedValues.get(3).getValues().get(i).getValue()), valuesX.get(i).getStep()));
        }
    }
    
    public void printFeatures() {
        List<FeatureSet> features=this.getFeatures();
        System.out.println(StringUtils.join(features, ""));
    }
    
    public List<FeatureSet> getFeatures() {
        List<FeatureSet> features=new ArrayList<FeatureSet>();
        for(int i=0; i<values.size(); i++) {
            features.add(new FeatureSet(coordinates_mapping.get(i), values.get(i).getMean(), values.get(i).getVariance(), values.get(i).getStandardDeviation(), values.get(i).getMin(), values.get(i).getMax()));
        }
        return features;
    }
    
    public List<FeatureSet> getNormalizedFeatures() {
        List<FeatureSet> features=new ArrayList<FeatureSet>();
        for(int i=0; i < normalizedValues.size(); i++) {
            features.add(new FeatureSet(coordinates_mapping.get(i), normalizedValues.get(i).getMean(), normalizedValues.get(i).getVariance(), normalizedValues.get(i).getStandardDeviation(), normalizedValues.get(i).getMin(), normalizedValues.get(i).getMax()));
        }
        return features;
    }
    
    /**
     * 
     * @param listCoordinates List<FeatureSet> is the list of base feature 
     * @return List<FeatureSet>: only X, Y and Z axes values
     */
    public List<FeatureSet> extractOnlyAxes(List<FeatureSet> listCoordinates) {
        List<FeatureSet> axes = new ArrayList<FeatureSet>();
        
        for (int i = 0; i < listCoordinates.size(); i++) {
            String titleCoordinate = listCoordinates.get(i).getTitle();
            if (titleCoordinate.equals("X") || titleCoordinate.equals("Y") || 
                    titleCoordinate.equals("Z")) {
                axes.add(listCoordinates.get(i));
            }
        }
        
        return axes;
    }
    
    /**
     * Calculates ratio between all axis of mean, standard deviation and variance
     * @param values
     * @return 
     */
    public static List<Double> calculateRatio(List<FeatureSet> values) {
        
        List<Double> ratios = new ArrayList<Double>();
        
        for (int i = 0; i < values.size()-1; i++) {
            
            for (int j = i+1; j < values.size(); j++) {
                
                //ratios.add(new Double(values.get(i).getMean() / values.get(j).getMean()));
                ratios.add(new Double(values.get(i).getStd() / values.get(j).getStd()));
                ratios.add(new Double(values.get(i).getVariance() / values.get(j).getVariance()));
            }
        }
        
        return ratios;
    }
    
    public static List<Double> calculateIntelligentRatios(List<FeatureSet> values) {
        
        List<Double> ratios = new ArrayList<Double>();
        
        //ratios.add(new Double(values.get(0).getVariance() / values.get(1).getVariance()));
        //ratios.add(new Double(values.get(0).getVariance() / values.get(2).getVariance()));
        
        ratios.add(new Double(values.get(0).getStd() / values.get(1).getStd()));
        ratios.add(new Double(values.get(0).getStd() / values.get(2).getStd()));
        
        return ratios;
    }
    
    /**
     * Calculates the ratio and the difference between the two axis with less
     * mean value
     * @param values
     * @return 
     */
    public static List<Double> calculateMeanDifference(List<FeatureSet> values) {
        
        List<Double> features = new ArrayList<Double>();
        features.add(new Double(values.get(1).getMean() / values.get(2).getMean()));
        features.add(values.get(1).getMean() - values.get(2).getMean());
        return features;
    }
    
    public static List<Double> minMaxComparisons(List<FeatureSet> values) {
        
        List<Double> minMaxComparisons = new ArrayList<Double>();
        
        for (int i = 0; i < values.size() - 1; i++) {
            for (int j = i + 1; j < values.size(); j++) {
                minMaxComparisons.add(Double.valueOf(values.get(i).getDifferenceMinMax() - values.get(j).getDifferenceMinMax()));
                minMaxComparisons.add(Double.valueOf(values.get(i).getDifferenceMinMax() / values.get(j).getDifferenceMinMax()));
                //minMaxComparisons.add(Double.valueOf(values.get(i).getRatioMinMax() - values.get(j).getRatioMinMax()));
                //minMaxComparisons.add(Double.valueOf(values.get(i).getRatioMinMax() / values.get(j).getRatioMinMax()));
            }
        }
        
        return minMaxComparisons;
    }
    
}
