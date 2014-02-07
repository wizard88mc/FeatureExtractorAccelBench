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
    
}
