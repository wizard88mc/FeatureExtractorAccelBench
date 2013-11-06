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
    private double minValue = Double.MAX_VALUE;
    private double maxValue = Double.MIN_VALUE;

    public SingleCoordinateSet(List<DataTime> values) {
        super();
        this.values = values;
        this.calculateMinMax();
    }
    
    public SingleCoordinateSet() {
        super();
        this.values=new ArrayList<DataTime>();
    }
    
    private void calculateMinMax() {
        
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i).getValue() > maxValue) {
                maxValue = values.get(i).getValue();
            }
            if (values.get(i).getValue() < minValue) {
                minValue = values.get(i).getValue();
            }
        }
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
        if (ctd.getValue() > this.maxValue) {
            this.maxValue = ctd.getValue();
        }
        if (ctd.getValue() < this.minValue) {
            this.minValue = ctd.getValue();
        }
    }
    
    public double getMax() {
        return this.maxValue;
    }

    public double getMin() {
        return this.minValue;
    }
    
    public double findMax() {
        return Collections.max(this.values, new CoupleTimeDataComparator()).getValue();
    }
    
    public double findMin() {
        return Collections.min(this.values, new CoupleTimeDataComparator()).getValue();
    }
    
    
    /*public void normalize() {
         DataTime max=Collections.max(this.values, new CoupleTimeDataComparator());
         DataTime min=Collections.min(this.values, new CoupleTimeDataComparator());
         this.normalize(min.getValue(), max.getValue());
    }*/
    
    public void normalize(double min, double max) {
         for(DataTime ctd: this.values) {
             ctd.normalize(min, max);
             //ctd.normalize(DataTime.MIN_VALUE, DataTime.MAX_VALUE);
         }
    }
    
    public void normalize() {
        
        ArrayList<Double> maxmin=new ArrayList<Double>();
        for(int i=0; i<this.values.size(); i++) {
            maxmin.add(this.findMin());
            maxmin.add(this.findMax());        
        }
        /*this.maxValue = Double.MIN_VALUE;
        this.minValue = Double.MAX_VALUE;
        for(int i=0; i<values.size(); i++) {
            values.get(i).normalize(DataTime.MIN_VALUE, DataTime.MAX_VALUE);
            this.calculateMinMax();
        }*/
        
        for(int i=0; i<values.size(); i++) {
            values.get(i).normalize(Collections.min(maxmin).doubleValue(), Collections.max(maxmin).doubleValue());
        }
    }
    
    public double getMean() {
        double sum=0;
        for(DataTime ctd: this.values) {
            sum+=ctd.getValue();
        }
        return sum/this.values.size();
    }
    
    public double getVariance() {
        double mean=this.getMean();
        double variance=0;
        for(DataTime ctd: this.values) {
            variance+=Math.pow((ctd.getValue() - mean), 2);
        }
        return variance/this.values.size();
    }
    
    public double getStandardDeviation() {
        return Math.sqrt(this.getVariance());
    }
    
    public int size() {
        return this.values.size();
    }
    
}
