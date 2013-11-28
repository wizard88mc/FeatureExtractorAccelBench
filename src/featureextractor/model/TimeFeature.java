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
public class TimeFeature {
    
    private Double magnitudeMean = 0.0;
    private List<Double> correlations = new ArrayList<Double>();
    private Double signalMagnitudeArea = 0.0;
    
    public TimeFeature(Batch batch, List<FeatureSet> features) {
        
        for (FeatureSet feature: features) {
            if (!feature.getTitle().equals("|V|")) {
                magnitudeMean += feature.getMean();
            }
        }
        magnitudeMean = Math.sqrt(magnitudeMean);
        
        correlations = this.calculateCorrelationAxis(batch, features);
        calculateSignalMagnitudeArea(batch, features);
    }
    
    
    public Double getMagnitudeMean() {
        return magnitudeMean;
    }
    
    public Double getSignalMagnitudeArea() {
        return signalMagnitudeArea;
    }
    
    public List<Double> getCorrelations() {
        return correlations;
    }
    
    private List<Double> calculateCorrelationAxis(Batch batch, List<FeatureSet> features) {
        
        for (int i = 0; i < features.size() - 1; i++) {
            for (int j = i + 1; j < features.size(); j++) {
                Double covariance = this.calculateCovariance(
                        batch.getRightAxesValues(features.get(i).getTitle()).getValues(), 
                        batch.getRightAxesValues(features.get(j).getTitle()).getValues());
                
                Double correlation = covariance / 
                        (features.get(i).getStd() * features.get(j).getStd());
                
                correlations.add(correlation);
            }
        }
        
        return correlations;
    }
    
    private Double calculateCovariance(List<DataTime> first, List<DataTime> second) {
        
        Double correlation = 0.0, sumX = 0.0, sumY = 0.0, product = 0.0; 
      
        for (int i = 0; i < first.size(); i++) {
            product += (first.get(i).getValue() * second.get(i).getValue());
            sumX += first.get(i).getValue();
            sumY += second.get(i).getValue();
        }
        
        correlation = (product / first.size()) - 
                ((sumX * sumY) / Math.pow(first.size(), 2));
        
        return correlation;
    }
    
    private void calculateSignalMagnitudeArea(Batch batch, List<FeatureSet> features) {
        
        signalMagnitudeArea = 0.0;
        
        List<SingleCoordinateSet> axisValues = new ArrayList();
        
        for (FeatureSet feature: features) {
            axisValues.add(batch.getRightAxesValues(feature.getTitle()));
        }
        
        for (int i = 0; i < axisValues.get(0).getValues().size(); i++) {
            for (SingleCoordinateSet axesValues: axisValues) {
                signalMagnitudeArea += Math.abs(axesValues.getValues().get(i).getValue());
            }
        }
        
        signalMagnitudeArea /= axisValues.get(0).getValues().size();
    }
}
