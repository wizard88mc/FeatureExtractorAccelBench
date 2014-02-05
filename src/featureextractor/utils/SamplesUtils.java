/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.utils;

import featureextractor.comparator.SampleTimeComparator;
import featureextractor.extractor.db.DbExtractor;
import featureextractor.model.Sample;
import featureextractor.model.Batch;
import featureextractor.model.DataTime;
import featureextractor.model.SingleCoordinateSet;
import featureextractor.model.SlidingWindow;
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

    public static List<Batch> getBatchesByTrunk(ArrayList<Sample> values, DbExtractor db_extractor, boolean linear) throws Exception {
        if (values.isEmpty()) {
            throw new Exception("No sample provided");
        }
        return db_extractor.extractByTrunk(linear);
    }

    public static List<Batch> getBatchesByStep(ArrayList<Sample> values) throws Exception {
        if (values.isEmpty()) {
            throw new Exception("No sample provided");
        }
        int num_samples = values.size();
        ArrayList<Batch> batches = new ArrayList<Batch>();
        int i = 0;
        int step = 0;
        List<Sample> samples = new ArrayList<Sample>();
        while (i < num_samples) {
            if (values.get(i).getStep() == 0) {
                i++;
                continue;
            }
            if (values.get(i).getStep() == step) {
                samples.add(values.get(i));
                i++;
            } else if (step == 0) {
                step = values.get(i).getStep();
            } else {
                Batch batch = new Batch(samples);
                batch.setTitle("Step " + step + ": " + values.get(i - 1).getAction());
                batches.add(batch);
                samples.clear();
                step = values.get(i).getStep();
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

    public static List<Batch> getBatchesByTimeRange(ArrayList<Sample> values, long duration) throws Exception {
        List<Batch> batches=new ArrayList<Batch>();
        List<Sample> samples_for_batch=new ArrayList<Sample>();
        Collections.sort(values, new SampleTimeComparator()); // make sure timestamp are ordered
        long deltaTime=0;
        for(Sample sample: values) {
            samples_for_batch.add(sample);
            deltaTime=(samples_for_batch.get(samples_for_batch.size()-1).getTime()-samples_for_batch.get(0).getTime());
            if (deltaTime>= duration) {
                batches.add(new Batch(samples_for_batch));
                samples_for_batch.clear();
            }  
        }
        // last batch skipped if dimension not big enough
        return batches;
    }
    
    public static List<SlidingWindow> getBatchesWithSlidingWindowAndFixedTime(List<SingleCoordinateSet> values, 
            long duration, int numberOverlappingWindows) throws Exception {
        
        List<SlidingWindow> finalBatches = new ArrayList<SlidingWindow>();
        List<Sample> samplesForSingleBatch = new ArrayList<Sample>();
        
        List<DataTime> valuesToCalculateSlidingWindow = values.get(0).getValues();
        
        for (int indexStartingWindow = 0; indexStartingWindow < valuesToCalculateSlidingWindow.size(); ) {
            
            boolean windowCompleted = false;
            int finalPoint = 0;
            for (int indexSlidingWindow = indexStartingWindow; 
                    indexSlidingWindow < valuesToCalculateSlidingWindow.size() && !windowCompleted; 
                    indexSlidingWindow++) {
                
                if ((valuesToCalculateSlidingWindow.get(indexSlidingWindow).getTime() - 
                        valuesToCalculateSlidingWindow.get(indexStartingWindow).getTime()) <= duration) {
                    
                    //samplesForSingleBatch.add(values.get(indexSlidingWindow));
                    
                }
                else {
                    windowCompleted = true;
                    finalPoint = indexSlidingWindow; // last point tof the sliding window
                }
                
            }
            
            if ((finalPoint + 1 < valuesToCalculateSlidingWindow.size()) && (valuesToCalculateSlidingWindow.get(finalPoint + 1).getTime() - 
                    valuesToCalculateSlidingWindow.get(indexStartingWindow).getTime()) >= duration 
                    ) { // means that the sliding window is complete
                
                List<SingleCoordinateSet> elementsForSlidingWindow = new ArrayList<SingleCoordinateSet>();
                
                for (int i = 0; i < values.size(); i++) {
                    
                    SingleCoordinateSet coordinateSet = new SingleCoordinateSet(values.get(i).getValues().subList(indexStartingWindow, finalPoint));
                    coordinateSet.setTitle(values.get(i).getTitle());
                    elementsForSlidingWindow.add(i, coordinateSet);
                }
                finalBatches.add(new SlidingWindow(elementsForSlidingWindow));
                
                long increaseTime = duration / numberOverlappingWindows;
                
                boolean stop = false;
                for (int i = indexStartingWindow + 1; i < values.size() && !stop; i++) {
                    if (valuesToCalculateSlidingWindow.get(i).getTime() - valuesToCalculateSlidingWindow.get(indexStartingWindow).getTime() >= increaseTime) {
                        indexStartingWindow = i;
                        stop = true;
                    }
                }
            }
        }
        return finalBatches;
    }
}
