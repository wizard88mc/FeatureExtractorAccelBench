/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor;

import featureextractor.weka.Weka;
import java.io.File;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;

/**
 *
 * @author Nicola Beghin
 */
public class App {

    final private static String[] dbs = new String[]{
        "accelbench_20130825164535.db", 
        "accelbench_20130825213441.db", 
        "accelbench_20130826181943.db", 
        "accelbench_20131107001720.db", 
        "accelbench_20131110162117.db", 
        "accelbench_20131113225612_NOSTAIRS.db", 
        "accelbench_20131113231049_STAIR.db", 
        "accelbench_20131113231246_NOSTAIRS.db", 
        "accelbench_20131113233157_NOSTAIRS.db", 
        "accelbench_20131115152325_SCALE.db", 
        "accelbench_20131115152238_NONSTAIR_VELOCE.db", 
        "accelbench_20131118205809_NOSTAIRS.db", 
        "matteo/accelbench_20131119220216.db", 
        "matteo/accelbench_20131119004850.db", 
        "matteo/accelbench_20131123002545_GALAXY_NEXUS.db", 
        "matteo/accelbench_20131125090116_NEXUS4.db", 
        "matteo/accelbench_20131125204755_NEXUS4_NONSTAIR.db",
        "matteo/accelbench_20131129094849_NEXUS4_NOSTAIRS.db",
        "torre/accelbench_20131127121019_STAIR.db", 
        "torre/accelbench_20131127121623_NON_STAIR.db", 
        "nonstair/accelbench_20131127210819.db", 
        "nonstair/accelbench_20131127210009.db",
        "nonstair/accelbench_20131127210108.db",
        "nonstair/accelbench_20131127210355.db",
        "nonstair/accelbench_20131127210819.db",
         // from now on duplicated samples
        "accelbench_20131115152238_NONSTAIR_VELOCE.db", 
        "accelbench_20131118205809_NOSTAIRS.db", 
        "accelbench_20131113231246_NOSTAIRS.db", 
        "accelbench_20131113233157_NOSTAIRS.db", 
        "nonstair/accelbench_20131127210819.db", 
        "nonstair/accelbench_20131127210009.db",
        "nonstair/accelbench_20131127210108.db",
        "nonstair/accelbench_20131127210355.db",
        "accelbench_20131115152238_NONSTAIR_VELOCE.db", 
        "accelbench_20131118205809_NOSTAIRS.db", 
        "accelbench_20131113231246_NOSTAIRS.db", 
        "accelbench_20131113233157_NOSTAIRS.db", 
        "nonstair/accelbench_20131127210819.db", 
        "nonstair/accelbench_20131127210009.db",
        "nonstair/accelbench_20131127210108.db",
        "nonstair/accelbench_20131127210355.db",
        "accelbench_20131115152238_NONSTAIR_VELOCE.db", 
        "accelbench_20131118205809_NOSTAIRS.db", 
        "accelbench_20131113231246_NOSTAIRS.db", 
        "accelbench_20131113233157_NOSTAIRS.db", 
        "nonstair/accelbench_20131127210819.db", 
        "nonstair/accelbench_20131127210009.db",
        "nonstair/accelbench_20131127210108.db",
        "nonstair/accelbench_20131127210355.db",
        "accelbench_20131115152238_NONSTAIR_VELOCE.db", 
        "accelbench_20131118205809_NOSTAIRS.db", 
        "accelbench_20131113231246_NOSTAIRS.db", 
        "accelbench_20131113233157_NOSTAIRS.db", 
        "nonstair/accelbench_20131127210819.db", 
        "nonstair/accelbench_20131127210009.db",
        "nonstair/accelbench_20131127210108.db",
        "nonstair/accelbench_20131127210355.db",
 
        

    }; // "accelbench_matteo.db", "accelbench_prof.db", 
    final private static String[] validation_dbs = dbs; // "accelbench_20131110161959_NONSTAIRS.db", 
    final private static String[] actions = new String[]{"NON_STAIR", "STAIR_DOWNSTAIRS", "STAIR_UPSTAIRS"};

    private enum MODE {

        VALIDATOR, // validates classifier against a given feature set
        CLASSIFIER, // loop through each defined db, extract and merge features, train the classifier
        TRUNK_PLOTTER, // plot each trunk to enable step marking
        STEP_AVG_CALCULATOR // plot each trunk to enable step marking
    };
    private static MODE mode = MODE.TRUNK_PLOTTER;

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
                    int total_samples_count=0,total_stair_samples_count=0,total_nonstair_samples_count=0;
                    int samples_count_per_db,stair_samples_count_per_db=0,nonstair_samples_count_per_db=0;
                    for (String db_path : dbs) {
                        db_path = "data" + File.separator + "db" + File.separator + db_path;
                        featureExtractor.setDb(db_path);
                        samples_count_per_db=featureExtractor.getSamplesCount();
                        stair_samples_count_per_db=featureExtractor.getStairSamplesCount();
                        nonstair_samples_count_per_db=featureExtractor.getNonstairSamplesCount();
                        total_samples_count+=samples_count_per_db;
                        total_stair_samples_count+=stair_samples_count_per_db;
                        total_nonstair_samples_count+=nonstair_samples_count_per_db;
                        System.out.println(db_path+": "+samples_count_per_db+" samples ("+stair_samples_count_per_db+" STAIR, "+nonstair_samples_count_per_db+" nonstair)");
                        featureExtractor.setBatchSize(20);
                        featureExtractor.setArffEnabled(true);
                        featureExtractor.setGravity_remove(false);
                        featureExtractor.setFeatureEnabled(true);
                        //featureExtractor.enableMinDiff(0.05f);
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
                    float percentage_stair=(float)total_stair_samples_count/(float)total_samples_count*100;
                    System.out.println("Using average step duration: "+avg_step_duration);
                    System.out.println("Using "+total_samples_count+" samples in total ("+total_stair_samples_count+" stair, "+total_nonstair_samples_count+" nonstairs - "+percentage_stair+"%)");
                    
                    break;
                case TRUNK_PLOTTER:
//                    String[] still_dbs=new String[]{"flat/accelbench_20131121002432_NEXUS.db", "flat/accelbench_20131121002259_GALAXY.db"};
//                    for (String db : dbs) {
                    String db2 = "matteo/accelbench.db";
                    db2 = "michele/accelbench_20140128090735.db";
                    featureExtractor.setDb("data/completo/" + db2);
                    System.out.println("data/completo/" + db2);
                    //                  featureExtractor.setTrunkIDs();
                    featureExtractor.setArffEnabled(false); // disable ARFF creation
                    featureExtractor.setFeatureEnabled(false); // disable feature calculation
                    featureExtractor.setGravity_remove(true);
                    featureExtractor.setLinearOrNot(false);
                    featureExtractor.setBatchCreationMode(FeatureExtractor.BATCH_CREATION_MODE.BY_TRUNK);
                    featureExtractor.extract();
//                  featureExtractor.enableMinDiff((float) 0);
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
