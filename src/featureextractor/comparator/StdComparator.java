/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.comparator;

/**
 *
 * @author Nicola Beghin
 */
import featureextractor.model.FeatureSet;
import java.util.Comparator;

/**
 *
 * @author Nicola Beghin
 */
public class StdComparator implements Comparator<FeatureSet> {
  
  @Override
  public int compare(FeatureSet first, FeatureSet second) {
    return new Double(second.getStd()).compareTo(new Double(first.getStd()));
  }
  
}
