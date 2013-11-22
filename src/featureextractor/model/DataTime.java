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
    private double filteredValue;
    private int step;

    public DataTime(long time, double value, int step) {
        this.time = time;
        this.value = value;
        this.step = step;
    }

    public long getTime() {
        return time;
    }

    public double getValue() {
        return value;
    }

    public double getFilteredValue() {
        return filteredValue;
    }

    public void setFilteredValue(double filteredValue) {
        this.filteredValue = filteredValue;
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
