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
    private double minValue;
    private double maxValue;
    
    public static String[] VALUES = new String[]{"mean", "std", "variance", "diffMinMax"};

    public FeatureSet(double mean, double variance, double std, double minValue, 
            double maxValue) {
        this.mean = mean;
        this.variance = variance;
        this.std = std;
        this.minValue = minValue; 
        this.maxValue = maxValue;
    }

    public FeatureSet(String title, double mean, double variance, double std, 
            double min, double max) {
        this(mean, variance, std, min, max);
        this.title=title;
    }
    
    public String getTitle() {
        return title;
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
    
    public double getMin() {
        return minValue;
    }
    
    public double getMax() {
        return maxValue;
    }
    
    public double getDifferenceMinMax() {
        return maxValue - minValue;
    }
    
    @Override
    public String toString() {
        NumberFormat nf = new DecimalFormat("0.#####");
        return "\n"+title+"\tMEAN "+nf.format(this.mean)+"\tVARIANCE "+nf.format(this.variance)+"\tSTD "+nf.format(this.std);
    }
}
