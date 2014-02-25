package featureextractor.extractor.db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 *
 * @author Matteo
 */
public class DBTextManager {
    
    private PrintWriter outputDatabase;
    private PrintWriter outputSamples;
    
    public DBTextManager() throws IOException {

        outputDatabase = new PrintWriter(new BufferedWriter(new FileWriter("databasesInserted.txt", true)));
        outputSamples = new PrintWriter(new BufferedWriter(new FileWriter("samplesSlidingWindows.dsw", true)));
    }
    
    /**
     * Checks if the database has been already analyzed and all the data inserted
     * @param databaseName: the name of the database to check
     * @return if the database already inserted
     */
    public boolean checkIfDatabaseAlreadyInserted(String databaseName) {
        
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("databasesInserted.txt")));
            String line = reader.readLine();
            
            String[] databases = line.split(",");
            
            boolean found = false;
            for (int i = 0; i < databases.length && !found; i++) {
                if (databases[i].equals(databaseName)) {
                    found = true;
                }
            }
            
            return found;
        }
        catch(FileNotFoundException exc) {
            System.out.println("File Not Found (databasesInserted)");
            exc.printStackTrace();
            return false;
        }
        catch(IOException exc) {
            System.out.println("IOException (databasesInserted)");
            exc.printStackTrace();
            return false;
        }
    }
    
    /**
     * Adds a new database to the file of the inserted databases
     * @param databaseName the name of the database
     */
    public void insertNewDatabase(String databaseName) {
        
        outputDatabase.print("," + databaseName);
    }
}
