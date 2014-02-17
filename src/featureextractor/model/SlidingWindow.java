/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package featureextractor.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matteo
 */
public class SlidingWindow {
    
    private List<SingleCoordinateSet> values = new ArrayList<SingleCoordinateSet>();
    private static List<String> coordinates = new ArrayList();
    private String supposedAction;
    private String placeAction;
    private int trunk = -1;
    private boolean linear = false;
    public static long lastTimestampEndDownstair = 0;
    public static long lastTimestampEndUpstair = 0;
    
    static {
        coordinates.add(0, "X");
        coordinates.add(1, "Y");
        coordinates.add(2, "Z");
        coordinates.add(3, "X+Y");
    }
    
    
    public SlidingWindow(List<SingleCoordinateSet> values) {
        this.values = values;
    }
    
    public SlidingWindow(List<SingleCoordinateSet> values, boolean linear, int trunk) {
        this(values); this.linear = linear;
        this.trunk = trunk;
    }
    
    public SlidingWindow(String action, String placeAction, List<SingleCoordinateSet> values, boolean linear, int trunk) {
        this(values); this.linear = linear;
        this.trunk = trunk; this.supposedAction = action; this.placeAction = placeAction;
    }
    
    public List<SingleCoordinateSet> getValues() {
        return this.values;
    }
    
    public void setSupposedAction(String action) {
        this.supposedAction = action;
    }
    
    public void setPlaceAction(String place) {
        this.placeAction = place;
    }
    
    public String getSupposedAction() {
        return this.supposedAction;
    }
    
    public String getPlaceAction() {
        return this.placeAction;
    }
    
    public int getTrunk() {
        return trunk;
    }
    
    public boolean equals(Object other) {
        
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        SlidingWindow otherWindow = (SlidingWindow)other;
        
        boolean equal = true;
        
        List<DataTime> thisX = values.get(0).getValues(),
                thisY = values.get(1).getValues(),
                thisZ = values.get(2).getValues(),
                otherX = otherWindow.values.get(0).getValues(),
                otherY = otherWindow.values.get(1).getValues(),
                otherZ = otherWindow.values.get(2).getValues();
        
        for (int i = 0; i < thisX.size() && equal; i++) {
            
            if (!(thisX.get(i).getTime() == otherX.get(i).getTime() && 
                   thisX.get(i).getValue().equals(otherX.get(i).getValue()) &&  
                   thisY.get(i).getValue().equals(otherY.get(i).getValue()) && 
                   thisZ.get(i).getValue().equals(otherZ.get(i).getValue()) && 
                   this.supposedAction.equals(otherWindow.supposedAction) && 
                   this.linear == otherWindow.linear)) {
                
                equal = false;
                
            }
            
        }
        
        return equal;
    }
}
