 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.extractor.db;

import featureextractor.model.Batch;
import featureextractor.model.Sample;
import featureextractor.model.TrunkFixSpec;
import featureextractor.plot.Plot;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.plot.IntervalMarker;

/**
 *
 * @author Nicola Beghin
 */
public class DbExtractor {

    private File db_path;
    private Connection connection = null;
    private double min_diff_for_next_batch = 1000000000;
    private String dbAccelerometer = "samples_accelerometer";
    //private String dbAccelerometer = "samples";
    private String dbLinear = "samples_linear";
    
    private String getRightDB(boolean linear) {
        if (!linear) {
            return dbAccelerometer;
        }
        else {
            return dbLinear;
        }
    }

    public DbExtractor(File db_path) {
        this.db_path = db_path;
    }

    private void connect() throws FileNotFoundException, ClassNotFoundException, SQLException {
        if (connection == null) {
            if (db_path.exists() == false) {
                throw new FileNotFoundException("No db found at " + db_path.getAbsolutePath());
            }
            Class.forName("org.sqlite.JDBC"); // ClassNotFoundException
            connection = DriverManager.getConnection("jdbc:sqlite:" + db_path.getAbsolutePath());
        }
    }

    public int countByAction(String action, boolean linear) throws FileNotFoundException, ClassNotFoundException, SQLException {
        this.connect();
        String query = "SELECT COUNT(*) as numsamples FROM " + getRightDB(linear) + " WHERE action=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, action);
        ResultSet rs = ps.executeQuery();
        return rs.getInt("numsamples");
    }

    private boolean checkActionExistence(String action, boolean linear) throws FileNotFoundException, ClassNotFoundException, SQLException {
        return this.countByAction(action, linear) == 0;
    }

    public List<int[]> getTrunkIDs(boolean linear) throws SQLException, FileNotFoundException, ClassNotFoundException {
        this.connect();
        PreparedStatement reset = connection.prepareStatement("UPDATE " + getRightDB(linear) + " SET trunk=NULL");
        reset.execute();
        // check for different sampling trunk
        String query = "SELECT s.ROWID,s.timestamp,(SELECT timestamp FROM " + getRightDB(linear) + " s2 WHERE ROWID=s.ROWID-1) as previous_timestamp,ABS(s.timestamp - (SELECT timestamp FROM " + getRightDB(linear) + " s2 WHERE ROWID=s.ROWID-1)) as diff FROM " + getRightDB(linear) + " s WHERE diff>1000000000 ORDER BY s.ROWID";
        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        List<int[]> ids = new ArrayList<int[]>();
        int first = 0, last = 0;
        int rowid = 0;
        while (rs.next()) {
            rowid = rs.getInt("ROWID") - 1; // previous row id
            ids.add(new int[]{first, rowid});
            first = rowid + 1;
        }
        return ids;
    }

    public void setTrunkIDs(boolean linear) throws SQLException, FileNotFoundException, ClassNotFoundException {
        int i = 1;
        for (int[] trunk : this.getTrunkIDs(linear)) {
            String query = "UPDATE " + getRightDB(linear) + " SET trunk=? WHERE ROWID>=? AND ROWID<=? AND trunk IS NULL";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, i);
            ps.setInt(2, trunk[0]);
            ps.setInt(3, trunk[1]);
            ps.execute();
            i++;
        }
    }

    public void deleteTrunk(int trunk_id, boolean linear) throws SQLException, FileNotFoundException, ClassNotFoundException {
        PreparedStatement delete_statement = connection.prepareStatement("DELETE FROM " + getRightDB(linear) + " WHERE trunk=?");
        delete_statement.setInt(1, trunk_id);
        delete_statement.execute();
    }

    public void deleteAllSteps(int trunk_id, boolean linear) throws SQLException, FileNotFoundException, ClassNotFoundException {
        PreparedStatement reset_statement = connection.prepareStatement("UPDATE " + getRightDB(linear) + " SET step=0 WHERE trunk=?");
        reset_statement.setInt(1, trunk_id);
        reset_statement.execute();
    }

    public void setTrunkMode(int trunk_id, String mode, boolean linear) throws SQLException, FileNotFoundException, ClassNotFoundException {
        PreparedStatement reset_statement = connection.prepareStatement("UPDATE " + getRightDB(linear) +" SET mode=? WHERE trunk=?");
        reset_statement.setString(1, mode);
        reset_statement.setInt(2, trunk_id);
        reset_statement.execute();
    }

    public void setTrunkAsInTasca(int trunk_id, boolean linear) throws SQLException, FileNotFoundException, ClassNotFoundException {
        setTrunkMode(trunk_id, "TASCA", linear);
    }

    public void setTrunkAsInMano(int trunk_id, boolean linear) throws SQLException, FileNotFoundException, ClassNotFoundException {
        setTrunkMode(trunk_id, "MANO", linear);
    }

    public List<IntervalMarker> getMarkersForTrunk(int trunk_id,  boolean linear) throws Exception {
        PreparedStatement statement = connection.prepareStatement("SELECT trunk,MIN(timestamp) as mintimestamp,MAX(timestamp) as maxtimestamp FROM " + getRightDB(linear) + " WHERE trunk=? AND step!=0 AND step IS NOT NULL GROUP BY step");
        statement.setInt(1, trunk_id);
        ResultSet rs = statement.executeQuery();
        List<IntervalMarker> markers = new ArrayList<IntervalMarker>();
        if (!rs.isBeforeFirst()) {
            throw new Exception("No step for this trunk");
        }
        while (rs.next()) {
            markers.add(new IntervalMarker(rs.getLong("mintimestamp") / Plot.time_divisor, rs.getLong("maxtimestamp") / Plot.time_divisor));
        }
        return markers;
    }

    public void applyTrunkFixes(List<TrunkFixSpec> fixes, boolean linear) throws SQLException, FileNotFoundException, ClassNotFoundException {
        this.connect();
        System.out.println("Applying " + fixes.size() + " fixes to trunks");
        PreparedStatement update_statement = connection.prepareStatement("UPDATE " + getRightDB(linear) + " SET action=? WHERE trunk=? AND (timestamp/? < ? OR timestamp/? > ?)");
        PreparedStatement delete_statement = connection.prepareStatement("DELETE FROM " + getRightDB(linear) + " WHERE trunk=?");
        for (TrunkFixSpec fix : fixes) {
            if (fix.isSkip()) {
                System.out.println("Deleting trunk " + fix.getTrunkId());
                delete_statement.setInt(1, fix.getTrunkId());
                delete_statement.execute();
            } else {
                System.out.println("Editing trunk " + fix.getTrunkId());
                update_statement.setString(1, "NON_STAIR");
                update_statement.setInt(2, fix.getTrunkId());
                update_statement.setInt(3, Plot.time_divisor);
                update_statement.setLong(4, fix.getStart());
                update_statement.setLong(5, Plot.time_divisor);
                update_statement.setLong(6, fix.getEnd());
                update_statement.execute();
            }
        }
    }

    public int getSamplesCount(boolean linear) throws SQLException, FileNotFoundException, ClassNotFoundException {
        this.connect();
        PreparedStatement step_statement = connection.prepareStatement("SELECT COUNT(*) as numsamples FROM " + getRightDB(linear));
        ResultSet rs = step_statement.executeQuery();
        return rs.getInt("numsamples");
    }

    public int getStairSamplesCount(boolean linear) throws SQLException, FileNotFoundException, ClassNotFoundException {
        this.connect();
        PreparedStatement step_statement = connection.prepareStatement("SELECT COUNT(*) as numsamples FROM " + getRightDB(linear) + " WHERE action!=?");
        step_statement.setString(1, "NON_STAIR");
        ResultSet rs = step_statement.executeQuery();
        return rs.getInt("numsamples");
    }

    public int getNonStairSamplesCount(boolean linear) throws SQLException, FileNotFoundException, ClassNotFoundException {
        this.connect();
        PreparedStatement step_statement = connection.prepareStatement("SELECT COUNT(*) as numsamples FROM " + getRightDB(linear) + " WHERE action=?");
        step_statement.setString(1, "NON_STAIR");
        ResultSet rs = step_statement.executeQuery();
        return rs.getInt("numsamples");
    }

    private int nextStepId(boolean linear) throws SQLException, FileNotFoundException, ClassNotFoundException {
        this.connect();
        PreparedStatement step_statement = connection.prepareStatement("SELECT MAX(step)+1 as next_step FROM " + getRightDB(linear));
        ResultSet rs = step_statement.executeQuery();
        return rs.getInt("next_step");
    }

    public void setSteps(List<IntervalMarker> markers, int trunk_id, boolean linear) throws SQLException, FileNotFoundException, ClassNotFoundException {
        this.connect();
        PreparedStatement reset_statement = connection.prepareStatement("UPDATE " + getRightDB(linear) + " SET step=0 WHERE trunk=?");
        reset_statement.setInt(1, trunk_id);
        reset_statement.execute();

        for (IntervalMarker marker : markers) {
            int step = this.nextStepId(linear);
            System.out.println("Set step " + step + " for trunk " + trunk_id + ", timestamp from " + (long) marker.getStartValue() + " to " + (long) marker.getEndValue());
            PreparedStatement step_statement = connection.prepareStatement("UPDATE " + getRightDB(linear) + " SET step=? WHERE trunk=? AND (timestamp/? >= ? AND timestamp/? <= ?)");
            step_statement.setInt(1, step);
            step_statement.setInt(2, trunk_id);
            step_statement.setInt(3, Plot.time_divisor);
            step_statement.setLong(4, (long) marker.getStartValue());
            step_statement.setInt(5, Plot.time_divisor);
            step_statement.setLong(6, (long) marker.getEndValue());
            step_statement.execute();
        }
    }

    public ArrayList<Sample> extractByAction(String action, boolean linear) throws FileNotFoundException, ClassNotFoundException, SQLException, AccelBenchException {
        this.connect();

        if (action != null && checkActionExistence(action, linear)) {
            throw new AccelBenchException("No sample for action '" + action + "'");
        }
        String queryAccelerometer = "SELECT ROWID, x, y, z, rotationX, rotationY, rotationZ, trunk, action, timestamp, step, mode  FROM " 
                + getRightDB(false) + " WHERE action=? ORDER BY ROWID";
        if (action == null) {
            queryAccelerometer = "SELECT ROWID, x, y, z, rotationX, rotationY, rotationZ, trunk, action, timestamp, step, mode FROM " + getRightDB(linear) + " ORDER BY ROWID";
        }
        PreparedStatement ps = connection.prepareStatement(queryAccelerometer);
        if (action != null) {
            System.out.println("Filtering by action: " + action);
            ps.setString(1, action);
        }
        ResultSet rs = ps.executeQuery();
        ArrayList<Sample> values = new ArrayList<Sample>();
        while (rs.next()) {
            values.add(new Sample(rs.getLong("timestamp"), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), 
                    rs.getDouble("rotationX"), rs.getDouble("rotationY"), rs.getDouble("rotationZ"), 
                    rs.getInt("trunk"), rs.getString("action"), rs.getInt("step"), rs.getString("mode")));
        }
        if (values.isEmpty()) {
            throw new AccelBenchException("No sample detected");
        }
        return values;
    }

    public ArrayList<Sample> extract(String action, boolean linear) throws FileNotFoundException, ClassNotFoundException, SQLException, AccelBenchException {
        this.connect();

        if (action != null && checkActionExistence(action, linear)) {
            throw new AccelBenchException("No sample for action '" + action + "'");
        }
        return extractByAction(action, linear);
    }

    public List<Batch> extractByTrunk(boolean linear) throws FileNotFoundException, ClassNotFoundException, SQLException, AccelBenchException, Exception {
        this.connect();
        List<Sample> values = new ArrayList<Sample>();
        List<Sample> valuesLinear = new ArrayList<Sample>();
        List<Batch> batches = new ArrayList<Batch>();
        
        String dbAccelerometer = getRightDB(false);

        String query = "SELECT trunk, MIN(ROWID) as minid,MAX(ROWID) as maxid FROM " + dbAccelerometer + " GROUP BY trunk";
        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        int trunk_id = 0, min = 0, max = 0;
        long lastTimestamp = 0;
        while (rs.next()) {
            trunk_id = rs.getInt("trunk");
            min = rs.getInt("minid");
            max = rs.getInt("maxid");
            query = "SELECT * FROM " + dbAccelerometer + " WHERE ROWID>=? AND ROWID<=?";
            PreparedStatement get_stmt = connection.prepareStatement(query);
            get_stmt.setInt(1, min);
            get_stmt.setInt(2, max);
            ResultSet rs2 = get_stmt.executeQuery();
            while (rs2.next()) {
                values.add(new Sample(rs2.getLong("timestamp"), rs2.getDouble("x"), rs2.getDouble("y"), rs2.getDouble("z"), 
                        rs2.getDouble("rotationX"), rs2.getDouble("rotationY"), rs2.getDouble("rotationZ"), 
                        rs2.getInt("trunk"), rs2.getString("action"), rs2.getInt("step"), rs2.getString("mode")));
                
                if (lastTimestamp != 0) {
                    System.out.println("Diff: " + (rs2.getLong("timestamp") - lastTimestamp)/ 1000000);
                }
                lastTimestamp = rs2.getLong("timestamp");
            }
            
            String linearDB = getRightDB(true);
            String queryLinear = "SELECT MIN(ROWID) as minid,MAX(ROWID) as maxid FROM " + linearDB + " WHERE trunk = " + trunk_id;
            PreparedStatement psLinear = connection.prepareStatement(queryLinear);
            ResultSet rsLinear = psLinear.executeQuery();
            int minLinear = 0, maxLinear = 0;
            long lastTimestampLinear = 0;
            while (rsLinear.next()) {
                minLinear = rsLinear.getInt("minid");
                maxLinear = rsLinear.getInt("maxid");
                queryLinear = "SELECT * FROM " + linearDB + " WHERE ROWID>=? AND ROWID<=?";
                PreparedStatement get_stmtLinear = connection.prepareStatement(queryLinear);
                get_stmtLinear.setInt(1, minLinear);
                get_stmtLinear.setInt(2, maxLinear);
                ResultSet rs2Linear = get_stmtLinear.executeQuery();
                while (rs2Linear.next()) {
                    valuesLinear.add(new Sample(rs2Linear.getLong("timestamp"), rs2Linear.getDouble("x"), rs2Linear.getDouble("y"), rs2Linear.getDouble("z"), 
                            rs2Linear.getDouble("rotationX"), rs2Linear.getDouble("rotationY"), rs2Linear.getDouble("rotationZ"), 
                            rs2Linear.getInt("trunk"), rs2Linear.getString("action"), rs2Linear.getInt("step"), rs2Linear.getString("mode")));

                    if (lastTimestampLinear != 0) {
                        System.out.println("Diff: " + (rs2Linear.getLong("timestamp") - lastTimestampLinear)/ 1000000);
                    }
                    lastTimestamp = rs2Linear.getLong("timestamp");
                }
            }
            
            Batch batch = new Batch(values, valuesLinear);
            batch.setTrunk(trunk_id);
            batch.setMode(values.get(0).getMode());
            batch.setTitle("Trunk " + trunk_id + ": " + values.get(0).getAction());
            batches.add(batch);
            values.clear();
            valuesLinear.clear();
        }
        return batches;
    }
    
    public List<Batch> extractByTrunkAndAction(String action) throws FileNotFoundException, ClassNotFoundException, SQLException, AccelBenchException, Exception {
        
        this.connect();
        List<Sample> values = new ArrayList<Sample>();
        List<Sample> valuesLinear = new ArrayList<Sample>();
        List<Batch> batches = new ArrayList<Batch>();
        
        String dbAccelerometer = getRightDB(false);

        String query = "SELECT trunk, MIN(ROWID) as minid,MAX(ROWID) as maxid FROM " + dbAccelerometer + 
                " WHERE action = \"" + action + "\" GROUP BY trunk";
        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        int trunk_id = 0, min = 0, max = 0;
        long lastTimestamp = 0;
        while (rs.next()) {
            trunk_id = rs.getInt("trunk");
            min = rs.getInt("minid");
            max = rs.getInt("maxid");
            query = "SELECT * FROM " + dbAccelerometer + " WHERE ROWID>=? AND ROWID<=?";
            PreparedStatement get_stmt = connection.prepareStatement(query);
            get_stmt.setInt(1, min);
            get_stmt.setInt(2, max);
            ResultSet rs2 = get_stmt.executeQuery();
            while (rs2.next()) {
                values.add(new Sample(rs2.getLong("timestamp"), rs2.getDouble("x"), rs2.getDouble("y"), rs2.getDouble("z"), 
                        rs2.getDouble("rotationX"), rs2.getDouble("rotationY"), rs2.getDouble("rotationZ"), 
                        rs2.getInt("trunk"), rs2.getString("action"), rs2.getInt("step"), rs2.getString("mode")));
                
                if (lastTimestamp != 0) {
                    System.out.println("Diff: " + (rs2.getLong("timestamp") - lastTimestamp)/ 1000000);
                }
                lastTimestamp = rs2.getLong("timestamp");
            }
            
            String linearDB = getRightDB(true);
            String queryLinear = "SELECT MIN(ROWID) as minid,MAX(ROWID) as maxid FROM " + linearDB + " WHERE trunk = " + trunk_id;
            PreparedStatement psLinear = connection.prepareStatement(queryLinear);
            ResultSet rsLinear = psLinear.executeQuery();
            int minLinear = 0, maxLinear = 0;
            long lastTimestampLinear = 0;
            while (rsLinear.next()) {
                minLinear = rsLinear.getInt("minid");
                maxLinear = rsLinear.getInt("maxid");
                queryLinear = "SELECT * FROM " + linearDB + " WHERE ROWID>=? AND ROWID<=?";
                PreparedStatement get_stmtLinear = connection.prepareStatement(queryLinear);
                get_stmtLinear.setInt(1, minLinear);
                get_stmtLinear.setInt(2, maxLinear);
                ResultSet rs2Linear = get_stmtLinear.executeQuery();
                while (rs2Linear.next()) {
                    valuesLinear.add(new Sample(rs2Linear.getLong("timestamp"), rs2Linear.getDouble("x"), rs2Linear.getDouble("y"), rs2Linear.getDouble("z"), 
                            rs2Linear.getDouble("rotationX"), rs2Linear.getDouble("rotationY"), rs2Linear.getDouble("rotationZ"), 
                            rs2Linear.getInt("trunk"), rs2Linear.getString("action"), rs2Linear.getInt("step"), rs2Linear.getString("mode")));

                    if (lastTimestampLinear != 0) {
                        System.out.println("Diff: " + (rs2Linear.getLong("timestamp") - lastTimestampLinear)/ 1000000);
                    }
                    lastTimestamp = rs2Linear.getLong("timestamp");
                }
            }
            
            Batch batch = new Batch(values, valuesLinear);
            batch.setTrunk(trunk_id);
            batch.setMode(values.get(0).getMode());
            batch.setAction(values.get(0).getAction());
            batch.setTitle("Trunk " + trunk_id + ": " + values.get(0).getAction());
            batches.add(batch);
            values.clear();
            valuesLinear.clear();
        }
        return batches;
    }

    // to be fixed (timestamp from the moment the mobile has been powered on)
    public int getSamplingRate(boolean linear) throws Exception {
        this.connect();
        String query = "SELECT COUNT(*)/((MAX(timestamp)-MIN(timestamp))/1000000000) as frequenza FROM " + getRightDB(linear) + " GROUP BY trunk";
        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        int sampling_rate = 0, trunks = 0;
        while (rs.next()) {
            sampling_rate += rs.getInt("frequenza");
            trunks++;
        }
        return sampling_rate / trunks;
    }

    public int getAvgSamplesForStep(boolean linear) throws Exception {
        this.connect();
        String query = "SELECT COUNT(*) as count FROM " + getRightDB(linear) + " WHERE step>0 GROUP BY step";
        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        int sampling_rate = 0, steps = 0;
        while (rs.next()) {
            sampling_rate += rs.getInt("count");
            steps++;
        }
        return sampling_rate / steps;
    }
}
