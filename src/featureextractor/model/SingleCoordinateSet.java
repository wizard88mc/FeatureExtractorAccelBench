/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.model;

import featureextractor.comparator.CoupleTimeDataComparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Nicola Beghin
 */
public class SingleCoordinateSet {
    private List<DataTime> values;
    private String title;

    public SingleCoordinateSet(List<DataTime> values) {
        super();
        this.values = values;
    }
    
    public SingleCoordinateSet() {
        super();
        this.values=new ArrayList<DataTime>();
    }
    
    public SingleCoordinateSet(String title) {
        this();
        this.title = title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<DataTime> getValues() {
        return values;
    }

    public String getTitle() {
        return title;
    }
    
    public void addValue(DataTime ctd) {
        this.values.add(ctd);
    }
    
    public double getMax(int frequency) {
        double minDeltaTime = 0.0, lastTimeStamp = 0.0, maxValue = Double.MIN_VALUE;
        
        if (frequency != -1) {
            minDeltaTime = (double)1000000000 / frequency;
        }
        
        for (int i = 0; i < this.values.size(); i++) {
            if (values.get(i).getTime() - lastTimeStamp > minDeltaTime) {
                if (maxValue < values.get(i).getValue()) {
                    maxValue = values.get(i).getValue();
                }
                
                lastTimeStamp = values.get(i).getTime();
            }
        }
        return maxValue;
    }

    public double getMin(int frequency) {
        double minDeltaTime = 0.0, lastTimeStamp = 0.0, minValue = Double.MAX_VALUE;
        
        if (frequency != -1) {
            minDeltaTime = (double)1000000000 / frequency;
        }
        
        for (int i = 0; i < this.values.size(); i++) {
            if (values.get(i).getTime() - lastTimeStamp > minDeltaTime) { 
                
                if (minValue > values.get(i).getValue()) {
                    minValue = values.get(i).getValue();
                }
                
                lastTimeStamp = values.get(i).getTime();
            }
        }
        return minValue;
    }
    
    
    public void normalize() {
         DataTime max=Collections.max(this.values, new CoupleTimeDataComparator());
         DataTime min=Collections.min(this.values, new CoupleTimeDataComparator());
         this.normalize(min.getValue(), max.getValue());
    }
    
    public void normalize(double min, double max) {
         for(DataTime ctd: this.values) {
             ctd.normalize(min, max);
         }
    }
    
    public void normalize(List<SingleCoordinateSet> values) {
        ArrayList<Double> maxmin=new ArrayList<Double>();
        for(int i=0; i<values.size(); i++) {
            maxmin.add(values.get(i).getMin(-1));
            maxmin.add(values.get(i).getMax(-1));        
        }
        for(int i=0; i<values.size(); i++) {
            values.get(i).normalize(Collections.min(maxmin).doubleValue(), Collections.max(maxmin).doubleValue());
        }
    }
    
    public double getMean(int frequency) {
        
        double minDelta = 0.0;
        if (frequency != -1) {
            minDelta = (double)1000000000 / frequency;
        }
        double sum=0, lastTimestamp = 0.0;
        int elements = 0; 
        for (int i = 0; i < this.values.size(); i++) {
            
            if (this.values.get(i).getTime() - lastTimestamp >= minDelta) {
                elements++;
                sum += this.values.get(i).getValue();
                lastTimestamp = this.values.get(i).getTime();
            }
            
        }
        return sum/elements;
    }
    
    public double getVariance(int frequency) {
        double mean=this.getMean(frequency);
        double minDelta = 0.0;
        if (frequency != -1) {
            minDelta = (double)1000000000 / frequency;
        }
        double variance=0, lastTimestamp = 0;
        int elements = 0;
        
        for (int i = 0; i < this.values.size(); i++) {
            
            if (this.values.get(i).getTime() - lastTimestamp >= minDelta) {
                variance += Math.pow(this.values.get(i).getValue() - mean, 2);
                elements++;
                lastTimestamp = this.values.get(i).getTime();
            }
            
        }
        return variance/elements;
    }
    
    public double getStandardDeviation(int frequency) {
        return Math.sqrt(this.getVariance(frequency));
    }
    
    public int size() {
        return this.values.size();
    }
    
}
