/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.packager;

import featureextractor.model.Sample;
import featureextractor.model.SamplesBatch;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ark0n3
 */
public class BatchCreator {
    
    public static List<SamplesBatch> getBatchesBySamplesNum(ArrayList<Sample> values, int num_samples_per_batch) throws Exception {
        if (values.isEmpty()) throw new Exception("No sample provided");
        int num_samples=values.size();
        ArrayList<SamplesBatch> batches=new ArrayList<SamplesBatch>();
        int max=0,i=0;
        while(max<num_samples-1) {
            max=(i+1)*num_samples_per_batch-1;
            if (max>=num_samples) {
                max=num_samples-1;
            }
            System.out.println((i*num_samples_per_batch)+" - "+max);
            batches.add(new SamplesBatch(values.subList(i*num_samples_per_batch, max)));
            i++;
        }
        return batches;
    }
    
}
