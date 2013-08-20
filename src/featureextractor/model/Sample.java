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
    
    public long time;
    public double valueX;
    public double valueY;
    public double valueZ;
    public double valueV;
    
    public Sample(long time, double valueX, double valueY, double valueZ) {
        this.time = time; 
        this.valueX = valueX; 
        this.valueY = valueY; 
        this.valueZ = valueZ;
        this.valueV=Math.sqrt(Math.pow(valueX, 2)+Math.pow(valueY, 2)+Math.pow(valueZ, 2));
    }

    public long getTime() {
        return time;
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

    public double getValueV() {
        return valueV;
    }

    @Override
    public String toString() {
        return "[" + time + "," + valueX + "," + valueY + "," + valueZ + "]\t(|V|="+valueV+")";
    }
}
