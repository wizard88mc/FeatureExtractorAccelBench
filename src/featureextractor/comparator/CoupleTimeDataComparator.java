/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.comparator;

import featureextractor.model.DataTime;
import java.util.Comparator;

/**
 *
 * @author Nicola Beghin
 */
public class CoupleTimeDataComparator implements Comparator<DataTime> {
  
  @Override
  public int compare(DataTime first, DataTime second) {
    return new Double(first.getValue()).compareTo(new Double(second.getValue()));
  }
  
}
