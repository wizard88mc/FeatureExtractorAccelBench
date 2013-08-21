/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor;

import featureextractor.utils.SamplesUtils;
import featureextractor.extractor.db.DbExtractor;
import featureextractor.model.Sample;
import featureextractor.ui.DeltaTimesGraph;
import featureextractor.ui.AxisValuesGraph;
//import featureextractor.extractor.text.FileContentExtractor;
import featureextractor.model.Batch;
import featureextractor.model.FeatureSet;
import featureextractor.weka.ARFF;
import featureextractor.weka.ARFFAttribute;
import featureextractor.weka.ARFFData;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import sun.tools.tree.ThisExpression;

/**
 *
 * @author Matteo
 */
public class FeatureExtractor extends JFrame {

    private static boolean dbMode = true;
    private ARFF arff;
    private DbExtractor db_extractor = null;
    public enum BATCH_CREATION_MODE { NON_INTERLAPPING_FIXED_SIZE, INTERLAPPING_FIXED_SIZE };
    private int batch_size=40; // default
    private BATCH_CREATION_MODE mode = BATCH_CREATION_MODE.NON_INTERLAPPING_FIXED_SIZE; // default
    
    public void setDb(String db_path) throws FileNotFoundException {
        File file = new File(db_path);
        if (file.exists() == false) {
            throw new FileNotFoundException();
        }
        db_extractor = new DbExtractor(file);
        this.initialize_ARFF();
    }

    public void setBatchSize(int batch_size) {
        this.batch_size = batch_size;
    }

    public void setBatchCreationMode(BATCH_CREATION_MODE mode) {
        this.mode=mode;
    }
    
    public void extract(String action, String className) throws Exception {
        if (db_extractor == null) {
            throw new Exception("No source DB set");
        }
        try {
            // create samples from db rows            
            ArrayList<Sample> samples = db_extractor.extract(action);

            // create samples batches by selected mode
            List<Batch> batches = null;
            switch(mode) {
                case INTERLAPPING_FIXED_SIZE:
                    batches = SamplesUtils.getInterlappingFixedSizeBatches(samples, batch_size);
                    break;
                case NON_INTERLAPPING_FIXED_SIZE:
                    batches = SamplesUtils.getNonInterlappingFixedSizeBatches(samples, batch_size);
                    break;
                default:
                    throw new Exception("Unknown batch creation mode");
            }

            // loop through batches
            int i = 1;
            arff.addClass(className);
            for (Batch batch : batches) {
                System.out.println("\n*** Batch " + i + " *** (" + batch.size() + " samples)");
                List<FeatureSet> features = batch.getFeatures();
                arff.addData(className, features.get(3)); // |V|
                batch.printFeatures();
                i++;
            }

//          System.out.println("Sampling detected: " + SamplesUtils.getSamplingRate(samples) + "Hz");
        } catch (Exception e) {
            System.err.println("ECCEZIONE: " + e.getMessage());
            System.exit(-1);
        }
    }

    public void dumpARFF(File file) throws IOException {
        System.out.println("Writing ARFF file to " + file.getAbsolutePath());
        arff.writeToFile(file);
    }

    private void initialize_ARFF() {
        // attributi ARFF
        List<ARFFAttribute> attributes = new ArrayList<ARFFAttribute>();
        attributes.add(new ARFFAttribute("mean", "REAL"));
        attributes.add(new ARFFAttribute("variance", "REAL"));
        attributes.add(new ARFFAttribute("std", "REAL"));

        // documento ARFF
        arff = new ARFF("StairDetection", attributes);
    }
}
