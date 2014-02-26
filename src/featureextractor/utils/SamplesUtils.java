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
    
    public static List<SlidingWindow> getBatchesWithSlidingWindowAndFixedTime(Batch batch, 
            long duration, int numberOverlappingWindows, boolean linear) throws Exception {
        
        List<SingleCoordinateSet> values = batch.getValuesWithoutGravityRotated();
        if (linear) {
            values = batch.getLinearValuesRotated();
        }
        List<SlidingWindow> finalBatches = new ArrayList<SlidingWindow>();
        
        List<DataTime> valuesToCalculateSlidingWindow = values.get(0).getValues();
        boolean endWindow = false;
        for (int indexStartingWindow = 0; indexStartingWindow < valuesToCalculateSlidingWindow.size()
                && !endWindow; ) {
            
            boolean windowCompleted = false;
            int finalPoint = 0;
            for (int indexSlidingWindow = indexStartingWindow; 
                    indexSlidingWindow < valuesToCalculateSlidingWindow.size() && !windowCompleted; 
                    indexSlidingWindow++) {
                
                if ((valuesToCalculateSlidingWindow.get(indexSlidingWindow).getTime() - 
                        valuesToCalculateSlidingWindow.get(indexStartingWindow).getTime()) > duration) {
                    
                    windowCompleted = true;
                    finalPoint = indexSlidingWindow -1; // last point of the sliding window
                }
            }
            
            if ((finalPoint + 1 < valuesToCalculateSlidingWindow.size()) && (valuesToCalculateSlidingWindow.get(finalPoint + 1).getTime() - 
                    valuesToCalculateSlidingWindow.get(indexStartingWindow).getTime()) > duration 
                    ) { // means that the sliding window is complete
                
                List<SingleCoordinateSet> elementsForSlidingWindow = new ArrayList<SingleCoordinateSet>();
                
                for (int i = 0; i < values.size(); i++) {
                    
                    SingleCoordinateSet coordinateSet = new SingleCoordinateSet(values.get(i).getValues().subList(indexStartingWindow, finalPoint +1));
                    coordinateSet.setTitle(values.get(i).getTitle());
                    elementsForSlidingWindow.add(i, coordinateSet);
                }
                SlidingWindow window = new SlidingWindow(elementsForSlidingWindow);
                window.setSupposedAction(batch.getAction());
                window.setPlaceAction(batch.getMode());
                finalBatches.add(window);
                
                long increaseTime = duration / numberOverlappingWindows;
                
                boolean stop = false;
                for (int i = indexStartingWindow + 1; i < valuesToCalculateSlidingWindow.size() && !stop; i++) {
                    
                    if (valuesToCalculateSlidingWindow.get(i).getTime() - valuesToCalculateSlidingWindow.get(indexStartingWindow).getTime() >= increaseTime) {
                        indexStartingWindow = i;
                        stop = true;
                    }
                }
            }
            else {
                endWindow = true;
            }
        }
        return finalBatches;
    }
    
    public static List<SlidingWindow> getSlidingWindowsOfFixedDefinition(Batch batch, boolean linear, List<SlidingWindow> toAddInitialData) {
        
        List<SlidingWindow> listOfWindows = new ArrayList<SlidingWindow>();
        
        List<SingleCoordinateSet> values = batch.getValuesWithoutGravityRotated(),
                vectorHMitzell = batch.getHVectorMitzell(),
                vectorPMitzell = batch.getPVectorMitzell();
        if (linear) {
            values = batch.getLinearValuesRotated();
            vectorHMitzell = null;
            vectorPMitzell = null;
        }
        
        /**
         * Search for the first two points where we move from a negative value 
         * to a positive one
         */
        int startPoint = -1; 
        for (int i = 0; i < values.get(2).size() - 1 && startPoint == -1 ; i++) {
            
            if (values.get(2).getValues().get(i).getValue() < 0 && 
                    values.get(2).getValues().get(i+1).getValue() >= 0) {
                
                startPoint = i + 1;
            }
        }
        
        /**
         * Add a Sliding Window as non stairs made with the initial values
         * of the batch
         */
        List<SingleCoordinateSet> elementsForWindow = new ArrayList<SingleCoordinateSet>(),
                elementsPMitzellWindow = null,
                elementsHMitzellWindow = null;
        for (int index = 0; index < values.size(); index++) {
            SingleCoordinateSet elements = new SingleCoordinateSet(values.get(index).getValues().subList(0, startPoint));
            elements.setTitle(values.get(index).getTitle());
            elementsForWindow.add(elements);
                    
            /**
             * if we are using accelerometer data store even the 
             * mitzell vectors
             */
            if (vectorPMitzell != null) {
                SingleCoordinateSet elementsP = new SingleCoordinateSet(vectorPMitzell.get(index).getValues().subList(0, startPoint)),
                        elementsH = new SingleCoordinateSet(vectorHMitzell.get(index).getValues().subList(0, startPoint));
                        elementsP.setTitle(vectorPMitzell.get(index).getTitle());
                        elementsH.setTitle(vectorHMitzell.get(index).getTitle());
                        
                if (elementsPMitzellWindow == null) {
                    elementsPMitzellWindow = new ArrayList<SingleCoordinateSet>();
                    elementsHMitzellWindow = new ArrayList<SingleCoordinateSet>();
                }
                elementsPMitzellWindow.add(elementsP);
                elementsHMitzellWindow.add(elementsH);
            }
                
        }
                
        toAddInitialData.add(new SlidingWindow(batch.getAction(), batch.getMode(),
            elementsForWindow, elementsPMitzellWindow, elementsHMitzellWindow, linear, batch.getTrunk()));
                
        for (int i = startPoint; i < values.get(0).size() - 1; ) {
            
            if (values.get(2).getValues().get(i).getValue() < 0 && 
                    values.get(2).getValues().get(i + 1).getValue() >= 0) {
                /**
                 * Sliding window is ended
                 * First point is startPoint, endPoint is i
                 */
                elementsForWindow = new ArrayList<SingleCoordinateSet>();
                elementsPMitzellWindow = null;
                elementsHMitzellWindow = null;
                /**
                 * Defining the List<SingleCoordinateSet> that will hold the 
                 * data for the sliding window
                 */
                for (int index = 0; index < values.size(); index++) {
                    SingleCoordinateSet elements = new SingleCoordinateSet(values.get(index).getValues().subList(startPoint, i + 1));
                    elements.setTitle(values.get(index).getTitle());
                    elementsForWindow.add(elements);
                    
                    /**
                     * if we are using accelerometer data store even the 
                     * mitzell vectors
                     */
                    if (vectorPMitzell != null) {
                        SingleCoordinateSet elementsP = new SingleCoordinateSet(vectorPMitzell.get(index).getValues().subList(startPoint, i + 1)),
                                elementsH = new SingleCoordinateSet(vectorHMitzell.get(index).getValues().subList(startPoint, i + 1));
                        elementsP.setTitle(vectorPMitzell.get(index).getTitle());
                        elementsH.setTitle(vectorHMitzell.get(index).getTitle());
                        
                        if (elementsPMitzellWindow == null) {
                            elementsPMitzellWindow = new ArrayList<SingleCoordinateSet>();
                            elementsHMitzellWindow = new ArrayList<SingleCoordinateSet>();
                        }
                        elementsPMitzellWindow.add(elementsP);
                        elementsHMitzellWindow.add(elementsH);
                    }
                    
                }
                
                listOfWindows.add(new SlidingWindow(batch.getAction(), batch.getMode(),
                    elementsForWindow, elementsPMitzellWindow, elementsHMitzellWindow, linear, batch.getTrunk()));
                
                startPoint = i+1;
                i = startPoint + 1;
            }
            else {
                i++;
            }
            
        }
        
        /**
         * Add the final window as a non stair window
         */
        elementsForWindow = new ArrayList<SingleCoordinateSet>();
        elementsPMitzellWindow = null;
        elementsHMitzellWindow = null;
        
        for (int index = 0; index < values.size(); index++) {
            SingleCoordinateSet elements = new SingleCoordinateSet(values.get(index).getValues().subList(startPoint, values.get(0).size()));
            elements.setTitle(values.get(index).getTitle());
            elementsForWindow.add(elements);
                    
            /**
             * if we are using accelerometer data store even the 
             * mitzell vectors
             */
            if (vectorPMitzell != null) {
                SingleCoordinateSet elementsP = 
                        new SingleCoordinateSet(vectorPMitzell.get(index).getValues().subList(startPoint, values.get(0).size())),
                    elementsH = new SingleCoordinateSet(vectorHMitzell.get(index).getValues().subList(startPoint, values.get(0).size()));
                    elementsP.setTitle(vectorPMitzell.get(index).getTitle());
                    elementsH.setTitle(vectorHMitzell.get(index).getTitle());
                        
                if (elementsPMitzellWindow == null) {
                    elementsPMitzellWindow = new ArrayList<SingleCoordinateSet>();
                    elementsHMitzellWindow = new ArrayList<SingleCoordinateSet>();
                }
                elementsPMitzellWindow.add(elementsP);
                elementsHMitzellWindow.add(elementsH);
            }
        }
        
        toAddInitialData.add(new SlidingWindow(batch.getAction(), batch.getMode(),
            elementsForWindow, elementsPMitzellWindow, elementsHMitzellWindow, linear, batch.getTrunk()));
        
        return listOfWindows;
    }
}
