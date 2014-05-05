/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor;

import featureextractor.position_analysis.MovementsAnalyzer;
import featureextractor.weka.ARFF;
import featureextractor.weka.Weka;
import java.io.File;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;

/**
 *
 * @author Nicola Beghin
 */
public class App {
    
    final private static String[] dbs = {"matteo/accelbench_20140130182200.db",
                        "matteo/accelbench_20140127101346.db",
                        "matteo/accelbench_20140127113057.db",
                        "matteo/accelbench_20140127172252.db",
                        "matteo/accelbench_20140301000700.db",
                        "matteo/accelbench_20140128182904.db",
                        "matteo/accelbench_20140307151200.db",
                        "matteo/accelbench_20140312103900.db",
                        "michele/accelbench_20140127092832.db",
                        "michele/accelbench_20140128090735.db"};

    final public static String NO_STAIR = "NON_STAIR";
    final public static String STAIR_DOWNSTAIRS = "STAIR_DOWNSTAIRS";
    final public static String STAIR_UPSTAIRS = "STAIR_UPSTAIRS";
    final private static String[] actions = new String[]{NO_STAIR, STAIR_DOWNSTAIRS, STAIR_UPSTAIRS};
    final private static int[] frequencies = new int[]{10, 15, 20, 25, 30, 50, 100};
    

    private enum MODE {

        VALIDATOR, // validates classifier against a given feature set
        CLASSIFIER, // loop through each defined db, extract and merge features, train the classifier
        TRUNK_PLOTTER, // plot each trunk to enable step marking
        STEP_AVG_CALCULATOR, // plot each trunk to enable step marking,
        BUILD_DB_SLIDING_WINDOW, // Build the Database with all the sliding window
        CLEAN_DB_SLIDING_WINDOW, // Cleans SlidingWindow DB from possible copies
        POPULATE_TEST_DB, // populates the DB that contains windows as test set
        FEATURES_FROM_TEXT_DB, // features calculated from the textual DB
        MOVEMENTS_ANALYZER // to analyze movements to get accelerometer position
    };
    private static MODE mode = MODE.BUILD_DB_SLIDING_WINDOW;

    private static long getAverageStepForAllDb() throws Exception {
        FeatureExtractor featureExtractor = new FeatureExtractor(false);
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
            FeatureExtractor featureExtractor = new FeatureExtractor(false);
            String className = null;
            switch (mode) {
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
                        featureExtractor.setFeatureEnabled(true);
                        //featureExtractor.enableMinDiff(0.05f);
                        for (String action : actions) {
                            if (action.equals(actions[0])) {
                                className = "NONSTAIR";
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
                    String db2 = "data/completo/matteo/accelbench_20140312103900.db";
                    //db2 = "michele/accelbench_20140128090735.db";
                    //featureExtractor.setDb("data/completo/" + db2);
                    //System.out.println("data/completo/" + db2);
                    //                  featureExtractor.setTrunkIDs();
                    featureExtractor.setDb(db2);
                    featureExtractor.setArffEnabled(false); // disable ARFF creation
                    featureExtractor.setFeatureEnabled(false); // disable feature calculation
                    featureExtractor.setBatchCreationMode(FeatureExtractor.BATCH_CREATION_MODE.BY_TRUNK);
                    //featureExtractor.createFinalDB();
                    //featureExtractor.populateDatabase();
                    featureExtractor.extract();
                    featureExtractor.plot(true, true, false);
//                    }
                    break;
                    
                case BUILD_DB_SLIDING_WINDOW: {
                    
                    featureExtractor.createFinalDB(false, true);
                    
                    for (String db: dbs) {
                        try {
                        if (!featureExtractor.getDBTextDataManager().checkIfDatabaseAlreadyInserted(db)) {
                            featureExtractor.getDBTextDataManager().insertNewDatabase(db);
                            featureExtractor.setDb("data/completo/" + db);
                            featureExtractor.populateTextualDatabase();
                        }
                        }catch(Exception exc) {
                            exc.printStackTrace();
                        }
                    }
                    break;
                }
                
                case POPULATE_TEST_DB: {
                    
                    featureExtractor.createFinalDB(true, true);
                    for (String db: dbs) {
                        try {
                            if (!featureExtractor.getDBTextDataManager().checkIfDatabaseAlreadyInserted(db)) {
                                featureExtractor.getDBTextDataManager().insertNewDatabase(db);
                                featureExtractor.setDb("data/completo/" + db);
                                featureExtractor.populateTextualDatabase();
                            }
                        }
                        catch(Exception exc) {
                            exc.printStackTrace();
                        
                        }
                        break;
                    }
                }
                
                case FEATURES_FROM_TEXT_DB:  {
                    
                    ARFF.AddClasses(actions);
                    
                    featureExtractor.createFinalDB(false, true);
                    featureExtractor.initializeListWindowsForFeatures();
                    
                    for (int frequency: frequencies) {
                        try {
                            featureExtractor.getARFF().resetData();
                            featureExtractor.extractUsingFrequency(frequency, false, false);
                        
                            featureExtractor.dumpARFF(new File("featuresVSW/StairDetectionVSW"+frequency+".arff"));
                        }
                        catch(Exception exc) {}
                    }
                    
                    for (int frequency: frequencies) {
                        try {
                        featureExtractor.getARFF().resetData();
                        featureExtractor.extractUsingFrequency(frequency, true, false);
                        
                        featureExtractor.dumpARFF(new File("featuresVSW/StairDetectionVSW"+frequency+"Linear.arff"));
                        }
                        catch(Exception exc) {exc.printStackTrace();}
                    }
                    
                    /*featureExtractor = new FeatureExtractor(true);
                    featureExtractor.createFinalDB(true);
                    featureExtractor.initializeListWindowsForFeatures();
                    
                    for (int frequency: frequencies) {
                        
                        featureExtractor.getARFF().resetData();
                        featureExtractor.extractUsingFrequency(frequency, false, true);
                        
                        featureExtractor.dumpARFF(new File("featuresVSW/StairDetectionVSWMitzell"+frequency+".arff"));
                    }*/
                    break;
                }
                
                case MOVEMENTS_ANALYZER: {
                    
                    ARFF.AddClasses(new String[]{"TASCA", "NO_TASCA"});
                    Double[] bufferDurationForMovements = new Double[]
                        {500000000.0, 1000000000.0, 1500000000.0, 2000000000.0}; // 1/2 secondo, 1 secondo, 1secondo e 1/2, 2 secondi
                    
                    for (int frequency: frequencies) {
                        
                        MovementsAnalyzer analyzer  = new MovementsAnalyzer(frequency);

                        for (Double duration: bufferDurationForMovements) {
                            featureExtractor.getARFF().resetData();
                            
                            analyzer.analyzeMovements(duration);
                            analyzer.dumpARFF(featureExtractor.getARFF(), false);
                            
                            featureExtractor.getARFF().writeToFile(new File("featuresMovements/MovementDU"+duration / 1000000
                                + "FREQ"+frequency+".arff"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ECCEZIONE: " + e.getMessage());

            System.exit(-1);
        }
    }
}
