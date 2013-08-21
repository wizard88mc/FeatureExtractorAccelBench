/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor;

import java.io.File;

/**
 *
 * @author Nicola Beghin
 */
public class App {
    public static void main(String[] args) {
        try {
            String db_path;
            if (args.length!=1) {
                throw new Exception("Mandatory arguments required: db path");
            }
            db_path = args[0];
            FeatureExtractor featureExtractor=new FeatureExtractor();
            featureExtractor.setDb(db_path);
            featureExtractor.setBatchSize(80);
            featureExtractor.setBatchCreationMode(FeatureExtractor.BATCH_CREATION_MODE.INTERLAPPING_FIXED_SIZE);
            featureExtractor.extract("STAIR_DOWNSTAIRS", "STAIR");
            featureExtractor.extract("STAIR_UPSTAIRS", "STAIR");
            featureExtractor.extract("NON_STAIR", "NONSTAIR");
            featureExtractor.dumpARFF(new File("StairDetection.arff"));
        } catch (Exception e) {
            System.err.println("ECCEZIONE: " + e.getMessage());
            System.exit(-1);
        }
    }
}
