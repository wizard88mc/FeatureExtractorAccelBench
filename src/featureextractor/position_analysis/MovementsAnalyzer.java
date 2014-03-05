package featureextractor.position_analysis;

import featureextractor.extractor.db.DBForLocation;
import featureextractor.weka.ARFF;
import featureextractor.weka.ARFFData;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matteo Ciman
 */
public class MovementsAnalyzer {
    
    private DBForLocation db;
    private String[] dbs = {"accelbenchWhereIs20140227192800.db", "accelbenchWhereIs20140305095700.db"};
    private List<Movement> movements = new ArrayList<Movement>();
    private List<FeaturesMovement> features;
    private int frequency;
    
    public MovementsAnalyzer(int frequency) {
        
        this.frequency = frequency;
        
        for (String dbMovements: dbs) {
            db = new DBForLocation(new File("locationDBS/"+dbMovements));
            
            movements.addAll(db.getListAllMovements());
        }
    }
    
    public void analyzeMovements(Double bufferDuration) {
        
        features = new ArrayList<FeaturesMovement>();
        for (Movement movement: movements) {
            try {
                features.add(new FeaturesMovement(movement, frequency, bufferDuration));
            }
            catch(Exception exc) {
                System.out.println(exc.toString());
            }
        } 
    }
    
    public void dumpARFF(ARFF arff, boolean linear) {
        
        for (FeaturesMovement feature: features) {
            
            List<Double> data_row = new ArrayList<Double>();
            
            for (int i = 0; i < feature.featuresAccelerometer.means.size(); i++) {
                if (!linear) {
                    data_row.add(feature.featuresAccelerometer.means.get(i));
                    data_row.add(feature.featuresAccelerometer.stds.get(i));
                    data_row.add(feature.featuresAccelerometer.variances.get(i));
                    data_row.add(feature.featuresAccelerometer.differenceMinMax.get(i));
                }
                else {
                    data_row.add(feature.featuresLinear.means.get(i));
                    data_row.add(feature.featuresLinear.stds.get(i));
                    data_row.add(feature.featuresLinear.variances.get(i));
                    data_row.add(feature.featuresLinear.differenceMinMax.get(i));
                }
            }
            
            if (!linear) {
                data_row.addAll(feature.featuresAccelerometer.ratios);
                data_row.addAll(feature.featuresAccelerometer.intelligentRatios);
                data_row.addAll(feature.featuresAccelerometer.correlations);
                data_row.add(feature.featuresAccelerometer.magnitudeMean);
                data_row.add(feature.featuresAccelerometer.signalMagnitudeArea);
            }
            else {
                data_row.addAll(feature.featuresLinear.ratios);
                data_row.addAll(feature.featuresLinear.intelligentRatios);
                data_row.addAll(feature.featuresLinear.correlations);
                data_row.add(feature.featuresLinear.magnitudeMean);
                data_row.add(feature.featuresLinear.signalMagnitudeArea);
            }
          
            arff.addData(new ARFFData(feature.getEndPosition(), data_row));
            
        }
    }
    
}
