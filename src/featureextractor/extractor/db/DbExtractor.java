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

/**
 *
 * @author Nicola Beghin
 */
public class DbExtractor {

    private File db_path;
    private Connection connection = null;

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
        String query = "SELECT x,y,z,timestamp FROM samples WHERE action=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, action);
        ResultSet rs = ps.executeQuery();
        ArrayList<Sample> valuesExtracted=new ArrayList<Sample>();
        while (rs.next()) {
            valuesExtracted.add(new Sample(rs.getDouble("timestamp"), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z")));
        }
        if (valuesExtracted.isEmpty()) throw new AccelBenchException("No sample detected");
        return valuesExtracted;
    }
}
