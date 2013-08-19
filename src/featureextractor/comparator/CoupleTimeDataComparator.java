/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.comparator;

import featureextractor.model.CoupleTimeData;
import java.util.Comparator;

/**
 *
 * @author ark0n3
 */
public class CoupleTimeDataComparator implements Comparator<CoupleTimeData> {
  
  @Override
  public int compare(CoupleTimeData first, CoupleTimeData second) {
    return new Double(first.getValue()).compareTo(new Double(second.getValue()));
  }
  
}
