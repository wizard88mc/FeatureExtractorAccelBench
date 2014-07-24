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
    private List<FeatureSet> featuresPMitzell = new ArrayList<FeatureSet>();
    private List<FeatureSet> featuresHMitzell = new ArrayList<FeatureSet>();
    private double magnitudeMean = 0.0;
    private double magnitudeMeanPMitzell = 0.0;
    private double magnitudeMeanHMitzell = 0.0;
    private double signalMagnitudeArea = 0.0;
    private double signalMagnitudeAreaPMitzell = 0.0;
    private double signalMagnitudeAreaHMitzell = 0.0;
    private String action;
    private List<Double> correlations = new ArrayList<Double>();
    private List<Double> correlationsPMitzell = new ArrayList<Double>();
    private List<Double> correlationsHMitzell = new ArrayList<Double>();
    private List<Double> ratios = new ArrayList<Double>();
    private List<Double> ratiosPMitzell = new ArrayList<Double>();
    private List<Double> ratiosHMitzell = new ArrayList<Double>();
    private List<Double> intelligentRatios = new ArrayList<Double>();
    private List<Double> intelligentRatiosPMitzell = new ArrayList<Double>();
    private List<Double> intelligentRatiosHMitzell = new ArrayList<Double>();
    final public static String[] AXIS = new String[] {
      "X", "Y", "Z", "|V|", "(X+Y)/2"  
    };
    
    public FeaturesSlidingWindow(SlidingWindow window, int frequency) {
        
        this.action = window.getSupposedAction();
        
        List<Double> means = window.getMeans(frequency),
                variances = window.getVariances(frequency),
                stds = window.getStds(frequency),
                mins = window.getMins(frequency),
                maxes = window.getMaxes(frequency);
        
        for (int i = 0; i < means.size(); i++) {
            features.add(new FeatureSet(means.get(i), variances.get(i), stds.get(i), mins.get(i), maxes.get(i)));
        }
        
        calculateIntelligentRatiosMinsMaxes(mins, maxes, intelligentRatios);
        
        if (!window.isLinear()) {
            means = window.getMeansPVector(frequency);
            variances = window.getVariancesPVector(frequency);
            stds = window.getStdsPVector(frequency);
            mins = window.getMinsPVector(frequency);
            maxes = window.getMaxesPVector(frequency);

            for (int i = 0; i < means.size(); i++) {
                featuresPMitzell.add(new FeatureSet(means.get(i), variances.get(i), stds.get(i), mins.get(i), maxes.get(i)));
            }

            calculateIntelligentRatiosMinsMaxes(mins, maxes, intelligentRatiosPMitzell);

            means = window.getMeansHVector(frequency);
            variances = window.getVariancesHVector(frequency);
            stds = window.getStdsHVector(frequency);
            mins = window.getMinsHVector(frequency);
            maxes = window.getMaxsHVector(frequency);

            for (int i = 0; i < means.size(); i++) {
                featuresHMitzell.add(new FeatureSet(means.get(i), variances.get(i), stds.get(i), mins.get(i), maxes.get(i)));
            }

            calculateIntelligentRatiosMinsMaxes(mins, maxes, intelligentRatiosHMitzell);
        }
        
        calculateRatios(features, ratios); ratios.addAll(intelligentRatios);
        
        if (!window.isLinear()) {
            calculateRatios(featuresPMitzell, ratiosPMitzell); ratiosPMitzell.addAll(intelligentRatiosPMitzell);
            calculateRatios(featuresHMitzell, ratiosHMitzell); ratiosHMitzell.addAll(intelligentRatiosHMitzell);
        }
        
        magnitudeMean = calculateMagnitudeMean(features);
        
        if (!window.isLinear()) {
            magnitudeMeanPMitzell = calculateMagnitudeMean(featuresPMitzell);
            magnitudeMeanHMitzell = calculateMagnitudeMean(featuresHMitzell);
        }
        
        signalMagnitudeArea = calculateSingalMagnitudeArea(window.getValues(), frequency);
        if (!window.isLinear()) {
            signalMagnitudeAreaPMitzell = calculateSingalMagnitudeArea(window.getPMitzellValues(), frequency);
            signalMagnitudeAreaHMitzell = calculateSingalMagnitudeArea(window.getHMitzellValues(), frequency);
        }
        
        if (!window.isLinear()) {
            calculateCorrelations(window.getValues(), frequency, correlations);
            calculateCorrelations(window.getPMitzellValues(), frequency, correlationsPMitzell);
            calculateCorrelations(window.getHMitzellValues(), frequency, correlationsHMitzell);
        }
    }
    
    /**
     * Calculates the magnitude Area of the set of features
     */
    private double calculateMagnitudeMean(List<FeatureSet> featuresToUse) {
        
        return Math.sqrt(Math.pow(featuresToUse.get(0).getMean(), 2) +
                Math.pow(featuresToUse.get(1).getMean(), 2) + 
                Math.pow(featuresToUse.get(2).getMean(), 2));
    }
    
    public double getMagnitudeMean() {
        return magnitudeMean;
    }
    
    public double getMagnitudeMeanPMitzell() {
        return magnitudeMeanPMitzell;
    }
    
    public double getMagnitudeMeanHMitzell() {
        return magnitudeMeanHMitzell;
    }
    
    /**
     * Calculates the signal magnitude area for the given sliding window
     * 
     * @param values: Set of values to use to calculate the Signal Magnitude Area
     * @param frequency:The frequency at which calculate the correlations and use
     * data     
     */
    private double calculateSingalMagnitudeArea(List<SingleCoordinateSet> valuesToUse, 
            int frequency) {
        
        double minDelta = (double)1000000000 / frequency;
        double lastTimestamp = 0.0; int numberOfElements = 0;
        double signalMagArea = 0.0;
        
        for (int i = 0; i < valuesToUse.get(0).getValues().size(); i++) {
            if (valuesToUse.get(0).getValues().get(i).getTime() - lastTimestamp > minDelta) {
                
                signalMagArea += Math.abs(valuesToUse.get(0).getValues().get(i).getValue()) + 
                        Math.abs(valuesToUse.get(1).getValues().get(i).getValue()) + 
                        Math.abs(valuesToUse.get(2).getValues().get(i).getValue());
                
                numberOfElements++; 
                lastTimestamp = valuesToUse.get(0).getValues().get(i).getTime();
            }
        }
        
        return signalMagArea / (double)numberOfElements;
    }
    
    /**
     * Calculates the correlation between all the set of elements of the window, 
     * that are X, Y, Z, |V| and X+Y / 2
     * @param window: the sliding window with the set of values
     * @param frequency: The frequency at which calculate the correlations and use
     * data
     */
    private void calculateCorrelations(List<SingleCoordinateSet> values, int frequency,
        List<Double> correlationsResult) {
        
        for (int i = 0; i < values.size() - 1; i++) {
            for (int j = i+1; j < values.size(); j++) {
                
                try {
                Double covariance = calculateCovariance(values.get(i).getValues(), 
                        values.get(j).getValues(), frequency);
                
                Double correlation = covariance / 
                        (features.get(i).getStd() * features.get(j).getStd());
                
                if (Double.isNaN(correlation)) {
                    correlation = 0.0;
                }
                
                correlationsResult.add(correlation);
                }
                catch(Exception exc) {
                    exc.printStackTrace();
                }
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
    private void calculateRatios(List<FeatureSet> featuresToUse, List<Double> ratiosWhereAdd) {
        
        for (int i = 0; i < featuresToUse.size() -1 ; i++) {
            for (int j = i+1; j < featuresToUse.size(); j++) {
                
                Double ratioMean = featuresToUse.get(i).getMean() / featuresToUse.get(j).getMean(),
                        ratioStd = featuresToUse.get(i).getStd() / featuresToUse.get(j).getStd(),
                        ratioVariance = featuresToUse.get(i).getVariance() / featuresToUse.get(j).getVariance(),
                        ratioMinMax = featuresToUse.get(i).getDifferenceMinMax() / featuresToUse.get(j).getDifferenceMinMax();
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
                ratiosWhereAdd.add(ratioMean);
                ratiosWhereAdd.add(ratioStd);
                ratiosWhereAdd.add(ratioVariance);
                ratiosWhereAdd.add(ratioMinMax);
            }
        }
        
        // std(X) ^ 2 / std(V)
        ratios.add(Math.pow(features.get(0).getStd(), 2) / featuresToUse.get(3).getStd());
        
        // std(Y) ^ 2 / std(V)
        ratios.add(Math.pow(features.get(1).getStd(), 2) / featuresToUse.get(3).getStd());
        
        // std(Z) ^ 2 / std(V)
        ratios.add(Math.pow(features.get(2).getStd(), 2) / featuresToUse.get(3).getStd());
        
        //std(X) + std(Y) / std(V)
        ratios.add(Math.pow(features.get(0).getStd() + features.get(1).getStd(), 2) 
                / features.get(2).getStd());
        
        //std(X) + std(Y) / std(V)
        ratios.add(Math.pow(features.get(0).getStd() + features.get(1).getStd(), 2) 
                / featuresToUse.get(3).getStd());
    
    }
    
    public List<FeatureSet> getBaseFeatures() {
        return features;
    }
    
    public List<FeatureSet> getBaseFeaturesPMitzell() {
        return featuresPMitzell;
    }
    
    public List<FeatureSet> getBaseFeaturesHMitzell() {
        return featuresHMitzell;
    }
    
    public double getSignalMagnitudeArea() {
        return signalMagnitudeArea;
    }
    
    public double getSignalMagnitudeAreaPMitzell() {
        return signalMagnitudeAreaPMitzell;
    }
    
    public double getSignalMagnitudeAreaHMitzell() {
        return signalMagnitudeAreaHMitzell;
    }
    
    public String getAction() {
        return action;
    }
    
    public List<Double> getRatios() {
        return ratios;
    }
    
    public List<Double> getRatiosPMitzell() {
        return ratiosPMitzell;
    }
    
    public List<Double> getRatiosHMitzell() {
        return ratiosHMitzell;
    }
    
    public List<Double> getCorrelations() {
        return correlations;
    }
    
    public List<Double> getCorrelationsPMitzell() {
        return correlationsPMitzell;
    }
    
    public List<Double> getCorrelationsHMitzell() {
        return correlationsPMitzell;
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
        
        attributes.add("RATIO:MAX(Z)_MAX(X+Y/2)");
        attributes.add("RATIO:|MIN(Z)_MIN(X+Y/2)|");
        
        /**
         * Intelligent ratios section
         */
        attributes.add("STD_X_2__STD_V");
        attributes.add("STD_Y_2__STD_V");
        attributes.add("STD_Z_2__STD_V");
        attributes.add("STD_XY_2__STD_Z");
        attributes.add("STD_XY_2__STD_V");
        
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
    
    public static List<String> getAllAttributesNameMitzell() {
        
        List<String> attributes = FeaturesSlidingWindow.getAllAttributesName();
        List<String> attributesH = FeaturesSlidingWindow.getAllAttributesName();
        
        for(int i = 0; i < attributes.size(); i++) {
            attributes.set(i, "P".concat(attributes.get(i)));
            attributesH.set(i, "H".concat(attributesH.get(i)));
        }
        
        attributes.addAll(attributesH);
        return attributes;
    }
    
    private void calculateIntelligentRatiosMinsMaxes(List<Double> mins, List<Double> maxes, List<Double> list) {
        
        Double ratioMaxes = maxes.get(2) / maxes.get(4),
                ratioMins = Math.abs(mins.get(2) / mins.get(4));
        if (Double.isInfinite(ratioMaxes) || Double.isNaN(ratioMaxes)) {
            ratioMaxes = 0.0;
        }
        if (Double.isInfinite(ratioMins) || Double.isNaN(ratioMins)) {
            ratioMins = 0.0;
        }
        list.add(ratioMaxes);
        list.add(ratioMins);
    }
}
