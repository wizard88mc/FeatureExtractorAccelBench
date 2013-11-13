/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor;

import featureextractor.model.Batch;
import featureextractor.weka.Weka;
import java.io.File;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;

/**
 *
 * @author Nicola Beghin
 */
public class App {
    // "accelbench_20130822165555.db", "accelbench_20130825164535.db", "accelbench_20130825213441.db", "accelbench_20130826181943.db", "accelbench_20131107001720.db", "downstairs.db", "upstairs.db", "accelbench_20131109191125_NONSTAIRS.db"
    final private static String[] dbs = new String[]{"accelbench_20130822165555.db", "accelbench_20130825164535.db", "accelbench_20130825213441.db", "accelbench_20130826181943.db", "accelbench_20131107001720.db", "accelbench_20131110162117.db"}; // "accelbench_matteo.db", "accelbench_prof.db", 
    final private static String[] validation_dbs = dbs; // "accelbench_20131109191125_NONSTAIRS.db", "accelbench_20131110161959_NONSTAIRS.db", 
    //final private static String[] dbs = new String[]{"accelbench_20130822165555.db"}; // "accelbench_matteo.db", "accelbench_prof.db", 
    //final private static String[] dbs = new String[]{"accelbench_20131029231129.db"};
    final private static String[] actions = new String[]{"NON_STAIR", "STAIR_DOWNSTAIRS", "STAIR_UPSTAIRS"};

    private enum MODE {
        VALIDATOR, // validates classifier against a given feature set
        CLASSIFIER, // loop through each defined db, extract and merge features, train the classifier
        TRUNK_PLOTTER // plot each trunk to enable step marking
    };
    private static MODE mode = MODE.VALIDATOR;

    public static void main(String[] args) {
        try {
            FeatureExtractor featureExtractor = new FeatureExtractor();
            String className = null;
            switch (mode) {
                case VALIDATOR:
                    for (String db_path : validation_dbs) {
                        db_path = "data" + File.separator + "db" + File.separator + db_path;
                        featureExtractor.setDb(db_path);
                        featureExtractor.setBatchCreationMode(FeatureExtractor.BATCH_CREATION_MODE.INTERLAPPING_SIZE_BY_STEP_AVG);
                        featureExtractor.setArffEnabled(true);
                        featureExtractor.setFeatureEnabled(true);
                        for (String action : actions) {
                            if (action.equals(actions[0])) {
                                className = "NONSTAIR";
                            } else {
                                className = "STAIRS";
                            }
                            featureExtractor.extract(action, className);
                        }
                        featureExtractor.dumpARFF(new File("validation.arff"));
                    }
                    break;
                case CLASSIFIER:
                    for (String db_path : dbs) {
                        db_path = "data" + File.separator + "db" + File.separator + db_path;
                        featureExtractor.setDb(db_path);
//                        featureExtractor.setBatchSize(20);
                        featureExtractor.setArffEnabled(true);
                        featureExtractor.setFeatureEnabled(true);

                        for (String action : actions) {
                            if (action.equals(actions[0])) {
                                className = "NONSTAIR";
                            } else {
                                //className=(action.equals(actions[1])?"DOWNSTAIRS":"UPSTAIRS");
                                // If we do not want to recognize upstairs/downstairs
                                className = "STAIRS";
                            }
                            if (action.equals(actions[0])) {
                                featureExtractor.setBatchCreationMode(FeatureExtractor.BATCH_CREATION_MODE.INTERLAPPING_SIZE_BY_STEP_AVG);
                            } else {
                                featureExtractor.setBatchCreationMode(FeatureExtractor.BATCH_CREATION_MODE.BY_STEP);
                            }
                            System.out.println("*** Parsing action " + action + " @" + db_path + " ***");
                            featureExtractor.extract(action, className);
                        }
                    }
                    featureExtractor.dumpARFF(new File("StairDetection.arff"));
                    Weka weka = new Weka();
                    weka.setTrainingSet(featureExtractor.getARFF());
                    Classifier[] classifiers = new Classifier[]{new J48(), new RandomForest()};
                    for (Classifier c : classifiers) {
                        System.out.println("Classifying with " + c.getClass().getName());
                        weka.setClassifier(c);
                        Classifier classifier = weka.classify();
                        weka.testClassifier(classifier);
                        System.out.println(classifier.getRevision());
                    }
                    break;
                case TRUNK_PLOTTER:
                    for(String db: dbs) {
//                    String db="accelbench_20131109191125_NONSTAIRS.db";
                        featureExtractor.setDb("data/db/"+db);
                        System.out.println("data/db/"+db);
    //                  featureExtractor.setTrunkIDs();
                        featureExtractor.setArffEnabled(false); // disable ARFF creation
                        featureExtractor.setFeatureEnabled(false); // disable feature calculation
                        featureExtractor.setBatchCreationMode(FeatureExtractor.BATCH_CREATION_MODE.BY_TRUNK);
                        featureExtractor.extract();
//                        featureExtractor.enableMinDiff((float)0);
                        featureExtractor.plot();
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ECCEZIONE: " + e.getMessage());

            System.exit(-1);
        }
    }
}
