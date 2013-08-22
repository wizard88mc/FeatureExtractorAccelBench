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

    public static void main(String[] args) {
        try {
            String db_path;
            if (args.length != 1) {
                throw new Exception("Mandatory arguments required: db path");
            }
            db_path = args[0];
            FeatureExtractor featureExtractor = new FeatureExtractor();
            featureExtractor.setDb(db_path);
//            featureExtractor.setTrunkIDs(); // detect samples trunk (in order to fix android events' timestamp bug)

            // apply fixes to each trunk (remove dirty data)
            List<TrunkFixSpec> fixes = new ArrayList<TrunkFixSpec>();
            fixes.add(new TrunkFixSpec(21, 16040, 16617));
            fixes.add(new TrunkFixSpec(18, 10092545, 10098361));
            fixes.add(new TrunkFixSpec(17, true)); // skip
            fixes.add(new TrunkFixSpec(15, 3047800, 3048320));
            fixes.add(new TrunkFixSpec(14, 3045800, 3046080));
            fixes.add(new TrunkFixSpec(12, 2896450, 2896690));
            fixes.add(new TrunkFixSpec(11, true)); // skip
            fixes.add(new TrunkFixSpec(10, 2849266, 2849680));
            fixes.add(new TrunkFixSpec(9, 2846783, 2847200));
            fixes.add(new TrunkFixSpec(8, 2844530, 2845033));
            fixes.add(new TrunkFixSpec(5, 1084960, 1085888));
            fixes.add(new TrunkFixSpec(4, 1083193, 1084050));
            fixes.add(new TrunkFixSpec(3, 1079432, 1080556));
            fixes.add(new TrunkFixSpec(1, 1072527, 1072785));
            featureExtractor.applyTrunkFixes(fixes);

            featureExtractor.setBatchSize(80);
            featureExtractor.setBatchCreationMode(FeatureExtractor.BATCH_CREATION_MODE.INTERLAPPING_FIXED_SIZE);
            featureExtractor.extract("STAIR_DOWNSTAIRS", "STAIR");
            featureExtractor.extract("STAIR_UPSTAIRS", "STAIR");
            featureExtractor.extract("NON_STAIR", "NONSTAIR");
            featureExtractor.dumpARFF(new File("StairDetection.arff"));

            // get graph for each trunk in order to remove dirty data
//            DbExtractor extractor = featureExtractor.getDbExtractor();
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
