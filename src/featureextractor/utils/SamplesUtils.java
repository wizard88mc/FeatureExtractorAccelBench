/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.utils;

import featureextractor.App;
import featureextractor.comparator.SampleTimeComparator;
import featureextractor.extractor.db.DbExtractor;
import featureextractor.model.Sample;
import featureextractor.model.Batch;
import featureextractor.model.DataTime;
import featureextractor.model.SingleCoordinateSet;
import featureextractor.model.SlidingWindow;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Nicola Beghin
 */
public class SamplesUtils {

    /*public static List<Batch> getSingleFixedSizeBatch(ArrayList<Sample> values, int num_samples) throws Exception {
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
    }*/

    public static List<Batch> getBatchesByTrunk(ArrayList<Sample> values, DbExtractor db_extractor, boolean linear) throws Exception {
        if (values.isEmpty()) {
            throw new Exception("No sample provided");
        }
        return db_extractor.extractByTrunk(linear);
    }

    /*public static List<Batch> getBatchesByStep(ArrayList<Sample> values) throws Exception {
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
    }*/
    
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
                window.setMode(batch.getMode());
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
    
    /**
     * Starting from an interval of values, creates a window with the data calculated 
     * with the correct Mizell method and our method removing data from the 
     * current window and not from the previous buffer of data
     * 
     * @param batch
     * @param startPoint: start point of the considered window
     * @param endPoint: end point of the considered window
     * @param elementsDataWithoutGravity: final elements rotated using our method
     * @param elementsPMizell: vector P calculated using the Mizell method
     * @param elementsHMizell: vector H calculated using the Mizell method
     */
    private static void createWindowOfData(Batch batch, 
            int startPoint, int endPoint, boolean linear,
            List<SingleCoordinateSet> elementsDataWithoutGravity, 
            List<SingleCoordinateSet> elementsPMizell, List<SingleCoordinateSet> elementsHMizell) {
        
        
        List<SingleCoordinateSet> valuesToUse = null;
        
        if (!linear) {
            valuesToUse = batch.getValuesWithoutGravityRotated();
        }
        else {
            valuesToUse = batch.getLinearValuesRotated();
        }
        /**
         * Getting all the elements for the data rotated without gravity
         * using our method
         */
        for (int index = 0; index < batch.getValues().size(); index++) {
            SingleCoordinateSet elements = new SingleCoordinateSet(
                    valuesToUse.get(index).getValues().subList(startPoint, endPoint));
            
            elements.setTitle(valuesToUse.get(index).getTitle());
            elementsDataWithoutGravity.add(elements);    
        }
        
        /**
         * Creating the SingleCoordinateSet elements for the Mizell records
         */
        if (!linear) {
            
            /**
            * This two values are the start and end point of a window in the List<SingleCoordinateSet>
            * as the values are retrieved from the accelerometer
            */
           int startIndexForBaseValues = batch.getRealIndexForTimestamp(batch.getValuesWithoutGravityRotated().get(0).getValues().get(startPoint).getTime());
           int endIndexForBaseValues = batch.getRealIndexForTimestamp(batch.getValuesWithoutGravityRotated().get(0).getValues().get(endPoint).getTime());
            
            /**
             * Retrieves all the values inside of the window that will be used to 
             * calculate the p and h vector
             * 
             * valuesForMizell used to store the data that will be used for the 
             * Mizell method
             */
            List<SingleCoordinateSet> valuesForMizell = new ArrayList<SingleCoordinateSet>();
            valuesForMizell.add(new SingleCoordinateSet(batch.getValues().get(0).getValues().subList(startIndexForBaseValues, endIndexForBaseValues)));
            valuesForMizell.add(new SingleCoordinateSet(batch.getValues().get(1).getValues().subList(startIndexForBaseValues, endIndexForBaseValues)));
            valuesForMizell.add(new SingleCoordinateSet(batch.getValues().get(2).getValues().subList(startIndexForBaseValues, endIndexForBaseValues)));
            
            valuesForMizell.get(0).setTitle(batch.getValues().get(0).getTitle());
            valuesForMizell.get(1).setTitle(batch.getValues().get(1).getTitle());
            valuesForMizell.get(2).setTitle(batch.getValues().get(2).getTitle());
            
            double meanValueX = 0.0, meanValueY = 0.0, meanValueZ = 0.0;
            
            for (int i = 0; i < valuesForMizell.get(0).getValues().size(); i++) {
                
                meanValueX += valuesForMizell.get(0).getValues().get(i).getValue();
                meanValueY += valuesForMizell.get(1).getValues().get(i).getValue();
                meanValueZ += valuesForMizell.get(2).getValues().get(i).getValue();
            }
            
            meanValueX /= valuesForMizell.get(0).getValues().size();
            meanValueY /= valuesForMizell.get(1).getValues().size();
            meanValueZ /= valuesForMizell.get(2).getValues().size();
            
            /**
             * Creating a new List<SingleCoordinateSet> with the correct values 
             * as starting point for the Mizell method
             * 
             * ValuesWithoutGravityMizell is the d vector of the Mizell solution
             */
            List<SingleCoordinateSet> valuesWithoutGravityMizell = new ArrayList<SingleCoordinateSet>();
            valuesWithoutGravityMizell.add(new SingleCoordinateSet(valuesForMizell.get(0).getTitle()));
            valuesWithoutGravityMizell.add(new SingleCoordinateSet(valuesForMizell.get(1).getTitle()));
            valuesWithoutGravityMizell.add(new SingleCoordinateSet(valuesForMizell.get(2).getTitle()));
            
            for (int i = 0; i < valuesForMizell.get(0).getValues().size(); i++) {
                
                /**
                 * time and step are fixed values
                 */
                double time = valuesForMizell.get(0).getValues().get(i).getTime();
                
                double value = valuesForMizell.get(0).getValues().get(i).getValue();
                valuesWithoutGravityMizell.get(0).addValue(new DataTime(time, value - meanValueX));
                
                value = valuesForMizell.get(1).getValues().get(i).getValue();
                valuesWithoutGravityMizell.get(1).addValue(new DataTime(time, value - meanValueY));
                
                value = valuesForMizell.get(2).getValues().get(i).getValue();
                valuesWithoutGravityMizell.get(2).addValue(new DataTime(time, value - meanValueZ));
                
            }
            
            if (elementsPMizell == null) {
                elementsPMizell = new ArrayList<SingleCoordinateSet>();
                elementsHMizell = new ArrayList<SingleCoordinateSet>();
            }
            
            elementsPMizell.add(new SingleCoordinateSet(valuesForMizell.get(0).getTitle()));
            elementsPMizell.add(new SingleCoordinateSet(valuesForMizell.get(1).getTitle()));
            elementsPMizell.add(new SingleCoordinateSet(valuesForMizell.get(2).getTitle()));
            
            elementsHMizell.add(new SingleCoordinateSet(valuesForMizell.get(0).getTitle()));
            elementsHMizell.add(new SingleCoordinateSet(valuesForMizell.get(1).getTitle()));
            elementsHMizell.add(new SingleCoordinateSet(valuesForMizell.get(2).getTitle()));
            
            double normMeanValues = (double)Math.sqrt(Math.pow(meanValueX, 2) + Math.pow(meanValueY, 2) + Math.pow(meanValueZ, 2));
            
            for (int i = 0; i < valuesWithoutGravityMizell.get(0).getValues().size(); i++) {
                
                double valueX = valuesWithoutGravityMizell.get(0).getValues().get(i).getValue(),
                        valueY = valuesWithoutGravityMizell.get(1).getValues().get(i).getValue(),
                        valueZ = valuesWithoutGravityMizell.get(2).getValues().get(i).getValue(),
                        time = valuesWithoutGravityMizell.get(0).getValues().get(i).getTime();
                
                double vectorProduct = (valueX * meanValueX + valueY * meanValueY
                         + valueZ * meanValueZ) / Math.pow(normMeanValues, 2);
                
                elementsPMizell.get(0).addValue(new DataTime(time, vectorProduct * meanValueX));
                elementsPMizell.get(1).addValue(new DataTime(time, vectorProduct * meanValueY));
                elementsPMizell.get(2).addValue(new DataTime(time, vectorProduct * meanValueZ));
                
                elementsHMizell.get(0).addValue(new DataTime(time, valueX - elementsPMizell.get(0).getValues().get(i).getValue()));
                elementsHMizell.get(1).addValue(new DataTime(time, valueY - elementsPMizell.get(1).getValues().get(i).getValue()));
                elementsHMizell.get(2).addValue(new DataTime(time, valueZ - elementsPMizell.get(2).getValues().get(i).getValue()));
            }
        }
    }
    
    /**
     * Searches for windows made considering data behavior
     * 
     * @param batch: batch from where we take data
     * @param linear: if it is linear data or not
     * @param toAddInitialData: windows of NoStair data to use to add initial and ending windows
     *          that are clearly not stairs
     * @return 
     */
    public static List<SlidingWindow> getSlidingWindowsOfFixedDefinition(Batch batch, 
            boolean linear, List<SlidingWindow> toAddInitialData) {
        
        List<SlidingWindow> listOfWindows = new ArrayList<SlidingWindow>();
        
        List<SingleCoordinateSet> valuesForSearch = batch.getValuesWithoutGravityRotated();
        if (linear) {
            valuesForSearch = batch.getLinearValuesRotated();
        }
        
        /**
         * Search for the first two points where we move from a negative value 
         * to a positive one
         */
        int startPoint = -1; 
        for (int i = 0; i < valuesForSearch.get(2).size() - 1 && startPoint == -1 ; i++) {
            
            if (valuesForSearch.get(2).getValues().get(i).getValue() < 0 && 
                    valuesForSearch.get(2).getValues().get(i+1).getValue() >= 0) {
                
                startPoint = i + 1;
            }
        }
        
        /**
         * Add a Sliding Window as non stairs made with the initial values
         * of the batch
         */
        List<SingleCoordinateSet> elementsForWindow = new ArrayList<SingleCoordinateSet>(),
                elementsPMizellWindow = null,
                elementsHMizellWindow = null;
        
        if (!linear) {
            elementsPMizellWindow = new ArrayList<SingleCoordinateSet>();
            elementsHMizellWindow = new ArrayList<SingleCoordinateSet>();
        }
        
        createWindowOfData(batch, 0, startPoint, linear, elementsForWindow, elementsPMizellWindow, elementsHMizellWindow);
                
        toAddInitialData.add(new SlidingWindow(batch.getSex(), batch.getAction(),
                batch.getHeight(), batch.getShoes(), batch.getMode(), App.NO_STAIR,
            elementsForWindow, elementsPMizellWindow, elementsHMizellWindow, 
                linear, batch.getTrunk()));
                
        int indexHalfWindow = -1;
        for (int i = startPoint; i < valuesForSearch.get(0).size() - 1; ) {
            try {
                /**
                 * Analyzing values of the Z axis
                 */
                if (valuesForSearch.get(2).getValues().get(i).getValue() < 0 && 
                        valuesForSearch.get(2).getValues().get(i + 1).getValue() >= 0
                        && indexHalfWindow != -1) {
                    
                    double durationFirstHalf = valuesForSearch.get(2).getValues().get(indexHalfWindow).getTime() - 
                            valuesForSearch.get(2).getValues().get(startPoint).getTime();
                    double durationSecondHalf = valuesForSearch.get(2).getValues().get(i).getTime() - 
                            valuesForSearch.get(2).getValues().get(indexHalfWindow + 1).getTime();
                    if (durationFirstHalf / durationSecondHalf > 1.5) {
                        
                        indexHalfWindow = -1;
                        i++;
                    }
                    else {
                    
                    
                        /**
                         * Sliding window is ended
                         * First point is startPoint, endPoint is i
                         */
                        elementsForWindow = new ArrayList<SingleCoordinateSet>();
                        if (!linear) {
                            elementsPMizellWindow = new ArrayList<SingleCoordinateSet>();
                            elementsHMizellWindow = new ArrayList<SingleCoordinateSet>();
                        }
                        else {
                            elementsPMizellWindow = null;
                            elementsHMizellWindow = null;
                        }

                        createWindowOfData(batch, startPoint, i, linear, elementsForWindow, elementsPMizellWindow, elementsHMizellWindow);

                        listOfWindows.add(new SlidingWindow(batch.getSex(), batch.getAge(), 
                                batch.getHeight(), batch.getShoes(), batch.getMode(), 
                                batch.getAction(), elementsForWindow, elementsPMizellWindow, 
                                elementsHMizellWindow, linear, batch.getTrunk()));

                        startPoint = i+1;
                        i = startPoint + 1;
                        indexHalfWindow = -1;
                    }
                }
                else if (valuesForSearch.get(2).getValues().get(i).getValue() >= 0 && 
                            valuesForSearch.get(2).getValues().get(i + 1).getValue() < 0) {
                    indexHalfWindow = i;
                    i++;
                }
                else {
                    i++;
                }
            }
        catch(Exception exc) {
            exc.printStackTrace();
            System.out.println("************");
        }
        }
        
        /**
         * Add the final window as a non stair window
         */
        elementsForWindow = new ArrayList<SingleCoordinateSet>();
        if (!linear) {
            elementsPMizellWindow = new ArrayList<SingleCoordinateSet>();
            elementsHMizellWindow = new ArrayList<SingleCoordinateSet>();
        }
        else {
            elementsPMizellWindow = null;
            elementsHMizellWindow = null;
        }
        
        createWindowOfData(batch, startPoint, valuesForSearch.get(0).size() - 1, 
                linear, elementsForWindow, elementsPMizellWindow, elementsHMizellWindow);
        
        toAddInitialData.add(new SlidingWindow(batch.getSex(), batch.getAge(), 
                batch.getHeight(), batch.getShoes(), batch.getMode(), App.NO_STAIR,
            elementsForWindow, elementsPMizellWindow, elementsHMizellWindow, 
                linear, batch.getTrunk()));
        
        return listOfWindows;
    }
    
    public static List<SlidingWindow> getBaseWindows(Batch batch, boolean linear) {
        
        List<SlidingWindow> listOfWindows = new ArrayList<SlidingWindow>();
        /**
         * Add a Sliding Window as non stairs made with the initial values
         * of the batch
         */
        List<SingleCoordinateSet> elementsForWindow = new ArrayList<SingleCoordinateSet>(),
                elementsPMizellWindow = null,
                elementsHMizellWindow = null;
        
        if (!linear) {
            elementsPMizellWindow = new ArrayList<SingleCoordinateSet>();
            elementsHMizellWindow = new ArrayList<SingleCoordinateSet>();
        }
        
        List<SingleCoordinateSet> valuesForSearch = batch.getValuesWithoutGravityRotated();
        if (linear) {
            valuesForSearch = batch.getLinearValuesRotated();
        }
        
        int startPoint = 0; 
        for (int i = startPoint; i < valuesForSearch.size() - 1; i++) {
            try {
                /**
                 * Analyzing values of the Z axis
                 */
                if (valuesForSearch.get(2).getValues().get(i).getValue() < 0 && 
                        valuesForSearch.get(2).getValues().get(i + 1).getValue() >= 0) {
                    /**
                     * Sliding window is ended
                     * First point is startPoint, endPoint is i
                     */
                    elementsForWindow = new ArrayList<SingleCoordinateSet>();
                    if (!linear) {
                        elementsPMizellWindow = new ArrayList<SingleCoordinateSet>();
                        elementsHMizellWindow = new ArrayList<SingleCoordinateSet>();
                    }
                    else {
                        elementsPMizellWindow = null;
                        elementsHMizellWindow = null;
                    }

                    createWindowOfData(batch, startPoint, i, linear, elementsForWindow, elementsPMizellWindow, elementsHMizellWindow);

                    listOfWindows.add(new SlidingWindow(batch.getSex(), batch.getAge(), 
                            batch.getHeight(), batch.getShoes(), batch.getMode(), 
                            batch.getAction(), elementsForWindow, elementsPMizellWindow, 
                            elementsHMizellWindow, linear, batch.getTrunk()));

                    startPoint = i+1;
                    i = startPoint + 1;
                }
                else {
                    i++;
                }
            }
            catch(Exception exc) {
                exc.printStackTrace();
                System.out.println("************");
            }
        }
        
        if (startPoint < valuesForSearch.size() - 1) {
            elementsForWindow = new ArrayList<SingleCoordinateSet>();
            if (!linear) {
                elementsPMizellWindow = new ArrayList<SingleCoordinateSet>();
                elementsHMizellWindow = new ArrayList<SingleCoordinateSet>();
            }
            else {
                elementsPMizellWindow = null;
                elementsHMizellWindow = null;
            }

            createWindowOfData(batch, startPoint, valuesForSearch.size() - 1, 
                    linear, elementsForWindow, elementsPMizellWindow, elementsHMizellWindow);
            
            listOfWindows.add(new SlidingWindow(batch.getSex(), batch.getAge(), 
                    batch.getHeight(), batch.getShoes(), batch.getMode(), App.NO_STAIR,  
                elementsForWindow, elementsPMizellWindow, elementsHMizellWindow, 
                    linear, batch.getTrunk()));
        }
        
        return listOfWindows;
    }
}
