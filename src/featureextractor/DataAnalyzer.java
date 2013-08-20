package featureextractor;

import featureextractor.model.Sample;
import featureextractor.model.DataTime;
import java.util.ArrayList;
/**
 *
 * @author Matteo
 */
public class DataAnalyzer {
    
    private static double minValue = Double.MAX_VALUE;
    private static double maxValue = Double.MIN_VALUE;
    private ArrayList<ArrayList<DataTime>> dataSets = new ArrayList<ArrayList<DataTime>>();
    public ArrayList<ArrayList<DataTime>> startingData = new ArrayList<ArrayList<DataTime>>();
    private ArrayList<Double> meanValues = new ArrayList<Double>();
    private ArrayList<Double> standardDeviationValues = new ArrayList<Double>();
    private ArrayList<Double> varianceValues = new ArrayList<Double>();
    private Double meanDeltaTimes = new Double(0);
    
    public DataAnalyzer(ArrayList<Sample> values) {
        
        this.dataSets.add(0, new ArrayList<DataTime>());
        this.dataSets.add(1, new ArrayList<DataTime>());
        this.dataSets.add(2, new ArrayList<DataTime>());
        this.startingData.add(0, new ArrayList<DataTime>());
        this.startingData.add(1, new ArrayList<DataTime>());
        this.startingData.add(2, new ArrayList<DataTime>());
        
        for (int i = 0; i < values.size(); i++) {
                
            this.dataSets.get(0).add(new DataTime(values.get(i).time, values.get(i).valueX));
            this.dataSets.get(1).add(new DataTime(values.get(i).time, values.get(i).valueY));
            this.dataSets.get(2).add(new DataTime(values.get(i).time, values.get(i).valueZ));
            this.startingData.get(0).add(new DataTime(values.get(i).time, values.get(i).valueX));
            this.startingData.get(1).add(new DataTime(values.get(i).time, values.get(i).valueY));
            this.startingData.get(2).add(new DataTime(values.get(i).time, values.get(i).valueZ));
        }
    }
    
    private void searchMaxMin(ArrayList<DataTime> data) {
        
        for (int i = 0; i < data.size(); i++) {
            
            if (data.get(i).value < minValue) {
                minValue = data.get(i).value;
            }
            if (data.get(i).value > maxValue) {
                maxValue = data.get(i).value;
            }
        }
    }
    
    /**
     * Searches for the max and the min value in both three list of values
     */
    public void searchForMaxOrMin() {
        
        for (int i = 0; i < this.dataSets.size(); i++) {
            this.searchMaxMin(this.dataSets.get(i));
        }
        System.out.println("Max: " + maxValue);
        System.out.println("Min: " + minValue);
    }
    
    private void normalizeListValue(ArrayList<DataTime> singleData) {
        
        for (int i = 0; i < singleData.size(); i++) {    
            singleData.get(i).value = (singleData.get(i).value - minValue) / 
                    (maxValue - minValue);
        }
    }
    
    public void normalize() {        
        for (int i = 0; i < this.dataSets.size(); i++) {
            this.normalizeListValue(this.dataSets.get(i));
        }
    }
    
    private double meanForListValue(ArrayList<DataTime> list) {
        
        double mean = 0;
        for (int i = 0; i < list.size(); i++) {
            mean += (list.get(i)).value;
        }
        
        mean /= list.size();
        return mean;
    }
    
    /**
     * Calcola la media 
     */
    private void calculateMean() {
        
        for (int i = 0; i < dataSets.size(); i++) {
            meanValues.add(i, meanForListValue(dataSets.get(i)));
        }
    }
    
    /**
     * Calcola varianza e deviazione standard
     */
    private double calculateVariance(ArrayList<DataTime> data, double mean) {
        
        double variance = 0;
        for (int i = 0; i < data.size(); i++) {
            
            variance += Math.pow((data.get(i).value - mean), 2);
        }
        variance /= data.size();
        return variance;
    }
    
    private void calculateStandardDeviation() {
        
        for (int i = 0; i < dataSets.size(); i++) {
            varianceValues.add(i, 
                    this.calculateVariance(dataSets.get(i), meanValues.get(i)));
            
            standardDeviationValues.add(i, Math.sqrt(varianceValues.get(i)));
        }
    }
    
    public void calculateFeatures() {
        this.calculateMean(); this.calculateStandardDeviation();
    }
    
    public void evaluateDeltaTimes() {
     
        this.meanDeltaTimes = this.dataSets.get(0).get(0).time;
        for (int i = 1; i < dataSets.get(0).size(); i++) {
            this.meanDeltaTimes += (this.dataSets.get(0).get(i).time - 
                    this.dataSets.get(0).get(i-1).time); 
        }
        
        this.meanDeltaTimes /= this.dataSets.get(0).size();
    }
    
    @Override
    public String toString() {
        
        String finale = "";
        finale = finale.concat("* * * Asse X * * *").concat("\n").concat("Media: ")
                .concat(this.meanValues.get(0).toString())
                .concat(" - Varianza: ").concat(this.varianceValues.get(0).toString())
                .concat(" - Deviazione Standard: ").concat(this.standardDeviationValues.get(0).toString())
                .concat("\n")
                .concat("* * * Asse Y * * *").concat("\n").concat("Media: ")
                .concat(this.meanValues.get(1).toString())
                .concat(" - Varianza: ").concat(this.varianceValues.get(1).toString())
                .concat(" - Deviazione Standard: ").concat(this.standardDeviationValues.get(1).toString())
                .concat("\n")
                .concat("* * * Asse Z * * *").concat("\n").concat("Media: ")
                .concat(this.meanValues.get(2).toString())
                .concat(" - Varianza: ").concat(this.varianceValues.get(2).toString())
                .concat(" - Deviazione Standard: ").concat(this.standardDeviationValues.get(2).toString())
                .concat("\n")
                .concat("* * * * * ").concat("\n")
                .concat("Media Delta Tempo: ").concat(this.meanDeltaTimes.toString());
        return finale;
    }
}
