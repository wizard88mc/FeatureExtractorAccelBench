/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor;

import featureextractor.utils.SamplesUtils;
import featureextractor.extractor.db.DbExtractor;
import featureextractor.model.Sample;
import featureextractor.model.Batch;
import featureextractor.model.FeatureSet;
import featureextractor.model.TrunkFixSpec;
import featureextractor.plot.Plot;
import featureextractor.weka.ARFF;
import featureextractor.weka.ARFFAttribute;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matteo
 */
public class FeatureExtractor {

    private static boolean dbMode = true;
    private static final String ARFF_RELATION = "StairDetection";
    private ARFF arff;
    private DbExtractor db_extractor = null;
    private List<Batch> batches = null;
    private int range = 1000; // default
    private int start = 0; // default
    private int max = range; // default
    private boolean arff_enabled = true;
    private boolean feature_enabled = true;

    public enum BATCH_CREATION_MODE {
        ALL, // all samples
        NON_INTERLAPPING_FIXED_SIZE, // non interlapping sliding window
        INTERLAPPING_FIXED_SIZE, // interlapping sliding window
        RANGE_FROM_START, // range from beginning 
        RANGE, // range from given index
        BY_TRUNK, // group by trunk
        BEFORE_TIMESTAMP
    };
    private int batch_size = 40; // default
    private BATCH_CREATION_MODE mode = BATCH_CREATION_MODE.NON_INTERLAPPING_FIXED_SIZE; // default

    public void setDb(String db_path) throws FileNotFoundException {
        File file = new File(db_path);
        if (file.exists() == false) {
            throw new FileNotFoundException();
        }
        db_extractor = new DbExtractor(file);
        this.initialize_ARFF();
    }

    public DbExtractor getDbExtractor() {
        return this.db_extractor;
    }

    public void setFeatureEnabled(boolean feature_enabled) {
        this.feature_enabled = feature_enabled;
    }

    public void setArffEnabled(boolean arff_enabled) {
        this.arff_enabled = arff_enabled;
    }

    public void setBatchSize(int batch_size) {
        this.batch_size = batch_size;
    }

    public void setBatchCreationMode(BATCH_CREATION_MODE mode) {
        this.mode = mode;
    }

    public void setTrunkIDs() throws Exception {
        if (db_extractor == null) {
            throw new Exception("No source DB set");
        }
        db_extractor.setTrunkIDs();
    }

    public void applyTrunkFixes(List<TrunkFixSpec> fixes) throws Exception {
        if (db_extractor == null) {
            throw new Exception("No source DB set");
        }
        db_extractor.applyTrunkFixes(fixes);
    }

    public void extract(String action, String className) throws Exception {
        if (db_extractor == null) {
            throw new Exception("No source DB set");
        }
        try {
            // create samples from db rows            
            ArrayList<Sample> samples = db_extractor.extract(action);

            // create samples batches by selected mode
            batches = null;
            switch (mode) {
                case INTERLAPPING_FIXED_SIZE:
                    System.out.println("Selected interlapping sliding window with a fixed size of " + batch_size + " samples");
                    batches = SamplesUtils.getInterlappingFixedSizeBatches(samples, batch_size);
                    break;
                case NON_INTERLAPPING_FIXED_SIZE:
                    System.out.println("Selected non-interlapping sliding window with a fixed size of " + batch_size + " samples");
                    batches = SamplesUtils.getNonInterlappingFixedSizeBatches(samples, batch_size);
                    break;
                case RANGE:
                    System.out.println("Selected range " + start + " - " + max);
                    batches = SamplesUtils.getRangeBatch(samples, start, max);
                    break;
                case RANGE_FROM_START:
                    System.out.println("Selected first " + range + " samples");
                    batches = SamplesUtils.getSingleFixedSizeBatch(samples, range);
                    break;
                case BEFORE_TIMESTAMP:
                    break;
                case BY_TRUNK:
                    System.out.println("Selected batches by trunk");
                    batches = SamplesUtils.getBatchesByTrunk(samples);
                    break;
                case ALL:
                    System.out.println("Selected a single batch with all samples");
                    batches = SamplesUtils.getAll(samples);
                    break;
                default:
                    throw new Exception("Unknown batch creation mode");
            }

            // loop through batches
            int i = 1;
            arff.addClass(className);
            for (Batch batch : batches) {
                System.out.println("\n*** Batch " + i + " *** (" + batch.size() + " samples)");
                List<FeatureSet> features = null;
                if (feature_enabled) {
                    features = batch.getFeatures();
                    batch.printFeatures();
                }
                if (arff_enabled) {
                    arff.addData(className, features.get(3)); // |V|
                }
                i++;
            }

//          System.out.println("Sampling detected: " + SamplesUtils.getSamplingRate(samples) + "Hz");
        } catch (Exception e) {
            System.err.println("ECCEZIONE: " + e.getMessage());
            System.exit(-1);
        }
    }

    public void setRange(int range) {
        this.range = range;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void plot() {
        for (Batch batch : batches) {
            Plot plot = new Plot(batch);
            plot.setVisible(true);
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
        arff = new ARFF(ARFF_RELATION, attributes);
    }
}
