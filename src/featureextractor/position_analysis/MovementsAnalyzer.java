/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package featureextractor.position_analysis;

import featureextractor.extractor.db.DBForLocation;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matteo Ciman
 */
public class MovementsAnalyzer {
    
    private DBForLocation db;
    String[] dbs = {};
    List<Movement> movements = new ArrayList<Movement>();
    
    public MovementsAnalyzer() {}
    
    public void analyzeMovements() {
        
        for (String dbMovements: dbs) {
            db = new DBForLocation(new File("locationDBS/"+dbMovements));
            
            movements.addAll(db.getListAllMovements());
        }
        
    }
    
}
