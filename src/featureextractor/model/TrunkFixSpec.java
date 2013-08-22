/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.model;

/**
 *
 * @author Nicola Beghin
 */
public class TrunkFixSpec {

    private boolean skip = false;
    private long start;
    private long end;
    final private int trunk_id;

    public TrunkFixSpec(int trunk_id, long start, long end) {
        this.trunk_id = trunk_id;
        this.start = start;
        this.end = end;
    }

    public TrunkFixSpec(int trunk_id, boolean skip) {
        this.trunk_id = trunk_id;
        this.skip = true;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public int getTrunkId() {
        return trunk_id;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }
    
    
   
}
