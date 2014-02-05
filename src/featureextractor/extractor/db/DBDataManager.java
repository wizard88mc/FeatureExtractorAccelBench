
package featureextractor.extractor.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
            initializeDB();
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
    
    private void initializeDB() throws FileNotFoundException, ClassNotFoundException, SQLException {
        
        connect();
        
        Statement stmt = connection.createStatement();
        //String createDB = "CREATE DATABASE IF NOT EXISTS dbFinalSamples";
        String createTable = "CREATE TABLE IF NOT EXISTS samples " + 
                "(ID INT AUTO INCREMENT PRIMARY KEY,"
                + "timestampt DOUBLE NOT NULL,"
                + "x DOUBLE NOT NULL, y DOUBLE NOT NULL, z DOUBLE NOT NULL,"
                + "rotationX DOUBLE NOT NULL, rotationY DOUBLE NOT NULL, rotationZ DOUBLE NOT NULL,"
                + "label STRING NOT NULL, delay INT NOT NULL, trunk INT, mode STRING, LINEAR BOOLEAN DEFAULT 0)";
        
        //stmt.executeUpdate(createDB);
        stmt.executeUpdate(createTable);
    }
    
    
}
