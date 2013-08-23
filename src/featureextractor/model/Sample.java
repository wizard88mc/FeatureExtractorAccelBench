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
    final private double valueX;
    final private double valueY;
    final private double valueZ;
    final private double valueV;
    final private String action;
    final private int trunk;
    final private int step;

    public Sample(long time, double valueX, double valueY, double valueZ, int trunk, String action, int step) {
        this.time = time;
        this.valueX = valueX;
        this.valueY = valueY;
        this.valueZ = valueZ;
        this.trunk = trunk;
        this.action = action;
        this.valueV = Math.sqrt(Math.pow(valueX, 2) + Math.pow(valueY, 2) + Math.pow(valueZ, 2));
        this.step = (step>0?step:0);
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

    public double getValueV() {
        return valueV;
    }

    public int getTrunk() {
        return trunk;
    }

    @Override
    public String toString() {
        return "[" + time + "," + valueX + "," + valueY + "," + valueZ + "]\t(|V|=" + valueV + ")";
    }
}
