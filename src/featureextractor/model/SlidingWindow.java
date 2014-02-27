/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package featureextractor.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matteo
 */
public class SlidingWindow {
    
    private List<SingleCoordinateSet> values = new ArrayList<SingleCoordinateSet>();
    private List<SingleCoordinateSet> valuesPMitzell = new ArrayList<SingleCoordinateSet>();
    private List<SingleCoordinateSet> valuesHMitzell = new ArrayList<SingleCoordinateSet>();
    private static List<String> coordinates = new ArrayList();
    private String supposedAction;
    private String placeAction;
    private int trunk = -1;
    private boolean linear = false;
    public static long lastTimestampEndDownstair = 0;
    public static long lastTimestampEndUpstair = 0;
    
    static {
        coordinates.add(0, "X");
        coordinates.add(1, "Y");
        coordinates.add(2, "Z");
        coordinates.add(3, "|V|");
        coordinates.add(4, "(X+Y)/2");
    }
    
    private double calculateV(List<SingleCoordinateSet> valuesToUse, int index) {
        
        return Math.sqrt(Math.pow(valuesToUse.get(0).getValues().get(index).getValue(), 2) + 
                Math.pow(valuesToUse.get(1).getValues().get(index).getValue(), 2) +
                Math.pow(valuesToUse.get(2).getValues().get(index).getValue(), 2));
    }
    
    public SlidingWindow(List<SingleCoordinateSet> values) {
        this.values = values;
    }
    
    public SlidingWindow(List<SingleCoordinateSet> values, boolean linear, int trunk) {
        this(values); this.linear = linear;
        this.trunk = trunk;
    }
    
    public SlidingWindow(String action, String placeAction, List<SingleCoordinateSet> values, boolean linear, int trunk) {
        this(values); this.linear = linear;
        this.trunk = trunk; this.supposedAction = action; this.placeAction = placeAction;
    }
    
    public SlidingWindow(String action, String placeAction, List<SingleCoordinateSet> values, 
            List<SingleCoordinateSet> vectorPMitzell, List<SingleCoordinateSet> vectorHMitzell, 
            boolean linear, int trunk) {
        this(action, placeAction, values, linear, trunk);
        this.valuesPMitzell = vectorPMitzell; this.valuesHMitzell = vectorHMitzell;
    }
    
    public void completeSlidingWindow() {
        values.add(new SingleCoordinateSet(coordinates.get(3)));
        values.add(new SingleCoordinateSet(coordinates.get(4)));
        
        for (int i = 0; i < values.get(0).size(); i++) {
            
            values.get(3).addValue(new DataTime(values.get(0).getValues().get(i).getTime(), calculateV(values, i), -1));
            values.get(4).addValue(new DataTime(values.get(0).getValues().get(i).getTime(),
                    (values.get(0).getValues().get(i).getValue() + 
                    values.get(1).getValues().get(i).getValue()) / 2.0, -1));
        }
        
        if (!linear) {
            valuesPMitzell.add(new SingleCoordinateSet(coordinates.get(3)));
            valuesPMitzell.add(new SingleCoordinateSet(coordinates.get(4)));
            valuesHMitzell.add(new SingleCoordinateSet(coordinates.get(3)));
            valuesHMitzell.add(new SingleCoordinateSet(coordinates.get(4)));

            for (int i = 0; i < valuesPMitzell.get(0).size(); i++) {
                valuesPMitzell.get(3).addValue(new DataTime(valuesPMitzell.get(0).getValues().get(i).getTime(), 
                        calculateV(valuesPMitzell, i), -1));
                valuesHMitzell.get(3).addValue(new DataTime(valuesHMitzell.get(0).getValues().get(i).getTime(), 
                        calculateV(valuesHMitzell, i), -1));

                valuesPMitzell.get(4).addValue(new DataTime(valuesPMitzell.get(0).getValues().get(i).getTime(),
                        (valuesPMitzell.get(0).getValues().get(i).getValue()
                    + valuesPMitzell.get(1).getValues().get(i).getValue()) / 2.0, -1));
                valuesHMitzell.get(4).addValue(new DataTime(valuesHMitzell.get(0).getValues().get(i).getTime(),
                        (valuesHMitzell.get(0).getValues().get(i).getValue()
                    + valuesHMitzell.get(1).getValues().get(i).getValue()) / 2.0, -1));
            }
        }
    }
    
    public List<SingleCoordinateSet> getValues() {
        return this.values;
    }
    
    public List<SingleCoordinateSet> getPMitzellValues() {
        return this.valuesPMitzell;
    }
    
    public List<SingleCoordinateSet> getHMitzellValues() {
        return this.valuesHMitzell;
    }
    
    public void setSupposedAction(String action) {
        this.supposedAction = action;
    }
    
    public void setPlaceAction(String place) {
        this.placeAction = place;
    }
    
    public String getSupposedAction() {
        return this.supposedAction;
    }
    
    public String getPlaceAction() {
        return this.placeAction;
    }
    
    public int getTrunk() {
        return trunk;
    }
    
    public boolean isLinear() {
        return this.linear;
    }
    
    public boolean equals(Object other) {
        
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        SlidingWindow otherWindow = (SlidingWindow)other;
        
        boolean equal = true;
        
        List<DataTime> thisX = values.get(0).getValues(),
                thisY = values.get(1).getValues(),
                thisZ = values.get(2).getValues(),
                otherX = otherWindow.values.get(0).getValues(),
                otherY = otherWindow.values.get(1).getValues(),
                otherZ = otherWindow.values.get(2).getValues();
        
        for (int i = 0; i < thisX.size() && equal; i++) {
            
            if (!(thisX.get(i).getTime() == otherX.get(i).getTime() && 
                   thisX.get(i).getValue().equals(otherX.get(i).getValue()) &&  
                   thisY.get(i).getValue().equals(otherY.get(i).getValue()) && 
                   thisZ.get(i).getValue().equals(otherZ.get(i).getValue()) && 
                   this.supposedAction.equals(otherWindow.supposedAction) && 
                   this.linear == otherWindow.linear)) {
                
                equal = false;   
            }
        }
        
        return equal;
    }

    public List<Double> getMeans(int frequency) {
        List<Double> means = new ArrayList<Double>();
        means.add(values.get(0).getMean(frequency));
        means.add(values.get(1).getMean(frequency));
        means.add(values.get(2).getMean(frequency));
        means.add(values.get(3).getMean(frequency));
        means.add(values.get(4).getMean(frequency));
        return means;
    }
    
    public List<Double> getMeansPVector(int frequency) {
        List<Double> means = new ArrayList<Double>();
        means.add(valuesPMitzell.get(0).getMean(frequency));
        means.add(valuesPMitzell.get(1).getMean(frequency));
        means.add(valuesPMitzell.get(2).getMean(frequency));
        return means;
    }
    
    public List<Double> getMeansHVector(int frequency) {
        List<Double> means = new ArrayList<Double>();
        means.add(valuesHMitzell.get(0).getMean(frequency));
        means.add(valuesHMitzell.get(1).getMean(frequency));
        means.add(valuesHMitzell.get(2).getMean(frequency));
        return means;
    }
    
    public List<Double> getVariances(int frequency) {
        List<Double> variances = new ArrayList<Double>();
        variances.add(values.get(0).getVariance(frequency));
        variances.add(values.get(1).getVariance(frequency));
        variances.add(values.get(2).getVariance(frequency));
        variances.add(values.get(3).getVariance(frequency));
        variances.add(values.get(4).getVariance(frequency));
        return variances;
    }
    
    public List<Double> getVariancesPVector(int frequency) {
        List<Double> variances = new ArrayList<Double>();
        variances.add(valuesPMitzell.get(0).getVariance(frequency));
        variances.add(valuesPMitzell.get(1).getVariance(frequency));
        variances.add(valuesPMitzell.get(2).getVariance(frequency));
        return variances;
    }
    
    public List<Double> getVariancesHVector(int frequency) {
        List<Double> variances = new ArrayList<Double>();
        variances.add(valuesHMitzell.get(0).getVariance(frequency));
        variances.add(valuesHMitzell.get(1).getVariance(frequency));
        variances.add(valuesHMitzell.get(2).getVariance(frequency));
        return variances;
    }
    
    public List<Double> getStds(int frequency) {
        List<Double> stds = new ArrayList<Double>();
        stds.add(values.get(0).getStandardDeviation(frequency));
        stds.add(values.get(1).getStandardDeviation(frequency));
        stds.add(values.get(2).getStandardDeviation(frequency));
        stds.add(values.get(3).getStandardDeviation(frequency));
        stds.add(values.get(4).getStandardDeviation(frequency));
        return stds;
    }
    
    public List<Double> getStdsPVector(int frequency) {
        List<Double> stds = new ArrayList<Double>();
        stds.add(valuesPMitzell.get(0).getStandardDeviation(frequency));
        stds.add(valuesPMitzell.get(1).getStandardDeviation(frequency));
        stds.add(valuesPMitzell.get(2).getStandardDeviation(frequency));
        return stds;
    }
    
    public List<Double> getStdsHVector(int frequency) {
        List<Double> stds = new ArrayList<Double>();
        stds.add(valuesHMitzell.get(0).getStandardDeviation(frequency));
        stds.add(valuesHMitzell.get(1).getStandardDeviation(frequency));
        stds.add(valuesHMitzell.get(2).getStandardDeviation(frequency));
        return stds;
    }
    
    public List<Double> getMins(int frequency) {
        List<Double> mins = new ArrayList<Double>();
        mins.add(values.get(0).getMin(frequency));
        mins.add(values.get(1).getMin(frequency));
        mins.add(values.get(2).getMin(frequency));
        mins.add(values.get(3).getMin(frequency));
        mins.add(values.get(4).getMin(frequency));
        return mins;
    }
    
    public List<Double> getMinsPVector(int frequency) {
        List<Double> mins = new ArrayList<Double>();
        mins.add(valuesPMitzell.get(0).getMin(frequency));
        mins.add(valuesPMitzell.get(1).getMin(frequency));
        mins.add(valuesPMitzell.get(2).getMin(frequency));
        return mins;
    }
    
    public List<Double> getMinsHVector(int frequency) {
        List<Double> mins = new ArrayList<Double>();
        mins.add(valuesHMitzell.get(0).getMin(frequency));
        mins.add(valuesHMitzell.get(1).getMin(frequency));
        mins.add(valuesHMitzell.get(2).getMin(frequency));
        return mins;
    }
    
    public List<Double> getMaxs(int frequency) {
        List<Double> maxs = new ArrayList<Double>();
        maxs.add(values.get(0).getMax(frequency));
        maxs.add(values.get(1).getMax(frequency));
        maxs.add(values.get(2).getMax(frequency));
        maxs.add(values.get(3).getMax(frequency));
        maxs.add(values.get(4).getMax(frequency));
        return maxs;
    }
    
    public List<Double> getMaxsPVector(int frequency) {
        List<Double> maxs = new ArrayList<Double>();
        maxs.add(valuesPMitzell.get(0).getMax(frequency));
        maxs.add(valuesPMitzell.get(1).getMax(frequency));
        maxs.add(valuesPMitzell.get(2).getMax(frequency));
        return maxs;
    }
    
    public List<Double> getMaxsHVector(int frequency) {
        List<Double> maxs = new ArrayList<Double>();
        maxs.add(valuesHMitzell.get(0).getMax(frequency));
        maxs.add(valuesHMitzell.get(1).getMax(frequency));
        maxs.add(valuesHMitzell.get(2).getMax(frequency));
        return maxs;
    }
}
