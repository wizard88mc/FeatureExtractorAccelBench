/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.model;

/**
 *
 * @author po
 */
public class CoupleTimeData {
    
    public double time;
    public double value;
    
    public CoupleTimeData(double time, double value) {
        this.time = time; this.value = value;
    }

    public double getTime() {
        return time;
    }

    public double getValue() {
        return value;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public void setValue(double value) {
        this.value = value;
    }
    
    public void normalize(double min, double max) {
        this.value = ((this.value - min) / (max - min));
    }
}
