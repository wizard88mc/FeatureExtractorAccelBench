
package featureextractor.extractor.db;

import featureextractor.model.DataTime;
import featureextractor.model.SingleCoordinateSet;
import featureextractor.model.SlidingWindow;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author matteo
 */
public class DBDataManager {

    private File db;
    private Connection connection = null;
    
    public DBDataManager(int slidingWindowSize) throws IOException {
        
        /*String finalFile = "data/dbWindows" 
                + Integer.toString((int)millisecondsSlidingWindow)
                + ".db";
        db = new File(finalFile);
        db.createNewFile();*/
        try {
            initializeDB(slidingWindowSize);
        }
        catch(Exception exc) {
            System.out.println(exc);
        }
    }
    
    public DBDataManager() {
        
    }
    
    private void connect() throws FileNotFoundException, ClassNotFoundException, SQLException, InstantiationException,
            IllegalAccessException{
        if (connection == null) {
            /*if (db.exists() == false) {
                throw new FileNotFoundException("No db found at " + db.getAbsolutePath());
            }*/
            //Class.forName("org.sqlite.JDBC"); // ClassNotFoundException
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            //connection = DriverManager.getConnection("jdbc:sqlite:" + db.getAbsolutePath());
            connection = DriverManager.getConnection("jdbc:mysql://localhost/accelbench?user=matteo&password=matteo");
        }
    }
    
    private void initializeDB(int slidingWindowSize) throws FileNotFoundException, ClassNotFoundException, SQLException, InstantiationException,
            IllegalAccessException {
        
        connect();
        
        String createTable = "CREATE TABLE IF NOT EXISTS samples" + slidingWindowSize + 
                " (ID INT AUTO_INCREMENT PRIMARY KEY,"
                + "timestamp DOUBLE NOT NULL,"
                + "x DOUBLE NOT NULL, y DOUBLE NOT NULL, z DOUBLE NOT NULL,"
                + "action VARCHAR(50) NOT NULL, trunk INT, mode VARCHAR(50) NOT NULL, isLinear TINYINT(1) DEFAULT 0)";
        
        PreparedStatement ps = connection.prepareStatement(createTable);
        //String createDB = "CREATE DATABASE IF NOT EXISTS dbFinalSamples";
        
        //stmt.executeUpdate(createDB);
        ps.executeUpdate();
        
        String createTableDB = "CREATE TABLE IF NOT EXISTS databasesInserted" + slidingWindowSize
                + " (ID INTEGER AUTO_INCREMENT PRIMARY KEY,"
                + "dbName VARCHAR(50) NOT NULL)";
        
        PreparedStatement ps1 = connection.prepareStatement(createTableDB);
        ps1.executeUpdate();
    }
    
    public void newDB(String dbName) {
        
        try {
            this.connect();
            String queryToInsert = "INSERT INTO databasesInserted(dbName) VALUES (\"" + dbName + "\")";
            PreparedStatement ps = connection.prepareStatement(queryToInsert);
            
            ps.executeUpdate();
        }
        catch(Exception exc) {
            exc.printStackTrace();
            System.out.println(exc);
        }
    }
    
    public boolean checkDBAlreadyInserted(String name) {
        
        try {
            connect();
            
            String query = "SELECT * FROM databasesInserted WHERE dbName = \"" + name+ "\"";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return false;
            }
            else {
                return true;
            }
        }
        catch(Exception exc) {
            exc.printStackTrace();
            System.out.println(exc);
            return false;
        }
    }
    
    private int getLastTrunk(boolean linear) {
        int trunkID = -1; 
        
        try {
            connect();
            String query = "SELECT MAX(trunk) as trunk FROM samples WHERE isLinear = " + (linear==true?1:0);
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            rs.next();
            
            trunkID = rs.getInt("trunk");
        }
        catch(Exception exc) {
            exc.printStackTrace();
            System.out.println(exc);
        }
        return trunkID;
    }
    
    public void addNewSlidingWindow(SlidingWindow window, String action, boolean linear) {
        
        List<SingleCoordinateSet> values = window.getValues();
        int lengthSlidingWindow = values.get(0).getValues().size();
        int newTrunkID = (getLastTrunk(linear)) + 1;
        
        for (int i = 0; i < lengthSlidingWindow; i++) {
            
            double timestamp = values.get(0).getValues().get(i).getTime();
            double x = values.get(0).getValues().get(i).getValue(),
                    y = values.get(1).getValues().get(i).getValue(),
                    z = values.get(2).getValues().get(i).getValue();
            
            String queryInsert = "INSERT INTO samples (timestamp, x, y, z," +
                    " action, trunk, mode, isLinear) VALUES (?,?,?,?,?,?,?,?)";
            try {
                connect();
                PreparedStatement ps = connection.prepareStatement(queryInsert);
                ps.setDouble(1, timestamp);
                ps.setDouble(2, x);
                ps.setDouble(3, y);
                ps.setDouble(4, z);
                ps.setString(5, action);
                ps.setInt(6, newTrunkID);
                ps.setString(7, window.getPlaceAction());
                ps.setInt(8, linear==false?0:1);
                
                ps.execute();
            }
            catch(Exception exc) {
                exc.printStackTrace();
                System.out.println(exc);
            }
        }
    }
    
    public List<SlidingWindow> getSlidinwWindows(int slidingWindowSize, String action, boolean linear) {
        
        List<SlidingWindow> slidingWindows = new ArrayList<SlidingWindow>();
        
        try {
            connect();
            
            String queryGetTrunks = "SELECT DISTINCT(trunk) AS trunkID FROM samples" + slidingWindowSize + " WHERE "
                    + "action=\"" +action +"\" AND isLinear=" + (linear==false?0:1); 
            PreparedStatement ps = connection.prepareStatement(queryGetTrunks);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                
                int trunk = rs.getInt("trunkID");
                String mode = null;
                String queryGetIDS = "SELECT * FROM samples" + slidingWindowSize + " WHERE "
                        + "action=\"" + action + "\" and isLinear=" + (linear==false?0:1)+
                        " and trunk=" + trunk;
                
                PreparedStatement ps1 = connection.prepareStatement(queryGetIDS);
                ResultSet rs1 = ps1.executeQuery();
                
                List<SingleCoordinateSet> singleCoordinateSet = new LinkedList<SingleCoordinateSet>();
                singleCoordinateSet.add(new SingleCoordinateSet("X"));
                singleCoordinateSet.add(new SingleCoordinateSet("Y"));
                singleCoordinateSet.add(new SingleCoordinateSet("Z"));
                
                while (rs1.next()) {
                    double timestamp = rs1.getDouble("timestamp"),
                            x = rs1.getDouble("x"), y = rs1.getDouble("y"),
                            z = rs1.getDouble("z");
                    if (mode == null) {
                        mode = rs1.getString("mode");
                    }
                    
                    singleCoordinateSet.get(0).addValue(new DataTime(timestamp, x, -1));
                    singleCoordinateSet.get(1).addValue(new DataTime(timestamp, y, -1));
                    singleCoordinateSet.get(2).addValue(new DataTime(timestamp, z, -1));
                }
                
                slidingWindows.add(new SlidingWindow(action, mode, singleCoordinateSet, linear, trunk));
            }
        }
        catch(Exception exc) {
            exc.printStackTrace();
            System.out.println(exc);
        }
        return slidingWindows;
    }
    
    public int deleteTrunk(int slidingWindowSize, int trunkID, boolean linear, String action) {
        
        String query = "DELETE FROM samples"+slidingWindowSize+ " WHERE "
                + "trunk="+trunkID+" AND isLinear=" + (linear==false?0:1) + " and "
                + "action = \"" + action + "\"";
        
        try {
            connect();
            
            PreparedStatement ps = connection.prepareStatement(query);
            
            int result = ps.executeUpdate();
            return result;
        }
        catch(Exception exc) {
            exc.printStackTrace();
            System.out.println(exc);
            return -1;
        }
        
    }
}
