/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.utils;

import featureextractor.comparator.SampleTimeComparator;
import featureextractor.model.Sample;
import featureextractor.model.Batch;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Nicola Beghin
 */
public class SamplesUtils {

    private static int samples_for_sampling_rate_calculation = 300;

    public static List<Batch> getAll(ArrayList<Sample> values) throws Exception {
        if (values.isEmpty()) {
            throw new Exception("No sample provided");
        }
        ArrayList<Batch> batches = new ArrayList<Batch>();
        batches.add(new Batch(values));
        return batches;
    }

    public static List<Batch> getSingleFixedSizeBatch(ArrayList<Sample> values, int num_samples) throws Exception {
        return SamplesUtils.getRangeBatch(values, 0, num_samples);
    }

    public static List<Batch> getRangeBatch(ArrayList<Sample> values, int start, int end) throws Exception {
        if (values.isEmpty()) {
            throw new Exception("No sample provided");
        }
        if (start > values.size()) {
            throw new Exception(values.size() + " samples (<" + start);
        }
        if (end > values.size()) {
            throw new Exception(values.size() + " samples (<" + end);
        }
        ArrayList<Batch> batches = new ArrayList<Batch>();
        batches.add(new Batch(values.subList(start, end + 1)));
        return batches;
    }

    public static List<Batch> getBatchesByTrunk(ArrayList<Sample> values) throws Exception {
        if (values.isEmpty()) {
            throw new Exception("No sample provided");
        }
        int num_samples = values.size();
        ArrayList<Batch> batches = new ArrayList<Batch>();
        int i=0;
        int trunk=1;
        List<Sample> samples=new ArrayList<Sample>();
        while(i<num_samples) {
            if (values.get(i).getTrunk()==trunk) {
                samples.add(values.get(i));
                i++;
            }
            else {
                Batch batch=new Batch(samples);
                batch.setTitle("Trunk "+trunk+": "+values.get(i-1).getAction());
                batches.add(batch);
                samples.clear();
                trunk=values.get(i).getTrunk();
            }
            
        }

        return batches;
    }

    public static List<Batch> getNonInterlappingFixedSizeBatches(ArrayList<Sample> values, int num_samples_per_batch) throws Exception {
        if (values.isEmpty()) {
            throw new Exception("No sample provided");
        }
        int num_samples = values.size();
        ArrayList<Batch> batches = new ArrayList<Batch>();
        int max = 0, i = 0;
        while (i < num_samples) {
            max = i + num_samples_per_batch - 1;
            if (max >= num_samples) {
                break; // skip last batch if not long enough
            }
            batches.add(new Batch(values.subList(i, max + 1)));
            i += num_samples_per_batch;
        }
        return batches;
    }

    public static List<Batch> getInterlappingFixedSizeBatches(ArrayList<Sample> values, int num_samples_per_batch) throws Exception {
        if (values.isEmpty()) {
            throw new Exception("No sample provided");
        }
        if (num_samples_per_batch % 2 == 1) {
            throw new Exception(num_samples_per_batch + " is not an even number");
        }
        int num_samples = values.size();
        ArrayList<Batch> batches = new ArrayList<Batch>();
        int max = 0, i = 0;
        while (i < num_samples) {
            max = i + num_samples_per_batch;
            if (max >= num_samples) {
                break; // skip last batch if not long enough
            }
            batches.add(new Batch(values.subList(i, max + 1)));
            i += num_samples_per_batch / 2;
        }
        return batches;
    }

    // to be fixed (timestamp from the moment the mobile has been powered on)
    public static double getSamplingRate(ArrayList<Sample> values) throws Exception {
        if (values.size() < samples_for_sampling_rate_calculation) {
            throw new Exception("At least " + samples_for_sampling_rate_calculation + " samples needed");
        }
        List<Sample> samples = values.subList(0, samples_for_sampling_rate_calculation);
        Sample max_sample = Collections.max(samples, new SampleTimeComparator());
        Sample min_sample = Collections.min(samples, new SampleTimeComparator());
        System.out.println("SECONDI: " + (max_sample.getTime() - min_sample.getTime()) / 1000000000);
        return samples_for_sampling_rate_calculation / (long) ((max_sample.getTime() - min_sample.getTime()) / 1000000000);
    }
}
