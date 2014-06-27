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
    private String sex;
    private String age;
    private String height;
    private String shoes;
    private String mode; 
    private String supposedAction;
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
    
    public SlidingWindow(String sex, String age, String height, String shoes, String mode,  
            String action, List<SingleCoordinateSet> values, boolean linear, int trunk) {
        this(values); 
        this.sex = sex; this.age = age; this.height = height; this.shoes = shoes;
        this.mode = mode; this.supposedAction = action;
        this.linear = linear; this.trunk = trunk;  
    }
    
    public SlidingWindow(String sex, String age, String height, String shoes, 
            String mode, String action, List<SingleCoordinateSet> values, 
            List<SingleCoordinateSet> vectorPMitzell, List<SingleCoordinateSet> vectorHMitzell, 
            boolean linear, int trunk) {
        
        this(sex, age, height, shoes, mode, action, values, linear, trunk);
        this.valuesPMitzell = vectorPMitzell; this.valuesHMitzell = vectorHMitzell;
        
        completeSlidingWindow();
    }
    
    private void completeSlidingWindow() {
        values.add(new SingleCoordinateSet(coordinates.get(3)));
        values.add(new SingleCoordinateSet(coordinates.get(4)));
        
        for (int i = 0; i < values.get(0).size(); i++) {
            
            values.get(3).addValue(new DataTime(values.get(0).getValues().get(i).getTime(), calculateV(values, i)));
            values.get(4).addValue(new DataTime(values.get(0).getValues().get(i).getTime(),
                    (values.get(0).getValues().get(i).getValue() + 
                    values.get(1).getValues().get(i).getValue()) / 2.0));
        }
        
        if (!linear) {
            valuesPMitzell.add(new SingleCoordinateSet(coordinates.get(3)));
            valuesPMitzell.add(new SingleCoordinateSet(coordinates.get(4)));
            valuesHMitzell.add(new SingleCoordinateSet(coordinates.get(3)));
            valuesHMitzell.add(new SingleCoordinateSet(coordinates.get(4)));

            for (int i = 0; i < valuesPMitzell.get(0).size(); i++) {
                valuesPMitzell.get(3).addValue(new DataTime(valuesPMitzell.get(0).getValues().get(i).getTime(), 
                        calculateV(valuesPMitzell, i)));
                valuesHMitzell.get(3).addValue(new DataTime(valuesHMitzell.get(0).getValues().get(i).getTime(), 
                        calculateV(valuesHMitzell, i)));

                valuesPMitzell.get(4).addValue(new DataTime(valuesPMitzell.get(0).getValues().get(i).getTime(),
                        (valuesPMitzell.get(0).getValues().get(i).getValue()
                    + valuesPMitzell.get(1).getValues().get(i).getValue()) / 2.0));
                valuesHMitzell.get(4).addValue(new DataTime(valuesHMitzell.get(0).getValues().get(i).getTime(),
                        (valuesHMitzell.get(0).getValues().get(i).getValue()
                    + valuesHMitzell.get(1).getValues().get(i).getValue()) / 2.0));
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
    
    public void setMode(String mode) {
        this.mode = mode;
    }
    
    public String getSupposedAction() {
        return this.supposedAction;
    }
    
    public String getMode() {
        return this.mode;
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

    private List<Double> calculateMeansForList(List<SingleCoordinateSet> valuesToUse, int frequency) {
        List<Double> means = new ArrayList<Double>();
        
        for (int i = 0; i < valuesToUse.size(); i++) {
            means.add(valuesToUse.get(i).getMean(frequency));
        }
        return means;
    }
    
    public List<Double> getMeans(int frequency) {
        
        return calculateMeansForList(values, frequency);
    }
    
    public List<Double> getMeansPVector(int frequency) {
        
        return calculateMeansForList(valuesPMitzell, frequency);
    }
    
    public List<Double> getMeansHVector(int frequency) {
        
        return calculateMeansForList(valuesHMitzell, frequency);
    }
    
    private List<Double> calculateVariancesForList(List<SingleCoordinateSet> valuesToUse, int frequency) {
        List<Double> variances = new ArrayList<Double>();
        for (int i = 0; i < valuesToUse.size(); i++) {
            variances.add(valuesToUse.get(i).getVariance(frequency));
        }
        return variances;
    }
    
    public List<Double> getVariances(int frequency) {
        
        return calculateVariancesForList(values, frequency);
    }
    
    public List<Double> getVariancesPVector(int frequency) {

        return calculateVariancesForList(valuesPMitzell, frequency);
    }
    
    public List<Double> getVariancesHVector(int frequency) {
        
        return calculateVariancesForList(valuesHMitzell, frequency);
    }
    
    private List<Double> calculateStdsForList(List<SingleCoordinateSet> valuesToUse, int frequency) {
        List<Double> stds = new ArrayList<Double>();
        for (int i = 0; i < valuesToUse.size(); i++) {
            stds.add(valuesToUse.get(i).getStandardDeviation(frequency));
        }
        
        return stds;
    }
    
    public List<Double> getStds(int frequency) {
        
        return calculateStdsForList(values, frequency);
    }
    
    public List<Double> getStdsPVector(int frequency) {
        
        return calculateStdsForList(valuesPMitzell, frequency);
    }
    
    public List<Double> getStdsHVector(int frequency) {
        
        return calculateStdsForList(valuesHMitzell, frequency);
    }
    
    private List<Double> calculateMinsForList(List<SingleCoordinateSet> valuesToUse, int frequency) {
        List<Double> mins = new ArrayList<Double>();
        for (int i = 0; i < valuesToUse.size(); i++) {
            mins.add(valuesToUse.get(i).getMin(frequency));
        }
        
        return mins;
    }
    
    public List<Double> getMins(int frequency) {
        
        return calculateMinsForList(values, frequency);
    }
    
    public List<Double> getMinsPVector(int frequency) {
        
        return calculateMinsForList(valuesPMitzell, frequency);
    }
    
    public List<Double> getMinsHVector(int frequency) {
        
        return calculateMinsForList(valuesHMitzell, frequency);
    }
    
    private List<Double> calculateMaxesForList(List<SingleCoordinateSet> valuesToUse, int frequency) {
        List<Double> maxes = new ArrayList<Double>();
        for (int i = 0; i < valuesToUse.size(); i++) {
            maxes.add(valuesToUse.get(i).getMax(frequency));
        }
        
        return maxes;
    }
    
    public List<Double> getMaxes(int frequency) {
        
        return calculateMaxesForList(values, frequency);
    }
    
    public List<Double> getMaxesPVector(int frequency) {
        
        return calculateMaxesForList(valuesPMitzell, frequency);
    }
    
    public List<Double> getMaxsHVector(int frequency) {
        
        return calculateMaxesForList(valuesHMitzell, frequency);
    }
    
    public String getSex() {
        return this.sex;
    }
    
    public String getAge() {
        return this.age;
    }
    
    public String getHeight() {
        return this.height;
    }
    
    public String getShoes() {
        return this.shoes;
    }
}
