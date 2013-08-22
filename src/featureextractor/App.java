/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor;

import featureextractor.extractor.db.DbExtractor;
import featureextractor.model.TrunkFixSpec;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Nicola Beghin
 */
public class App {

    final private static String[] dbs = new String[]{"accelbench_matteo.db", "accelbench_prof.db", "accelbench_20130822165555.db"};
    final private static String[] actions = new String[]{"NON_STAIR", "STAIR_DOWNSTAIRS", "STAIR_DOWNSTAIRS"};

    public static void main(String[] args) {
        try {
            FeatureExtractor featureExtractor = new FeatureExtractor();
            for (String db_path : dbs) {
                db_path = "data" + File.separator + "db" + File.separator + db_path;
                featureExtractor.setDb(db_path);
                featureExtractor.setBatchSize(80);
                featureExtractor.setArffEnabled(true);
                featureExtractor.setFeatureEnabled(true);
                for (String action : actions) {
                    System.out.println("*** Parsing action " + action + " @" + db_path + " ***");
                    featureExtractor.extract(action, (action.equals(actions[0]) ? "NONSTAIR" : "STAIR"));
                }
            }
            featureExtractor.dumpARFF(new File("StairDetection.arff"));
            // get graph for each trunk in order to remove dirty data
//            featureExtractor.setDb("data/db/accelbench_20130822165555.db");
//            featureExtractor.setArffEnabled(false); // disable ARFF creation
//            featureExtractor.setFeatureEnabled(false); // disable feature calculation
//            featureExtractor.setBatchCreationMode(FeatureExtractor.BATCH_CREATION_MODE.BY_TRUNK);
//            featureExtractor.extract(null, "STAIR");
//            featureExtractor.plot();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ECCEZIONE: " + e.getMessage());

            System.exit(-1);
        }
    }
}
//            featureExtractor.setTrunkIDs(); // detect samples trunk (in order to fix android events' timestamp bug)
// apply fixes to each trunk (remove dirty data)
//            List<TrunkFixSpec> matteo_fixes = new ArrayList<TrunkFixSpec>();
//            matteo_fixes.add(new TrunkFixSpec(21, 16040, 16617));
//            matteo_fixes.add(new TrunkFixSpec(18, 10092545, 10098361));
//            matteo_fixes.add(new TrunkFixSpec(17, true)); // skip
//            matteo_fixes.add(new TrunkFixSpec(15, 3047800, 3048320));
//            matteo_fixes.add(new TrunkFixSpec(14, 3045800, 3046080));
//            matteo_fixes.add(new TrunkFixSpec(12, 2896450, 2896690));
//            matteo_fixes.add(new TrunkFixSpec(11, true)); // skip
//            matteo_fixes.add(new TrunkFixSpec(10, 2849266, 2849680));
//            matteo_fixes.add(new TrunkFixSpec(9, 2846783, 2847200));
//            matteo_fixes.add(new TrunkFixSpec(8, 2844530, 2845033));
//            matteo_fixes.add(new TrunkFixSpec(5, 1084960, 1085888));
//            matteo_fixes.add(new TrunkFixSpec(4, 1083193, 1084050));
//            matteo_fixes.add(new TrunkFixSpec(3, 1079432, 1080556));
//            matteo_fixes.add(new TrunkFixSpec(1, 1072527, 1072785));
//            featureExtractor.applyTrunkFixes(matteo_fixes);
//            List<TrunkFixSpec> prof_fixes = new ArrayList<TrunkFixSpec>();
//            prof_fixes.add(new TrunkFixSpec(25, 57954, 58431));
//            prof_fixes.add(new TrunkFixSpec(21, 11900, 12712));
//            prof_fixes.add(new TrunkFixSpec(20, true));
//            prof_fixes.add(new TrunkFixSpec(19, 50885, 51670));
//            prof_fixes.add(new TrunkFixSpec(18, 49480, 50350));
//            prof_fixes.add(new TrunkFixSpec(17, 48165, 48920));
//            prof_fixes.add(new TrunkFixSpec(16, 45800, 46700));
//            prof_fixes.add(new TrunkFixSpec(15, 44560, 45350));
//            prof_fixes.add(new TrunkFixSpec(14, 43702, 43950));
//            prof_fixes.add(new TrunkFixSpec(13, 43127, 43468));
//            prof_fixes.add(new TrunkFixSpec(12, true));
//            prof_fixes.add(new TrunkFixSpec(11, 40447, 41300));
//            prof_fixes.add(new TrunkFixSpec(10, 38930, 39700));
//            prof_fixes.add(new TrunkFixSpec(9, 37800, 38500));
//            prof_fixes.add(new TrunkFixSpec(8, 35780, 36600));
//            prof_fixes.add(new TrunkFixSpec(5, true));
//            prof_fixes.add(new TrunkFixSpec(4, 29700, 30323));
//            prof_fixes.add(new TrunkFixSpec(2, 429800, 437600));
    //            featureExtractor.applyTrunkFixes(prof_fixes);
