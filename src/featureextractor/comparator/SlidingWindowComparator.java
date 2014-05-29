/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package featureextractor.comparator;

import featureextractor.model.SlidingWindow;
import java.util.Comparator;

/**
 *
 * @author Matteo
 */
public class SlidingWindowComparator implements Comparator<SlidingWindow> {

    @Override
    public int compare(SlidingWindow t, SlidingWindow t1) {
        return new Double(t.getValues().get(0).getValues().get(0).getTime()).compareTo(
            t1.getValues().get(0).getValues().get(0).getTime());
    }
    
}
