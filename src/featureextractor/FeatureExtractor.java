/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor;

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
    private DbExtractor dbExtractor = null;

    public void setDb(String db_path) throws FileNotFoundException {
        File file = new File(db_path);
        if (file.exists() == false) {
            throw new FileNotFoundException();
        }
        dbExtractor = new DbExtractor(file);
        this.initialize_ARFF();
    }

    public void extract(String action, String className) throws Exception {
        if (dbExtractor == null) {
            throw new Exception("No source DB set");
        }
        try {
            // create samples from db rows            
            ArrayList<Sample> samples = dbExtractor.extract(action);

            // create samples batches
            List<Batch> batches = SamplesUtils.getBatchesBySamplesNum(samples, 25);

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

//            System.out.println("Sampling detected: " + SamplesUtils.getSamplingRate(samples) + "Hz");
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
        attributes.add(new ARFFAttribute("mean", "NUMERIC"));
        attributes.add(new ARFFAttribute("variance", "NUMERIC"));
        attributes.add(new ARFFAttribute("std", "NUMERIC"));

        // classi ARFF
//        List<String> classes = new ArrayList<String>();
//        classes.add("STAIR");
//        classes.add("NONSTAIR");

        // documento ARFF
        arff = new ARFF("StairDetection", attributes);
    }
}
