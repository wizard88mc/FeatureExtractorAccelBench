/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.utils;

import featureextractor.extractor.db.DbExtractor;
import featureextractor.model.Sample;
import featureextractor.model.Batch;
import featureextractor.plot.Plot;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.plot.IntervalMarker;

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

    public static List<Batch> getBatchesByTrunk(ArrayList<Sample> values, DbExtractor db_extractor) throws Exception {
        if (values.isEmpty()) {
            throw new Exception("No sample provided");
        }
        return db_extractor.extractByTrunk();
//        int num_samples = values.size();
//        List<Batch> batches = new ArrayList<Batch>();
//        List<IntervalMarker> markers = new ArrayList<IntervalMarker>();
//        int i = 0;
//        int trunk = 1;
//        int step_marker_start = 0;
//        long step_marker_start_timestamp = 0;
//        int step = 0;
//        List<Sample> samples = new ArrayList<Sample>();
//        while (i < num_samples) {
//            if (values.get(i).getTrunk() == trunk) {
//                step = values.get(i).getStep();
//                if (step != 0 && step_marker_start == 0) {
//                    step_marker_start = values.get(i).getStep();
//                    step_marker_start_timestamp = (long) (values.get(i).getTime() / Plot.time_divisor);
//                } else if (step != 0 && step != step_marker_start) {
//                    markers.add(new IntervalMarker(step_marker_start_timestamp, (long) (values.get(i).getTime() / Plot.time_divisor)));
//                    step_marker_start = 0;
//                }
//                samples.add(values.get(i));
//                i++;
//            } else {
//                if (samples.isEmpty()==false) {
//                    Batch batch = new Batch(samples);
//                    batch.setTrunk(trunk);
//                    batch.setMarkers(markers);
//                    batch.setTitle("Trunk " + trunk + ": " + values.get(i - 1).getAction());
//                    batches.add(batch);
//                    samples.clear();
//                    markers.clear();
//                    trunk = values.get(i).getTrunk();
//                }
//            }
//        }
//        if (samples.isEmpty() == false) {
//            Batch batch = new Batch(samples);
//            batch.setTrunk(trunk);
//            batch.setMarkers(markers);
//            batch.setTitle("Trunk " + trunk + ": " + values.get(i - 1).getAction());
//            batches.add(batch);
//            samples.clear();
//            markers.clear();
//        }
//
//        return batches;
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

    public static List<Batch> getBatchesByTimeRange(ArrayList<Sample> values, int time_range) {
        return new ArrayList<Batch>();
    }
}
