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

    final private static String[] dbs = new String[]{"accelbench_20130825164535.db", "accelbench_20130825213441.db", "accelbench_20130826181943.db", "accelbench_20131107001720.db", "accelbench_20131110162117.db", "accelbench_20131113225612_NOSTAIRS.db", "accelbench_20131113231049_STAIR.db", "accelbench_20131113231246_NOSTAIRS.db", "accelbench_20131113233157_NOSTAIRS.db", "accelbench_20131115152325_SCALE.db", "accelbench_20131115152238_NONSTAIR_VELOCE.db", "accelbench_20131118205809_NOSTAIRS.db", "matteo/accelbench_20131119220216.db", "matteo/accelbench_20131119004850.db", "matteo/accelbench_20131123002545.db"}; // "accelbench_matteo.db", "accelbench_prof.db", 
    final private static String[] validation_dbs = dbs; // "accelbench_20131110161959_NONSTAIRS.db", 
    final private static String[] actions = new String[]{"NON_STAIR", "STAIR_DOWNSTAIRS", "STAIR_UPSTAIRS"};

    private enum MODE {

        VALIDATOR, // validates classifier against a given feature set
        CLASSIFIER, // loop through each defined db, extract and merge features, train the classifier
        TRUNK_PLOTTER, // plot each trunk to enable step marking
        STEP_AVG_CALCULATOR // plot each trunk to enable step marking
    };
    private static MODE mode = MODE.CLASSIFIER;

    private static long getAverageStepForAllDb() throws Exception {
        FeatureExtractor featureExtractor = new FeatureExtractor();
        float sum = 0;
        int stair_db = 0;
        long second=1000000000;
        for (String db : dbs) {
            System.out.println("data/db/" + db);
            featureExtractor.setDb("data/db/" + db);
            try {
                sum += featureExtractor.getAverageStepDuration();
                stair_db++;
            } catch (Exception ex) {
                System.out.println("No step for this db");
            }
        }
        float average_step_duration = (float) sum / (float) stair_db;
        System.out.println("AVG STEP DURATION: " + average_step_duration + ": " + 60 * average_step_duration + " ms");
        return (long)(average_step_duration*second);
    }

    public static void main(String[] args) {
        try {
            FeatureExtractor featureExtractor = new FeatureExtractor();
            String className = null;
            switch (mode) {
//                case VALIDATOR:
//                    for (String db_path : validation_dbs) {
//                        db_path = "data" + File.separator + "db" + File.separator + db_path;
//                        featureExtractor.setDb(db_path);
//                        featureExtractor.setBatchCreationMode(FeatureExtractor.BATCH_CREATION_MODE.INTERLAPPING_SIZE_BY_STEP_AVG);
//                        featureExtractor.setArffEnabled(true);
//                        featureExtractor.setGravity_remove(false);
//                        featureExtractor.setFeatureEnabled(true);
//                        for (String action : actions) {
//                            if (action.equals(actions[0])) {
//                                className = "NONSTAIR";
//                            } else {
//                                className = "STAIRS";
//                            }
//                            featureExtractor.extract(action, className);
//                        }
//                        featureExtractor.dumpARFF(new File("validation.arff"));
//                    }
//                    break;
                case CLASSIFIER:
                    long avg_step_duration=getAverageStepForAllDb();
                    for (String db_path : dbs) {
                        db_path = "data" + File.separator + "db" + File.separator + db_path;
                        featureExtractor.setDb(db_path);
                        featureExtractor.setBatchSize(20);
                        featureExtractor.setArffEnabled(true);
                        featureExtractor.setGravity_remove(false);
                        featureExtractor.setFeatureEnabled(true);
                        for (String action : actions) {
                            if (action.equals(actions[0])) {
                                className = "NONSTAIR";
                                featureExtractor.setTime_range(avg_step_duration);
                                featureExtractor.setBatchCreationMode(FeatureExtractor.BATCH_CREATION_MODE.FIXED_TIME_LAPSE);
                            } else {
                                className = "STAIRS";
                                featureExtractor.setBatchCreationMode(FeatureExtractor.BATCH_CREATION_MODE.BY_STEP);
                            }
                            System.out.println("*** Parsing action " + action + " @" + db_path + " ***");
                            featureExtractor.extract(action, className);
                        }
                    }
                    featureExtractor.dumpARFF(new File("StairDetection.arff"));
                    Weka weka = new Weka();
                    weka.setTrainingSet(featureExtractor.getARFF());
                    Classifier[] classifiers = new Classifier[]{new J48()}; // , new RandomForest()
                    for (Classifier c : classifiers) {
                        System.out.println("Classifying with " + c.getClass().getName());
                        weka.setClassifier(c);
                        Classifier classifier = weka.classify();
                        weka.testClassifier(classifier);
                        System.out.println(classifier.getRevision());
                    }
                    System.out.println("Using average step duration: "+avg_step_duration);
                    break;
                case TRUNK_PLOTTER:
//                    String[] still_dbs=new String[]{"flat/accelbench_20131121002432_NEXUS.db", "flat/accelbench_20131121002259_GALAXY.db"};
//                    for (String db : dbs) {
                    String db2 = "matteo/accelbench_20131123002545.db";
                    featureExtractor.setDb("data/db/" + db2);
                    System.out.println("data/db/" + db2);
                    //                  featureExtractor.setTrunkIDs();
                    featureExtractor.setArffEnabled(false); // disable ARFF creation
                    featureExtractor.setFeatureEnabled(false); // disable feature calculation
                    featureExtractor.setGravity_remove(false);
                    featureExtractor.setBatchCreationMode(FeatureExtractor.BATCH_CREATION_MODE.BY_TRUNK);
                    featureExtractor.extract();
                    featureExtractor.enableMinDiff((float) 0);
                    featureExtractor.plot();
//                    }
                    break;

                case STEP_AVG_CALCULATOR:
                    getAverageStepForAllDb();
//                    float sum = 0;
//                    int stair_db = 0;
//                    for (String db : dbs) {
//                        System.out.println("data/db/" + db);
//                        featureExtractor.setDb("data/db/" + db);
//                        try {
//                            sum += featureExtractor.getAverageStepDuration();
//                            stair_db++;
//                        } catch (Exception ex) {
//                            System.out.println("No step for this db");
//                        }
//                    }
//                    float average_step_duration = (float) sum / (float) stair_db;
//                    System.out.println("AVG STEP DURATION: " + average_step_duration + ": " + 60 * average_step_duration + " ms");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ECCEZIONE: " + e.getMessage());

            System.exit(-1);
        }
    }
}
