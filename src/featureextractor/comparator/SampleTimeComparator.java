/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.comparator;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import featureextractor.model.Sample;
import java.util.Comparator;

/**
 *
 * @author ark0n3
 */
public class SampleTimeComparator implements Comparator<Sample> {
  
  @Override
  public int compare(Sample first, Sample second) {
    return new Double(first.getTime()).compareTo(new Double(second.getTime()));
  }
  
}
