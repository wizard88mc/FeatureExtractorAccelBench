/*
 * Class that calculates all the features of a particular sliding window
 */

package featureextractor.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matteo
 */
public class FeaturesSlidingWindow {
    
    private List<FeatureSet> features = new ArrayList<FeatureSet>();
    private double magnitudeMean = 0.0;
    private double signalMagnitudeArea = 0.0;
    private String action;
    private List<Double> correlations = new ArrayList<Double>();
    private List<Double> ratios = new ArrayList<Double>();
    final public static String[] AXIS = new String[] {
      "X", "Y", "Z", "|V|", "X+Y"  
    };
    
    public FeaturesSlidingWindow(SlidingWindow window, int frequency) {
        
        List<Double> means = window.getMeans(frequency),
                variances = window.getVariances(frequency),
                stds = window.getStds(frequency),
                mins = window.getMins(frequency),
                maxes = window.getMaxs(frequency);
        features.add(new FeatureSet(means.get(0), variances.get(0), stds.get(0), mins.get(0), maxes.get(0))); // X
        features.add(new FeatureSet(means.get(1), variances.get(1), stds.get(1), mins.get(1), maxes.get(1))); // Y
        features.add(new FeatureSet(means.get(2), variances.get(2), stds.get(2), mins.get(2), maxes.get(2))); // Z 
        features.add(new FeatureSet(means.get(3), variances.get(3), stds.get(3), mins.get(3), maxes.get(3))); // |V|
        features.add(new FeatureSet(means.get(4), variances.get(4), stds.get(4), mins.get(4), maxes.get(4))); // (X+Y) / 2
        
        this.action = window.getSupposedAction();
        
        calculateRatios();
        calculateMagnitudeMean();
        calculateSingalMagnitudeArea(window.getValues(), frequency);
        calculateCorrelations(window, frequency);
    }
    
    /**
     * Calculates the magnitude Area of the set of features
     */
    private void calculateMagnitudeMean() {
        
        magnitudeMean = Math.sqrt(Math.pow(features.get(0).getMean(), 2) + Math.pow(features.get(1).getMean(), 2) + Math.pow(features.get(2).getMean(), 2));
    }
    
    public double getMagnitudeMean() {
        return magnitudeMean;
    }
    
    /**
     * Calculates the signal magnitude area for the given sliding window
     * 
     * @param values: Set of values to use to calculate the Signal Magnitude Area
     * @param frequency:The frequency at which calculate the correlations and use
     * data     
     */
    private void calculateSingalMagnitudeArea(List<SingleCoordinateSet> values, int frequency) {
        
        double minDelta = (double)1000000000 / frequency;
        double lastTimestamp = 0.0; int numberOfElements = 0;
        
        for (int i = 0; i < values.get(0).getValues().size(); i++) {
            if (values.get(0).getValues().get(i).getTime() - lastTimestamp > minDelta) {
                
                signalMagnitudeArea += Math.abs(values.get(0).getValues().get(i).getValue()) + 
                        Math.abs(values.get(1).getValues().get(i).getValue()) + 
                        Math.abs(values.get(2).getValues().get(i).getValue());
                
                numberOfElements++; 
                lastTimestamp = values.get(0).getValues().get(i).getTime();
            }
        }
        
        signalMagnitudeArea /= numberOfElements;
    }
    
    /**
     * Calculates the correlation between all the set of elements of the window, 
     * that are X, Y, Z, |V| and X+Y / 2
     * @param window: the sliding window with the set of values
     * @param frequency: The frequency at which calculate the correlations and use
     * data
     */
    private void calculateCorrelations(SlidingWindow window, int frequency) {
        
        for (int i = 0; i < window.getValues().size() - 1; i++) {
            for (int j = i+1; j <window.getValues().size(); j++) {
                
                Double covariance = calculateCovariance(window.getValues().get(i).getValues(), 
                        window.getValues().get(j).getValues(), frequency);
                
                Double correlation = covariance / 
                        (features.get(i).getStd() * features.get(j).getStd());
                
                if (Double.isNaN(correlation)) {
                    correlation = 0.0;
                }
                
                correlations.add(correlation);
            }
            
        }
    }
    
    /**
     * Calculates the covariance between two List of values
     * 
     * @param first: first set of values
     * @param second: second set of values
     * @param frequency: frequency at which use data and calculate covariance
     * @return The covariance between the two sets of data
     */
    private Double calculateCovariance(List<DataTime> first, List<DataTime> second, 
            int frequency) {
        
        Double covariance = 0.0, sumX = 0.0, sumY = 0.0, product = 0.0; 
        double minDelta = (double)1000000000 / frequency;
        double lastTimestamp = 0.0; int numberOfElements = 0;
      
        for (int i = 0; i < first.size(); i++) {
            if (first.get(i).getTime() - lastTimestamp >= minDelta) {
                product += (first.get(i).getValue() * second.get(i).getValue());
                sumX += first.get(i).getValue();
                sumY += second.get(i).getValue();
                
                numberOfElements++;
                lastTimestamp = first.get(i).getTime();
            }
        }
        
        covariance = (product / numberOfElements) - 
                ((sumX * sumY) / Math.pow(numberOfElements, 2));
        
        return covariance;
    }
    
    /**
     * Calculates ratios between mean, std, variance and difference min/max values
     * between all the axis
     */
    private void calculateRatios() {
        
        for (int i = 0; i < features.size() -1 ; i++) {
            for (int j = i+1; j < features.size(); j++) {
                
                Double ratioMean = features.get(i).getMean() / features.get(j).getMean(),
                        ratioStd = features.get(i).getStd() / features.get(j).getStd(),
                        ratioVariance = features.get(i).getVariance() / features.get(j).getVariance(),
                        ratioMinMax = features.get(i).getDifferenceMinMax() / features.get(j).getDifferenceMinMax();
                if (Double.isNaN(ratioMean) || Double.isInfinite(ratioMean)) {
                    ratioMean = 0.0;
                }
                if (Double.isNaN(ratioStd) || Double.isInfinite(ratioStd)) {
                    ratioStd = 0.0;
                }
                if (Double.isNaN(ratioVariance) || Double.isInfinite(ratioVariance)) {
                    ratioVariance = 0.0;
                }
                if (Double.isNaN(ratioMinMax) || Double.isInfinite(ratioMinMax)) {
                    ratioMinMax = 0.0;
                }
                ratios.add(ratioMean);
                ratios.add(ratioStd);
                ratios.add(ratioVariance);
                ratios.add(ratioMinMax);
            }
        }
    }
    
    public List<FeatureSet> getBaseFeatures() {
        return features;
    }
    
    public double getSignalMagnitudeArea() {
        return signalMagnitudeArea;
    }
    
    public String getAction() {
        return action;
    }
    
    public List<Double> getRatios() {
        return ratios;
    }
    
    public List<Double> getCorrelations() {
        return correlations;
    }
    
    public static List<String> getAllAttributesName() {
        
        List<String> attributes = new ArrayList<String>();
        
        /**
         * Base features attributes
         */
        for (int i = 0; i < AXIS.length; i++) {
            for (int j = 0; j < FeatureSet.VALUES.length; j++) {
                attributes.add(AXIS[i] + "_" + FeatureSet.VALUES[j]);
            }  
        }
        
        /**
         * Ratios attributes
         */
        for (int i = 0; i < AXIS.length; i++) {
            for (int k = i+1; k < AXIS.length; k++) {
                for (int j = 0; j < FeatureSet.VALUES.length; j++) {
                    attributes.add("RATIO:"+AXIS[i] + FeatureSet.VALUES[j] + "_" + AXIS[k] + FeatureSet.VALUES[j]);
                }  
            }
        }
        
        /**
         * Covariance attributes
         */
        for (int i = 0; i < AXIS.length; i++) {
            for (int k = i+1; k < AXIS.length; k++) {
                
                attributes.add("CORRELATION:"+AXIS[i] + "_" + AXIS[k] );  
            }
        }
        
        attributes.add("MAGNITUDE_AREA");
        attributes.add("SIGNAL_MAGNITUDE_AREA");
        
        return attributes;
    }
}
