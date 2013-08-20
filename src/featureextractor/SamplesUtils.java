/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor;

import featureextractor.comparator.SampleTimeComparator;
import featureextractor.model.Sample;
import featureextractor.model.Batch;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author ark0n3
 */
public class SamplesUtils {
    
    private static int samples_for_sampling_rate_calculation=300;
            
    public static List<Batch> getBatchesBySamplesNum(ArrayList<Sample> values, int num_samples_per_batch) throws Exception {
        if (values.isEmpty()) throw new Exception("No sample provided");
        int num_samples=values.size();
        ArrayList<Batch> batches=new ArrayList<Batch>();
        int max=0,i=0;
        while(max<num_samples-1) {
            max=(i+1)*num_samples_per_batch-1;
            if (max>=num_samples) {
                max=num_samples-1;
            }
            batches.add(new Batch(values.subList(i*num_samples_per_batch, max)));
            i++;
        }
        return batches;
    }
    
    public static double getSamplingRate(ArrayList<Sample> values) throws Exception {
        if (values.size()<samples_for_sampling_rate_calculation) throw new Exception("At least "+samples_for_sampling_rate_calculation+" samples needed");
        List<Sample> samples=values.subList(0, samples_for_sampling_rate_calculation);
        Sample max_sample=Collections.max(samples, new SampleTimeComparator());
        Sample min_sample=Collections.min(samples, new SampleTimeComparator());
        System.out.println("SECONDI: "+(max_sample.getTime()-min_sample.getTime())/1000000000);
        return samples_for_sampling_rate_calculation/(long)((max_sample.getTime()-min_sample.getTime())/1000000000);
    }
        
}
