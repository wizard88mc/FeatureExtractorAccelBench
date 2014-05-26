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
    final private int step;
    final private String mode;

    public Sample(long time, double valueX, double valueY, double valueZ, double rotationX, 
            double rotationY, double rotationZ, int trunk, String action, int step, String mode) {
        this.time = time;
        this.valueX = valueX; this.valueY = valueY; this.valueZ = valueZ;
        this.rotationX = rotationX; this.rotationY = rotationY; this.rotationZ = rotationZ;
        this.trunk = trunk;
        this.action = action;
        this.step = (step > 0 ? step : 0);
        this.mode = mode;
    }
    
    private void rotateValues() {
        
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

    public int getStep() {
        return step;
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
            xFirst = noGravityX; yFirst = noGravityY; zFirst = noGravityZ;
        }
        
        if (wantX) {
            return ((xSquare + (1 - xSquare) * cosAlpha) * xFirst +
                (((1 - cosAlpha) * x * y) - sinAlpha * z) * yFirst +
                (((1 - cosAlpha) * x * z) + sinAlpha * y) * zFirst);
        }
        else if (wantY) {
            return ((((1 - cosAlpha) * y * x) + sinAlpha * z) * xFirst +
                (ySquare + (1 - ySquare) * cosAlpha) * yFirst +
                (((1 - cosAlpha) * y * z) - sinAlpha * x) * zFirst);
        }
        else if (wantZ) {
            double value = ((((1 - cosAlpha) * z * x) - sinAlpha * y) * xFirst +
                        ((1 - cosAlpha) * z * y + sinAlpha * x) * yFirst +
                        (zSquare + (1 - zSquare) * cosAlpha) * zFirst);
            if (Double.isNaN(value)) {
                System.out.println("none");
                return -1;
            }
            else {
                return value;
            }
        }
        return -1;
    }
    
    public double getRotatedX() {
        double value = getRotatedAxis(true, false, false, false);
        if (Double.isNaN(value)) {
            System.out.println("None");
            return -1;
        }
        else {
            return value;
        }
    }
    
    public double getRotatedY() {
        return getRotatedAxis(false, true, false, false);
    }
    
    public double getRotatedZ() {
        return getRotatedAxis(false, false, true, false);
    }
    
    public double getRotatedXAndYMean() {
        return (getRotatedX() + getRotatedY()) / (double)2;
    }
    
    public double getRotatedNoGravityX() {
        return getRotatedAxis(true, false, false, true);
    }
    
    public double getRotatedNoGravityY() {
        return getRotatedAxis(false, true, false, true);
    }
    
    public double getRotatedNoGravityZ() {
        return getRotatedAxis(false, false, true, true);
    }
    
    public double getRotatedNoGravityXAndYMean() {
        return (getRotatedNoGravityX() + getRotatedNoGravityY()) / (double) 2;
    }
    
    public double getNoGravityX() {
        return noGravityX;
    }
    
    public double getNoGravityY() {
        return noGravityY;
    }
    
    public double getNoGravityZ() {
        return noGravityZ;
    }
    
    public double getNoGravityXAndYMean() {
        return (noGravityX + noGravityY) / (double)2;
    }*/

    @Override
    public String toString() {
        return "[" + time + "," + valueX + "," + valueY + "," + valueZ + "]\t(|V|=" + getValueV() + ")";
    }
}
