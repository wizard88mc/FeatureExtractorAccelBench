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
    
    public void normalize() {
         CoupleTimeData max=Collections.max(this.values, new CoupleTimeDataComparator());
         CoupleTimeData min=Collections.min(this.values, new CoupleTimeDataComparator());
         for(CoupleTimeData ctd: this.values) {
             ctd.normalize(min.getValue(), max.getValue());
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
    
}
