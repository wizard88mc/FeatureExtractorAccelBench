package featureextractor.position_analysis;

import featureextractor.model.DataTime;
import featureextractor.model.Sample;
import featureextractor.model.SingleCoordinateSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matteo Ciman
 */
public class Movement {
    
    private List<SingleCoordinateSet> valuesAccelerometer = new ArrayList<SingleCoordinateSet>();
    private List<SingleCoordinateSet> valuesWithoutGravity = new ArrayList<SingleCoordinateSet>(); 
   private List<SingleCoordinateSet> valuesLinear = new ArrayList<SingleCoordinateSet>();
    private List<SingleCoordinateSet> valuesRotationAccelerometer = new ArrayList<SingleCoordinateSet>();
    private List<SingleCoordinateSet> valuesRotationLinear = new ArrayList<SingleCoordinateSet>();
    private SingleCoordinateSet proximity;
    private String startPosition;
    private String endPosition;
    private String statusRecord;
    public static List<String> coordinates = new ArrayList<String>();
    
    static {
        coordinates.add("x");
        coordinates.add("y");
        coordinates.add("z");
    }
    
    public Movement(String startPosition, String endPosition, String statusRecord,
            List<SingleCoordinateSet> accelerometer, List<SingleCoordinateSet> linear, 
            List<SingleCoordinateSet> rotationAccelerometer, List<SingleCoordinateSet> rotationLinear, 
            SingleCoordinateSet proximity) {
        
        this.startPosition = startPosition; this.endPosition = endPosition;
        this.statusRecord = statusRecord; this.valuesAccelerometer = accelerometer;
        this.valuesLinear = linear; this.valuesRotationAccelerometer = rotationAccelerometer;
        this.valuesRotationLinear = rotationLinear;
        this.proximity = proximity;
        
        removeGravity();
        
        rotateValues(valuesAccelerometer, valuesRotationAccelerometer);
        rotateValues(valuesLinear, valuesRotationLinear);
        rotateValues(valuesWithoutGravity, valuesRotationAccelerometer);
        
        calculateVAndXPlusYValues(valuesAccelerometer); calculateVAndXPlusYValues(valuesLinear);
        calculateVAndXPlusYValues(valuesWithoutGravity);
    }
    
    /**
     * Rotates the values of the accelerometer or the linear acceleration
     * using the rotation values
     * @param values: the values to rotate
     * @param rotationValues: the values of the rotation vector
     */
    private void rotateValues(List<SingleCoordinateSet> values, List<SingleCoordinateSet> rotationValues) {
        
        for (int i = 0; i < values.get(0).size(); i++) {
            
            double rotationX = rotationValues.get(0).getValues().get(i).getValue(),
                    rotationY = rotationValues.get(1).getValues().get(i).getValue(),
                    rotationZ = rotationValues.get(2).getValues().get(i).getValue();
            
            double norm = Math.sqrt(Math.pow(rotationX, 2) + Math.pow(rotationY, 2) + 
                    Math.pow(rotationZ, 2));
            
            if (norm >1) {
                norm = 1;
            }
            double alpha = 2 * Math.asin(norm);

            double x = rotationX / norm, y = rotationY / norm, z = rotationZ / norm;
            double xSquare = Math.pow(x, 2), ySquare = Math.pow(y, 2), zSquare = Math.pow(z, 2);

            double sinAlpha = Math.sin(alpha), cosAlpha = Math.cos(alpha);

            if (values.get(0).getValues().get(i) != null) {
                double xFirst = values.get(0).getValues().get(i).getValue(), 
                        yFirst = values.get(1).getValues().get(i).getValue(), 
                        zFirst = values.get(2).getValues().get(i).getValue();

                {
                    values.get(0).getValues().get(i).setValue(
                           (xSquare + (1 - xSquare) * cosAlpha) * xFirst +
                        (((1 - cosAlpha) * x * y) - sinAlpha * z) * yFirst +
                        (((1 - cosAlpha) * x * z) + sinAlpha * y) * zFirst);
                }

                {
                    values.get(1).getValues().get(i).setValue(
                    ((((1 - cosAlpha) * y * x) + sinAlpha * z) * xFirst +
                        (ySquare + (1 - ySquare) * cosAlpha) * yFirst +
                        (((1 - cosAlpha) * y * z) - sinAlpha * x) * zFirst));
                }

                {
                    values.get(2).getValues().get(i).setValue(
                            ((((1 - cosAlpha) * z * x) - sinAlpha * y) * xFirst +
                                ((1 - cosAlpha) * z * y + sinAlpha * x) * yFirst +
                                (zSquare + (1 - zSquare) * cosAlpha) * zFirst));
                }
            }
        }
    }
    
    private void calculateVAndXPlusYValues(List<SingleCoordinateSet> values) {
        
        values.add(3, new SingleCoordinateSet("|V|"));
        values.add(4, new SingleCoordinateSet("(X+Y)/2"));
        
        for (int i = 0; i < values.get(0).size(); i++) {
            
            if (values.get(0).getValues().get(i) != null) {
                values.get(3).addValue(new DataTime(values.get(0).getValues().get(i).getTime(), 
                    Math.sqrt(Math.pow(values.get(0).getValues().get(i).getValue(), 2) + 
                    Math.pow(values.get(1).getValues().get(i).getValue(), 2) + 
                    Math.pow(values.get(2).getValues().get(i).getValue(), 2)), -1));

                values.get(4).addValue(new DataTime(values.get(0).getValues().get(i).getTime(), 
                    (values.get(0).getValues().get(i).getValue() + 
                            values.get(1).getValues().get(i).getValue()) / 2, -1));
            }
            else {
                values.get(3).addValue(null);
                values.get(4).addValue(null);
            }
        }
    }
    
    private void removeGravity() {
        
        List<Integer> buffer = new ArrayList<Integer>();
        boolean bufferFull = false;
        double bufferDuration = 500000000;
        valuesWithoutGravity.add(new SingleCoordinateSet("X"));
        valuesWithoutGravity.add(new SingleCoordinateSet("Y"));
        valuesWithoutGravity.add(new SingleCoordinateSet("Z"));
        

        for (int index = 0; index < valuesAccelerometer.get(0).size(); index++) {
            
            if (buffer.size() > 0 && (valuesAccelerometer.get(0).getValues().get(index).getTime() - 
                    valuesAccelerometer.get(0).getValues().get(buffer.get(0)).getTime()) > bufferDuration) {
                bufferFull = true;
            }
            else {
                bufferFull = false;
                buffer.add(index);
                valuesWithoutGravity.get(0).addValue(null);
                valuesWithoutGravity.get(1).addValue(null);
                valuesWithoutGravity.get(2).addValue(null);
            }
            
            if (bufferFull) {
                
                float meanValueX = 0, meanValueY = 0, meanValueZ = 0;
                
                for (int i = 0; i < buffer.size(); i++) {
                    meanValueX += valuesAccelerometer.get(0).getValues().get(buffer.get(i)).getValue();
                    meanValueY += valuesAccelerometer.get(1).getValues().get(buffer.get(i)).getValue();
                    meanValueZ += valuesAccelerometer.get(2).getValues().get(buffer.get(i)).getValue();
                }
                
                meanValueX /= buffer.size();
                meanValueY /= buffer.size();
                meanValueZ /= buffer.size();
                
                valuesWithoutGravity.get(0).addValue(new DataTime(valuesAccelerometer.get(0).getValues().get(index).getTime(), 
                        valuesAccelerometer.get(0).getValues().get(index).getValue() - meanValueX, -1));
                valuesWithoutGravity.get(1).addValue(new DataTime(valuesAccelerometer.get(1).getValues().get(index).getTime(),
                        valuesAccelerometer.get(1).getValues().get(index).getValue() - meanValueY, -1));
                valuesWithoutGravity.get(2).addValue(new DataTime((valuesAccelerometer.get(2).getValues().get(index).getTime()), 
                        valuesAccelerometer.get(2).getValues().get(index).getValue() - meanValueZ, -1));
                
                buffer.remove(0);
                buffer.add(index);
            }
        }
    }
    
    public String getStartPosition() {
        return this.startPosition;
    }
    
    public String getEndPosition() {
        return this.endPosition;
    }
    
    public String getStatusRecord() {
        return this.statusRecord;
    }
    
    public SingleCoordinateSet getProximityValues() {
        return this.proximity;
    }
    
    public List<SingleCoordinateSet> getAccelerometerWithoutGravity() {
        return this.valuesWithoutGravity;
    }
    
    public List<SingleCoordinateSet> getLinearValues() {
        return this.valuesLinear;
    }
}
