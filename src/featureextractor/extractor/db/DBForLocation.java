/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package featureextractor.extractor.db;

import featureextractor.model.Batch;
import featureextractor.model.DataTime;
import featureextractor.model.Sample;
import featureextractor.model.SingleCoordinateSet;
import featureextractor.position_analysis.Movement;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matteo Ciman
 */
public class DBForLocation {
    
    private File db_path;
    private Connection connection = null;
    private String dbAccelerometer = "samples_accelerometer";
    private String dbLinear = "samples_linear";
    
    private String getRightDB(boolean linear) {
        if (!linear) {
            return dbAccelerometer;
        }
        else {
            return dbLinear;
        }
    }

    public DBForLocation(File db_path) {
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
    
    private boolean checkTrunkExistence(int trunkID) throws FileNotFoundException, 
            ClassNotFoundException, SQLException {
        
        this.connect();
        String query = "SELECT * FROM " + getRightDB(false) + " WHERE trunk=" + trunkID;
        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        
        if (!rs.next()) {
            return false;
        }
        else {
            return true;
        }
        
    }
    
    public String extractMovementsData(boolean linear, List<SingleCoordinateSet> values,
            List<SingleCoordinateSet> rotation, SingleCoordinateSet luminosity,
            SingleCoordinateSet proximity, int trunk) throws FileNotFoundException, 
            ClassNotFoundException, SQLException {
        
        String propertiesToReturn = null;
        
        this.connect();
            
        String query = "SELECT * FROM " + getRightDB(linear) + " WHERE trunk>=?";
        PreparedStatement get_stmt = connection.prepareStatement(query);
        get_stmt.setInt(1, trunk);
        ResultSet rs2 = get_stmt.executeQuery();
        while (rs2.next()) {

            values.get(0).addValue(new DataTime(rs2.getLong("timestamp"), rs2.getDouble("x"), -1));
            values.get(1).addValue(new DataTime(rs2.getLong("timestamp"), rs2.getDouble("y"), -1));
            values.get(2).addValue(new DataTime(rs2.getLong("timestamp"), rs2.getDouble("z"), -1));

            rotation.get(0).addValue(new DataTime(rs2.getLong("timestamp"), rs2.getDouble("rotationX"), -1));
            rotation.get(1).addValue(new DataTime(rs2.getLong("timestamp"), rs2.getDouble("rotationY"), -1));
            rotation.get(2).addValue(new DataTime(rs2.getLong("timestamp"), rs2.getDouble("rotationZ"), -1));

            if (!linear) {
                luminosity.addValue(new DataTime(rs2.getLong("timestamp"), rs2.getDouble("luminosity"), -1));
                proximity.addValue(new DataTime(rs2.getLong("timestamp"), rs2.getDouble("proximity"), -1));

                if (propertiesToReturn == null) {
                    propertiesToReturn = rs2.getString("start_position")
                            .concat(",").concat(rs2.getString("end_position"))
                            .concat(",").concat(rs2.getString("current_task"));
                }       
            }
        }
        
        return propertiesToReturn;
    }
    
    public void instantiateList(List<SingleCoordinateSet> values) {
        
        for (String axis: Movement.coordinates) {
            values.add(new SingleCoordinateSet(axis));
        }
    }
    
    public List<Movement> getListAllMovements() {
        
        List<Movement> movements = new ArrayList<Movement>();
        
        int trunkID = 1;
        
        try {
            while (this.checkTrunkExistence(trunkID)) {
                List<SingleCoordinateSet> valuesAccelerometer = new ArrayList<SingleCoordinateSet>(),
                        valuesLinear = new ArrayList<SingleCoordinateSet>(),
                        valuesRotationAccelerometer = new ArrayList<SingleCoordinateSet>(),
                        valuesRotationLinear = new ArrayList<SingleCoordinateSet>();
                
                instantiateList(valuesAccelerometer); instantiateList(valuesLinear);
                instantiateList(valuesRotationAccelerometer); instantiateList(valuesRotationLinear);
                
                SingleCoordinateSet luminosity = new SingleCoordinateSet("luminosity"),
                        proximity = new SingleCoordinateSet("proximity");
                
                String additionalInfos = extractMovementsData(false, valuesAccelerometer, 
                        valuesRotationAccelerometer, luminosity, proximity, trunkID);
                extractMovementsData(true, valuesLinear, valuesRotationLinear, null, null, trunkID);
                
                String[] infos = additionalInfos.split(",");
                movements.add(new Movement(infos[0], infos[1], infos[2], valuesAccelerometer,
                    valuesLinear, valuesRotationAccelerometer, valuesRotationLinear, 
                    luminosity, proximity));
                
                trunkID++;
            }
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
        
        return movements;
    }
}
