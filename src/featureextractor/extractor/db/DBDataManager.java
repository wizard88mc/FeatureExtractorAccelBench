
package featureextractor.extractor.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author matteo
 */
public class DBDataManager {

    private File db;
    private Connection connection = null;
    
    public DBDataManager(double millisecondsSlidingWindow) throws IOException {
        
        String finalFile = "data/dbWindows" 
                + Integer.toString((int)millisecondsSlidingWindow)
                + ".db";
        db = new File(finalFile);
        db.createNewFile();
        try {
            connect();
        }
        catch(Exception exc) {
            System.out.println(exc);
        }
    }
    
    private void connect() throws FileNotFoundException, ClassNotFoundException, SQLException {
        if (connection == null) {
            if (db.exists() == false) {
                throw new FileNotFoundException("No db found at " + db.getAbsolutePath());
            }
            Class.forName("org.sqlite.JDBC"); // ClassNotFoundException
            connection = DriverManager.getConnection("jdbc:sqlite:" + db.getAbsolutePath());
        }
    }
    
    
}
