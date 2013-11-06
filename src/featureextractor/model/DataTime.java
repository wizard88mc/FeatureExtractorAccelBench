/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.model;

/**
 *
 * @author po
 */
public class DataTime {
    
    private long time;
    private double value;
    private int step;
    public static double MIN_VALUE = Double.MAX_VALUE;
    public static double MAX_VALUE = Double.MIN_VALUE;
    
    public DataTime(long time, double value, int step) {
        this.time = time; this.value = value; this.step= step;
        if (this.value > DataTime.MAX_VALUE) {
            DataTime.MAX_VALUE = this.value;
        }
        if (this.value < DataTime.MIN_VALUE) {
            DataTime.MIN_VALUE = this.value;
        }
    }
    
    public static void reset() {
        DataTime.MIN_VALUE = Double.MAX_VALUE;
        DataTime.MAX_VALUE = Double.MIN_VALUE;
    }

    public long getTime() {
        return time;
    }

    public double getValue() {
        return value;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setValue(double value) {
        this.value = value;
    }
    
    public void normalize(double min, double max) {
        this.value = ((this.value - min) / (max - min));
    }

    public int getStep() {
        return step;
    }
    
}
