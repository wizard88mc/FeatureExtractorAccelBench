/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.extractor.db;

import featureextractor.model.Sample;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Nicola Beghin
 */
public class DbExtractor {

    private File db_path;
    private Connection connection = null;
    private double min_diff_for_next_batch=1000000000;

    public DbExtractor(File db_path) {
        this.db_path = db_path;
    }

    private void connect() throws FileNotFoundException, ClassNotFoundException, SQLException {
        if (connection==null) {
            if (db_path.exists() == false) {
                throw new FileNotFoundException("No db found at " + db_path.getAbsolutePath());
            }
            Class.forName("org.sqlite.JDBC"); // ClassNotFoundException
            connection = DriverManager.getConnection("jdbc:sqlite:" + db_path.getAbsolutePath());
        }
    }

    private int checkActionExistence(String action) throws FileNotFoundException, ClassNotFoundException, SQLException {
        this.connect();
        String query = "SELECT COUNT(*) as numsamples FROM samples WHERE action=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, action);
        ResultSet rs = ps.executeQuery();
        return rs.getInt("numsamples");
    }
    
        public ArrayList<Sample> extract(String action) throws FileNotFoundException, ClassNotFoundException, SQLException, AccelBenchException {
        this.connect();
 
        if (checkActionExistence(action)==0) throw new AccelBenchException("No sample for action '"+action+"'");
        String query = "SELECT ROWID,x,y,z,timestamp,timestamp/1000000 as parsedtimestamp FROM samples WHERE action=? ORDER BY ROWID";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, action);
        ResultSet rs = ps.executeQuery();
        ArrayList<Sample> values=new ArrayList<Sample>();
        while (rs.next()) {
            values.add(new Sample(rs.getDouble("timestamp"), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z")));
        }
        if (values.isEmpty()) throw new AccelBenchException("No sample detected");
        return values;
    }
        
    public ArrayList<ArrayList<Sample>> extractByBatch(String action) throws FileNotFoundException, ClassNotFoundException, SQLException, AccelBenchException {
        this.connect();
 
        if (checkActionExistence(action)==0) throw new AccelBenchException("No sample for action '"+action+"'");
        String query = "SELECT ROWID,x,y,z,timestamp,timestamp/1000000 as parsedtimestamp FROM samples WHERE action=? ORDER BY ROWID";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, action);
        ResultSet rs = ps.executeQuery();
//        ArrayList<Sample> valuesExtracted=new ArrayList<Sample>();
        ArrayList<ArrayList<Sample>> batches=new ArrayList<ArrayList<Sample>>();
        ArrayList<Sample> batch=new ArrayList<Sample>();
        double last_timestamp=0.00;
        double time_diff=0.00;
        boolean first=true;
        double first_timestamp=0.00;
        while (rs.next()) {
            if (first) {
                first_timestamp=rs.getDouble("timestamp");
                last_timestamp=rs.getDouble("timestamp");
                first=false;
            }
            time_diff=(rs.getDouble("timestamp")-last_timestamp);
            if (time_diff>min_diff_for_next_batch) {
                Date date=new Date(rs.getLong("parsedtimestamp"));
                System.out.println("Rilevato nuovo batch [timestamp: "+date+" diff: "+time_diff/1000000+"] (#"+rs.getInt("ROWID")+")");
                batches.add(batch);
                batch=new ArrayList<Sample>();
            }
            last_timestamp=rs.getDouble("timestamp");
            batch.add(new Sample(last_timestamp, rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z")));
        }
        if (batches.isEmpty()) throw new AccelBenchException("No sample detected");
        return batches;
    }
    
        public ArrayList<ArrayList<Sample>> extract2(String action) throws FileNotFoundException, ClassNotFoundException, SQLException, AccelBenchException {
        this.connect();
 
        if (checkActionExistence(action)==0) throw new AccelBenchException("No sample for action '"+action+"'");
        String query = "SELECT ROWID,x,y,z,timestamp,timestamp/1000000 as parsedtimestamp FROM samples WHERE action=? ORDER BY ROWID";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, action);
        ResultSet rs = ps.executeQuery();
//        ArrayList<Sample> valuesExtracted=new ArrayList<Sample>();
        ArrayList<ArrayList<Sample>> batches=new ArrayList<ArrayList<Sample>>();
        ArrayList<Sample> batch=new ArrayList<Sample>();
        double last_timestamp=0.00;
        double time_diff=0.00;
        boolean first=true;
        double first_timestamp=0.00;
        while (rs.next()) {
            if (first) {
                first_timestamp=rs.getDouble("timestamp");
                last_timestamp=rs.getDouble("timestamp");
                first=false;
            }
            time_diff=(rs.getDouble("timestamp")-last_timestamp);
            if (time_diff>min_diff_for_next_batch) {
                Date date=new Date(rs.getLong("parsedtimestamp"));
                System.out.println("Rilevato nuovo batch [timestamp: "+date+" diff: "+time_diff/1000000+"] (#"+rs.getInt("ROWID")+")");
                batches.add(batch);
                batch=new ArrayList<Sample>();
            }
            last_timestamp=rs.getDouble("timestamp");
            batch.add(new Sample(last_timestamp, rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z")));
        }
        if (batches.isEmpty()) throw new AccelBenchException("No sample detected");
        return batches;
    }
}
