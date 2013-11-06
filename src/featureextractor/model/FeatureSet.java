/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 *
 * @author Nicola Beghin
 */
public class FeatureSet {
    private String title="";
    private double mean;
    private double variance;
    private double std; 
    private double min;
    private double max;
    private double differenceMinMax;
    private double ratioMinMax;

    public FeatureSet(double mean, double variance, double std, double min, double max) {
        this.mean = mean;
        this.variance = variance;
        this.std = std;
        this.min = min; this.max = max; this.differenceMinMax = max - min;
        this.ratioMinMax = max / min;
    }

    public FeatureSet(String title, double mean, double variance, double std, double min, double max) {
        this(mean, variance, std, min, max);
        this.title=title;
    }
        
    public double getMean() {
        return mean;
    }

    public double getVariance() {
        return variance;
    }

    public double getStd() {
        return std;
    }
    
    public String getTitle() {
        return title;
    }
    
    public double getMin() {
        return min;
    }
    
    public double getMAx() {
        return max;
    }
    
    public double getDifferenceMinMax() {
        return differenceMinMax;
    }
    
    public double getRatioMinMax() {
        return ratioMinMax;
    }
    
    @Override
    public String toString() {
        NumberFormat nf = new DecimalFormat("0.#####");
        return "\n"+title+"\tMEAN "+nf.format(this.mean)+"\tVARIANCE "+nf.format(this.variance)+"\tSTD "+nf.format(this.std);
    }
    
    
}
