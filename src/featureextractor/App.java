/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor;

import featureextractor.weka.ARFF;
import java.io.File;
import java.io.IOException;

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
                        "matteo/accelbench_20140314103609.db",
                        "matteo/accelbench_20140314104200.db",
                        "matteo/accelbench_20140314112405.db",
                        "matteo/accelbench_20140314112800.db",
                        "matteo/accelbench_20150216100024.db",
                        "michele/accelbench_20140127092832.db",
                        "michele/accelbench_20140128090735.db"};

    
    /*final private static String[] dbs = {"datiCompleti/accelbench_20140606181600.db", 
        "datiCompleti/accelbench_20140610150000.db",
        "datiCompleti/accelbench_20142706190000.db"};*/
    
    final private static String[] testDBs = {
        "accelbench_prova.db"};
    
    final public static String NO_STAIR = "NON_STAIR";
    final public static String STAIR_DOWNSTAIRS = "STAIR_DOWNSTAIRS";
    final public static String STAIR_UPSTAIRS = "STAIR_UPSTAIRS";
    final public static String STAIR = "STAIR";
    final private static String[] actions = new String[]{NO_STAIR, STAIR_DOWNSTAIRS, STAIR_UPSTAIRS};
    final private static String[] actionsEasy = new String[]{NO_STAIR, STAIR};
    final private static int[] frequencies = new int[]{10, 15, 20, 25, 30, 50, 100};
    

    private enum MODE {
        TRUNK_PLOTTER, // plot each trunk to enable step marking
        BUILD_DB_SLIDING_WINDOW, // Build the Database with all the sliding window
        CLEAN_DB_SLIDING_WINDOW, // Cleans SlidingWindow DB from possible copies
        POPULATE_TEST_DB, // populates the DB that contains windows as test set
        FEATURES_FROM_TEXT_DB, // features calculated from the textual DB
    };
    private static MODE mode = MODE.TRUNK_PLOTTER;

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
            switch (mode) {
                
                case TRUNK_PLOTTER:
                    String db2 = "data/completo/datiCompleti/accelbench_test.db";
                    //db2 = "michele/accelbench_20140128090735.db";
                    //featureExtractor.setDb("data/completo/" + db2);
                    //System.out.println("data/completo/" + db2);
                    //                  featureExtractor.setTrunkIDs();
                    //db2 = "data/completo/matteo/accelbench_20140127172252.db";
                    
                    db2 = "data/completo/matteo/accelbench_step.db";
                    featureExtractor.setDb(db2);
                    featureExtractor.setArffEnabled(false); // disable ARFF creation
                    featureExtractor.setFeatureEnabled(false); // disable feature calculation
                    featureExtractor.setBatchCreationMode(FeatureExtractor.BATCH_CREATION_MODE.BY_TRUNK);
                    //featureExtractor.createFinalDB();
                    //featureExtractor.populateDatabase();
                    featureExtractor.extract();
                    featureExtractor.plot(true, true, true);
//                    }
                    break;
                    
                case BUILD_DB_SLIDING_WINDOW: {
                    
                    featureExtractor.createFinalDB(false);
                    
                    for (String db: dbs) {
                        try {
                        if (!featureExtractor.getDBTextDataManager().checkIfDatabaseAlreadyInserted(db)) {
                            featureExtractor.getDBTextDataManager().insertNewDatabase(db);
                            featureExtractor.setDb("data/completo/" + db);
                            featureExtractor.populateTextualDatabase(false);
                        }
                        }catch(Exception exc) {
                            exc.printStackTrace();
                        }
                    }
                    break;
                }
                
                case POPULATE_TEST_DB: {
                    
                    featureExtractor.createFinalDB(true);
                    for (String db: testDBs) {
                        try {
                            if (!featureExtractor.getDBTextDataManager().checkIfDatabaseAlreadyInserted(db)) {
                                featureExtractor.getDBTextDataManager().insertNewDatabase(db);
                                featureExtractor.setDb("data/completo/test/" + db);
                                featureExtractor.populateTextualDatabase(true);
                            }
                        }
                        catch(Exception exc) {
                            exc.printStackTrace();
                        
                        }
                    }
                    
                    /**
                     * Reorder all windows inserted into the test DB
                     */
                    featureExtractor.createFinalDB(true);
                    featureExtractor.sortWindowsTestDB();
                    break;
                }
                
                case FEATURES_FROM_TEXT_DB:  {
                    
                    ARFF.AddClasses(actions);
                    
                    featureExtractor.createFinalDB(false);
                    featureExtractor.initializeListWindowsForFeatures();
                    
                    for (int frequency: frequencies) {
                        try {
                            featureExtractor.getARFF().resetData();
                            featureExtractor.extractUsingFrequency(frequency, false);
                            
                            /**
                             * Accelerometer data + UP/DOWN distinction
                             */
                            featureExtractor.createARFFDataFromExtractedFeatures(false, false);
                            featureExtractor.dumpARFF(new File("featuresVSW_20140724/StairDetectionVSW"+frequency+".arff"));
                            featureExtractor.getARFF().resetData();
                            
                            /**
                             * Accelerometer data + NO distinction
                             */
                            featureExtractor.createARFFDataFromExtractedFeatures(false, true);
                            featureExtractor.dumpARFF(new File("featuresVSW_20140724/StairDetectionVSW"+frequency+"OS.arff"));
                            featureExtractor.getARFF().resetData();
                            
                            featureExtractor.extractUsingFrequency(frequency, true);
                            
                            /**
                             * Linear data + UP/DOWN distinction
                             */
                            featureExtractor.createARFFDataFromExtractedFeatures(false, false);
                            featureExtractor.dumpARFF(new File("featuresVSW_20140724/StairDetectionVSW"+frequency+"Linear.arff"));
                            featureExtractor.getARFF().resetData();
                            
                            /**
                             * Linear data + NO distinction
                             */
                            featureExtractor.createARFFDataFromExtractedFeatures(false, true);
                            featureExtractor.dumpARFF(new File("featuresVSW_20140724/StairDetectionVSW"+frequency+"OSLinear.arff"));
                        }
                        catch(IOException exc) {
                            exc.printStackTrace();
                        }
                    }
                    
                    featureExtractor = new FeatureExtractor(true);
                    featureExtractor.createFinalDB(false);
                    featureExtractor.initializeListWindowsForFeatures();
                    
                    for (int frequency: frequencies) {
                        
                        featureExtractor.getARFF().resetData();
                        /**
                         * Mizell data + UP/DOWN distinction
                         */
                        featureExtractor.extractUsingFrequency(frequency, false);
                        featureExtractor.createARFFDataFromExtractedFeatures(true, false);
                        featureExtractor.dumpARFF(new File("featuresVSW_20140724/StairDetectionVSWMizell"+frequency+".arff"));
                        featureExtractor.getARFF().resetData();
                        /**
                         * Mizell data + NO distinction
                         */
                        featureExtractor.createARFFDataFromExtractedFeatures(true, true);
                        featureExtractor.dumpARFF(new File("featuresVSW_20140724/StairDetectionVSWMizell"+frequency+"OS.arff"));
                    }
                    
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ECCEZIONE: " + e.getMessage());

            System.exit(-1);
        }
    }
}
