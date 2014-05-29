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

    private double time;
    private Double value;
    private Double filteredValue;

    public DataTime(double time, Double value) {
        this.time = time;
        this.value = value;
        
        if (Double.isInfinite(this.value) || Double.isNaN(this.value)) {
            this.value = 0.0;
        }
    }

    public double getTime() {
        return time;
    }

    public Double getValue() {
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
}
