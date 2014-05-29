/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.model;

/**
 *
 * @author Matteo
 */
public class Sample {

    final private long time;
    private boolean hasNoGravityValues = false;
    final private double valueX;
    final private double valueY;
    final private double valueZ;
    private double rotatedValueX;
    private double rotatedValueY;
    private double rotatedValueZ;
    private double valueXNoGravity;
    private double valueYNoGravity;
    private double valueZNoGravity;
    private double rotatedValueXNoGravity;
    private double rotatedValueYNoGravity;
    private double rotatedValueZNoGravity;
    final private double rotationX;
    final private double rotationY;
    final private double rotationZ;
    final private String action;
    final private int trunk;
    final private String mode;

    public Sample(long time, double valueX, double valueY, double valueZ, double rotationX, 
            double rotationY, double rotationZ, int trunk, String action, String mode) {
        this.time = time;
        this.valueX = valueX; this.valueY = valueY; this.valueZ = valueZ;
        this.rotationX = rotationX; this.rotationY = rotationY; this.rotationZ = rotationZ;
        this.trunk = trunk;
        this.action = action;
        this.mode = mode;
        
        rotateValues(false);
    }
    
    public void setNoGravityValues(double x, double y, double z) {
        
        this.valueXNoGravity = x; this.valueYNoGravity = y; 
        this.valueZNoGravity = z;
        rotateValues(true);
    }
    
    public void hasNoGravityValues() {
        this.hasNoGravityValues = true;
    }
    
    public boolean getHasNoGravityValues() {
        return hasNoGravityValues;
    }
    
    public String getMode() {
        return mode;
    }

    public long getTime() {
        return time;
    }

    public String getAction() {
        return action;
    }

    public double getValueX() {
        return valueX;
    }

    public double getValueY() {
        return valueY;
    }

    public double getValueZ() {
        return valueZ;
    }
    
    public double getValueXAndYMean() {
        return (valueX + valueY) / (double)2;
    }

    public double getValueV() {
        return Math.sqrt(Math.pow(valueX, 2) + Math.pow(valueY, 2) + Math.pow(valueZ, 2));
    }

    public int getTrunk() {
        return trunk;
    }
    
    private void rotateValues(boolean useNoGravityValues) {
        
        double norm = Math.sqrt(Math.pow(rotationX, 2) + Math.pow(rotationY, 2) + 
                Math.pow(rotationZ, 2));
        if (norm >1) {
            norm = 1;
        }
        double alpha = 2 * Math.asin(norm);
        
        double x = rotationX / norm, y = rotationY / norm, z = rotationZ / norm;
        double xSquare = Math.pow(x, 2), ySquare = Math.pow(y, 2), zSquare = Math.pow(z, 2);
        
        double sinAlpha = Math.sin(alpha), cosAlpha = Math.cos(alpha);
        
        double xFirst = valueX, yFirst = valueY, zFirst = valueZ;
        
        if (useNoGravityValues) {
            xFirst = valueXNoGravity; yFirst = valueYNoGravity; zFirst = valueZNoGravity;
        }
        
        double calculatedValueX = ((xSquare + (1 - xSquare) * cosAlpha) * xFirst +
                (((1 - cosAlpha) * x * y) - sinAlpha * z) * yFirst +
                (((1 - cosAlpha) * x * z) + sinAlpha * y) * zFirst);
        
        double calculatedValueY = ((((1 - cosAlpha) * y * x) + sinAlpha * z) * xFirst +
                (ySquare + (1 - ySquare) * cosAlpha) * yFirst +
                (((1 - cosAlpha) * y * z) - sinAlpha * x) * zFirst);
        
        
        double calculatedValueZ = ((((1 - cosAlpha) * z * x) - sinAlpha * y) * xFirst +
                        ((1 - cosAlpha) * z * y + sinAlpha * x) * yFirst +
                        (zSquare + (1 - zSquare) * cosAlpha) * zFirst);
            
        if (useNoGravityValues) {
            rotatedValueXNoGravity = calculatedValueX;
            rotatedValueYNoGravity = calculatedValueY;
            rotatedValueZNoGravity = calculatedValueZ;
        }
        else {
            rotatedValueX = calculatedValueX;
            rotatedValueY = calculatedValueY;
            rotatedValueZ = calculatedValueZ;
        }
        
    }
    
    public double getRotatedX() {
        return rotatedValueX;
    }
    
    public double getRotatedY() {
        return rotatedValueY;
    }
    
    public double getRotatedZ() {
        return rotatedValueZ;
    }
    
    public double getRotatedXAndYMean() {
        return (rotatedValueX + rotatedValueY) / (double)2;
    }
    
    public double getRotatedNoGravityX() {
        return rotatedValueXNoGravity;
    }
    
    public double getRotatedNoGravityY() {
        return rotatedValueYNoGravity;
    }
    
    public double getRotatedNoGravityZ() {
        return rotatedValueZNoGravity;
    }
    
    public double getRotatedNoGravityXAndYMean() {
        return (getRotatedNoGravityX() + getRotatedNoGravityY()) / (double) 2;
    }
    
    public double getNoGravityX() {
        return valueXNoGravity;
    }
    
    public double getNoGravityY() {
        return valueYNoGravity;
    }
    
    public double getNoGravityZ() {
        return valueZNoGravity;
    }
    
    public double getNoGravityXAndYMean() {
        return (valueXNoGravity + valueYNoGravity) / (double)2;
    }

    @Override
    public String toString() {
        return "[" + time + "," + valueX + "," + valueY + "," + valueZ + "]\t(|V|=" + getValueV() + ")";
    }
}
