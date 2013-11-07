/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.extractor.db;

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

    public int countByAction(String action) throws FileNotFoundException, ClassNotFoundException, SQLException {
        this.connect();
        String query = "SELECT COUNT(*) as numsamples FROM samples WHERE action=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, action);
        ResultSet rs = ps.executeQuery();
        return rs.getInt("numsamples");
    }

    private boolean checkActionExistence(String action) throws FileNotFoundException, ClassNotFoundException, SQLException {
        return this.countByAction(action) == 0;
    }

    public List<int[]> getTrunkIDs() throws SQLException, FileNotFoundException, ClassNotFoundException {
        this.connect();
        PreparedStatement reset = connection.prepareStatement("UPDATE samples SET trunk=NULL");
        reset.execute();
        // check for different sampling trunk
        String query = "SELECT s.ROWID,s.timestamp,(SELECT timestamp FROM samples s2 WHERE ROWID=s.ROWID-1) as previous_timestamp,ABS(s.timestamp - (SELECT timestamp FROM samples s2 WHERE ROWID=s.ROWID-1)) as diff FROM samples s WHERE diff>1000000000 ORDER BY s.ROWID";
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

    public void setTrunkIDs() throws SQLException, FileNotFoundException, ClassNotFoundException {
        int i = 1;
        for (int[] trunk : this.getTrunkIDs()) {
            String query = "UPDATE samples SET trunk=? WHERE ROWID>=? AND ROWID<=? AND trunk IS NULL";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, i);
            ps.setInt(2, trunk[0]);
            ps.setInt(3, trunk[1]);
            ps.execute();
            i++;
        }
    }

    public void deleteTrunk(int trunk_id) throws SQLException, FileNotFoundException, ClassNotFoundException {
        PreparedStatement delete_statement = connection.prepareStatement("DELETE FROM samples WHERE trunk=?");
        delete_statement.setInt(1, trunk_id);
        delete_statement.execute();
    }

    public void deleteAllSteps(int trunk_id) throws SQLException, FileNotFoundException, ClassNotFoundException {
        PreparedStatement reset_statement = connection.prepareStatement("UPDATE samples SET step=0 WHERE trunk=?");
        reset_statement.setInt(1, trunk_id);
        reset_statement.execute();
    }

    public void setTrunkMode(int trunk_id, String mode) throws SQLException, FileNotFoundException, ClassNotFoundException {
        PreparedStatement reset_statement = connection.prepareStatement("UPDATE samples SET mode=? WHERE trunk=?");
        reset_statement.setString(1, mode);
        reset_statement.setInt(2, trunk_id);
        reset_statement.execute();
    }

    public void setTrunkAsInTasca(int trunk_id) throws SQLException, FileNotFoundException, ClassNotFoundException {
        setTrunkMode(trunk_id, "TASCA");
    }

    public void setTrunkAsInMano(int trunk_id) throws SQLException, FileNotFoundException, ClassNotFoundException {
        setTrunkMode(trunk_id, "MANO");
    }

    public void applyTrunkFixes(List<TrunkFixSpec> fixes) throws SQLException, FileNotFoundException, ClassNotFoundException {
        this.connect();
        System.out.println("Applying " + fixes.size() + " fixes to trunks");
        PreparedStatement update_statement = connection.prepareStatement("UPDATE samples SET action=? WHERE trunk=? AND (timestamp/? < ? OR timestamp/? > ?)");
        PreparedStatement delete_statement = connection.prepareStatement("DELETE FROM samples WHERE trunk=?");
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

    private int nextStepId() throws SQLException, FileNotFoundException, ClassNotFoundException {
        this.connect();
        PreparedStatement step_statement = connection.prepareStatement("SELECT MAX(step)+1 as next_step FROM samples");
        ResultSet rs = step_statement.executeQuery();
        return rs.getInt("next_step");
    }

    public void setSteps(List<IntervalMarker> markers, int trunk_id) throws SQLException, FileNotFoundException, ClassNotFoundException {
        this.connect();
        PreparedStatement reset_statement = connection.prepareStatement("UPDATE samples SET step=0 WHERE trunk=?");
        reset_statement.setInt(1, trunk_id);
        reset_statement.execute();

        for (IntervalMarker marker : markers) {
            int step = this.nextStepId();
            System.out.println("Set step " + step + " for trunk " + trunk_id + ", timestamp from " + (long) marker.getStartValue() + " to " + (long) marker.getEndValue());
            PreparedStatement step_statement = connection.prepareStatement("UPDATE samples SET step=? WHERE trunk=? AND (timestamp/? >= ? AND timestamp/? <= ?)");
            step_statement.setInt(1, step);
            step_statement.setInt(2, trunk_id);
            step_statement.setInt(3, Plot.time_divisor);
            step_statement.setLong(4, (long) marker.getStartValue());
            step_statement.setInt(5, Plot.time_divisor);
            step_statement.setLong(6, (long) marker.getEndValue());
            step_statement.execute();
        }
    }

    public ArrayList<Sample> extract(String action) throws FileNotFoundException, ClassNotFoundException, SQLException, AccelBenchException {
        this.connect();

        if (action != null && checkActionExistence(action)) {
            throw new AccelBenchException("No sample for action '" + action + "'");
        }
        String query = "SELECT ROWID,x,y,z,trunk,action,timestamp,step  FROM samples WHERE action=? ORDER BY ROWID";
        if (action == null) {
            query = "SELECT ROWID,x,y,z,trunk,action,timestamp,step FROM samples ORDER BY ROWID";
        }
        PreparedStatement ps = connection.prepareStatement(query);
        if (action != null) {
            System.out.println("Filtering by action: " + action);
            ps.setString(1, action);
        }
        ResultSet rs = ps.executeQuery();
        ArrayList<Sample> values = new ArrayList<Sample>();
        while (rs.next()) {
            values.add(new Sample(rs.getLong("timestamp"), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getInt("trunk"), rs.getString("action"), rs.getInt("step")));
        }
        if (values.isEmpty()) {
            throw new AccelBenchException("No sample detected");
        }
        return values;
    }

    // to be fixed (timestamp from the moment the mobile has been powered on)
    public int getSamplingRate() throws Exception {
        this.connect();
        String query = "SELECT COUNT(*)/((MAX(timestamp)-MIN(timestamp))/1000000000) as frequenza FROM samples GROUP BY trunk";
        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        int sampling_rate = 0, trunks = 0;
        while (rs.next()) {
            sampling_rate += rs.getInt("frequenza");
            trunks++;
        }
        return sampling_rate / trunks;
    }

    public int getAvgSamplesForStep() throws Exception {
        this.connect();
        String query = "SELECT COUNT(*) as count FROM samples WHERE step>0 GROUP BY step";
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
