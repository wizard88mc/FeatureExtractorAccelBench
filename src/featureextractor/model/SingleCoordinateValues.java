/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.model;

import featureextractor.comparator.CoupleTimeDataComparator;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author ark0n3
 */
public class SingleCoordinateValues {
    private ArrayList<CoupleTimeData> values;

    public SingleCoordinateValues(ArrayList<CoupleTimeData> values) {
        super();
        this.values = values;
    }
    
    public SingleCoordinateValues() {
        super();
        this.values=new ArrayList<CoupleTimeData>();
    }
    
    public void addValue(CoupleTimeData ctd) {
        this.values.add(ctd);
    }
    
    public double getMax() {
        return Collections.max(this.values, new CoupleTimeDataComparator()).getValue();
    }

    public double getMin() {
        return Collections.min(this.values, new CoupleTimeDataComparator()).getValue();
    }
    
    
    public void normalize() {
         CoupleTimeData max=Collections.max(this.values, new CoupleTimeDataComparator());
         CoupleTimeData min=Collections.min(this.values, new CoupleTimeDataComparator());
         this.normalize(min.getValue(), max.getValue());
    }
    
    public void normalize(double min, double max) {
         for(CoupleTimeData ctd: this.values) {
             ctd.normalize(min, max);
         }
    }
    
    public void normalize(ArrayList<SingleCoordinateValues> values) {
        ArrayList<Double> maxmin=new ArrayList<Double>();
        for(int i=0; i<values.size(); i++) {
            maxmin.add(values.get(i).getMin());
            maxmin.add(values.get(i).getMax());        
        }
        for(int i=0; i<values.size(); i++) {
            values.get(i).normalize(Collections.min(maxmin).doubleValue(), Collections.max(maxmin).doubleValue());
        }
    }
    
    public double getMean() {
        double sum=0;
        for(CoupleTimeData ctd: this.values) {
            sum+=ctd.getValue();
        }
        return sum/this.values.size();
    }
    
    public double getVariance() {
        double mean=this.getMean();
        double variance=0;
        for(CoupleTimeData ctd: this.values) {
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
