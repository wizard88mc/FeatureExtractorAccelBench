package featureextractor.position_analysis;

import featureextractor.model.SingleCoordinateSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matteo Ciman
 */
public class Movement {
    
    private List<SingleCoordinateSet> valuesAccelerometer = new ArrayList<SingleCoordinateSet>();
    private List<SingleCoordinateSet> valuesLinear = new ArrayList<SingleCoordinateSet>();
    private List<SingleCoordinateSet> valuesRotationAccelerometer = new ArrayList<SingleCoordinateSet>();
    private List<SingleCoordinateSet> valuesRotationLinear = new ArrayList<SingleCoordinateSet>();
    private SingleCoordinateSet luminosity;
    private SingleCoordinateSet proximity;
    private String startPosition;
    private String endPosition;
    private String statusRecord;
    private static List<String> coordinates = new ArrayList<String>();
    
    static {
        coordinates.add("x");
        coordinates.add("y");
        coordinates.add("z");
    }
    
    public Movement(String startPosition, String endPosition, String statusRecord,
            List<SingleCoordinateSet> accelerometer, List<SingleCoordinateSet> linear, 
            List<SingleCoordinateSet> rotationAccelerometer, List<SingleCoordinateSet> rotationLinear, 
            SingleCoordinateSet luminosity, SingleCoordinateSet proximity) {
        
        this.startPosition = startPosition; this.endPosition = endPosition;
        this.statusRecord = statusRecord; this.valuesAccelerometer = accelerometer;
        this.valuesLinear = linear; this.valuesRotationAccelerometer = rotationAccelerometer;
        this.valuesRotationLinear = rotationLinear; this.luminosity = luminosity;
        this.proximity = proximity;
    }
}
