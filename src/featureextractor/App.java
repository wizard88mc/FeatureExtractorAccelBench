/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor;

import featureextractor.weka.Weka;
import java.io.File;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;

/**
 *
 * @author Nicola Beghin
 */
public class App {

    final private static String[] dbs = new String[]{"accelbench_20130822165555.db", "accelbench_20130825164535.db"}; // "accelbench_matteo.db", "accelbench_prof.db", 
    final private static String[] actions = new String[]{"NON_STAIR", "STAIR_DOWNSTAIRS", "STAIR_UPSTAIRS"};
    private enum MODE {
        CLASSIFIER, // loop through each defined db, extract and merge features, train the classifier
        TRUNK_PLOTTER // plot each trunk to enable step marking
    };
    private static MODE mode = MODE.TRUNK_PLOTTER;

    public static void main(String[] args) {
        try {
            FeatureExtractor featureExtractor = new FeatureExtractor();
            switch (mode) {
                case CLASSIFIER:
                    for (String db_path : dbs) {
                        db_path = "data" + File.separator + "db" + File.separator + db_path;
                        featureExtractor.setDb(db_path);
                        featureExtractor.setBatchSize(20);
                        featureExtractor.setArffEnabled(true);
                        featureExtractor.setFeatureEnabled(true);

                        for (String action : actions) {
                            if (action.equals(actions[0])) {
                                featureExtractor.setBatchCreationMode(FeatureExtractor.BATCH_CREATION_MODE.INTERLAPPING_SIZE_BY_STEP_AVG);
                            } else {
                                featureExtractor.setBatchCreationMode(FeatureExtractor.BATCH_CREATION_MODE.BY_STEP);
                            }
                            System.out.println("*** Parsing action " + action + " @" + db_path + " ***");
                            featureExtractor.extract(action, (action.equals(actions[0]) ? "NONSTAIR" : "STAIR"));
                        }
                    }
                    featureExtractor.dumpARFF(new File("StairDetection.arff"));
                    Weka weka = new Weka();
                    weka.setTrainingSet(featureExtractor.getARFF());
                    weka.setClassifier(new J48());
                    Classifier classifier = weka.classify();
                    weka.testClassifier(classifier);
                    break;
                case TRUNK_PLOTTER:
                    featureExtractor.setDb("data/db/accelbench_20130825164535.db");
//                  featureExtractor.setTrunkIDs();
                    featureExtractor.setArffEnabled(false); // disable ARFF creation
                    featureExtractor.setFeatureEnabled(false); // disable feature calculation
                    featureExtractor.setBatchCreationMode(FeatureExtractor.BATCH_CREATION_MODE.BY_TRUNK);
                    featureExtractor.extract();
                    featureExtractor.plot();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ECCEZIONE: " + e.getMessage());

            System.exit(-1);
        }
    }
}
