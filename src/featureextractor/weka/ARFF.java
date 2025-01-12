/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.weka;

import featureextractor.model.FeatureSet;
import featureextractor.model.FeaturesSlidingWindow;
import featureextractor.model.TimeFeature;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Nicola Beghin
 */
public class ARFF {

    private String title = "StairDetection";
    private List<ARFFAttribute> attributes = new ArrayList<ARFFAttribute>();
    public static List<String> classes = new ArrayList<String>();
    private List<ARFFData> data = new ArrayList<ARFFData>();

    public ARFF(String title, List<ARFFAttribute> attributes) {
        this.title = title;
        this.attributes = attributes;
    }

    public String getTitle() {
        return title;
    }

    public List<ARFFAttribute> getAttributes() {
        return attributes;
    }

    public List<String> getClasses() {
        return classes;
    }

    public List<ARFFData> getData() {
        return data;
    }

    public void addClass(String className) {
        if (this.classes.contains(className) == false) {
            this.classes.add(className);
        }
    }
    
    public static void AddClasses(String[] classes) {
        ARFF.classes = new ArrayList<String>(Arrays.asList(classes));
    }

    public void writeToFile(File file) throws IOException {
        if (file.exists()) {
            file.delete();
        }
        FileUtils.writeStringToFile(file, this.toString());
    }

    public void addData(List<ARFFData> data) {
        this.data.addAll(data);
    }

    public void addAllFeaturesData(String title, List<FeatureSet> featureSets, 
            TimeFeature timeFeature, List<Double> ratios) {
        
        List<Double> data_row = new ArrayList<Double>();
        for (FeatureSet featureSet : featureSets) {
            data_row.add(featureSet.getMean());
            data_row.add(featureSet.getStd());
            data_row.add(featureSet.getVariance());
        }
        
        if (timeFeature != null) {
            data_row.add(timeFeature.getMagnitudeMean());
            data_row.add(timeFeature.getSignalMagnitudeArea());
            data_row.addAll(timeFeature.getCorrelations());
        }
        
        if (ratios != null) {
            data_row.addAll(ratios);
        }
        
        
        this.addData(new ARFFData(title, data_row));
    }
    
    public void addAllFeaturesData(String title, List<FeaturesSlidingWindow> features) {
        
        for (int i = 0; i < features.size(); i++) {
            
            List<Double> data_row = new ArrayList<Double>();
            List<FeatureSet> baseFeatures = features.get(i).getBaseFeatures();
            for (int j = 0; j < baseFeatures.size(); j++) {
                data_row.add(baseFeatures.get(j).getMean());
                data_row.add(baseFeatures.get(j).getStd());
                data_row.add(baseFeatures.get(j).getVariance());
                data_row.add(baseFeatures.get(j).getDifferenceMinMax());
            }
            
            data_row.addAll(features.get(i).getRatios());
            data_row.addAll(features.get(i).getCorrelations());
            
            data_row.add(features.get(i).getMagnitudeMean());
            data_row.add(features.get(i).getSignalMagnitudeArea());
            
            this.addData(new ARFFData(title, data_row));
        }
        
    }
    
    /**
     * Adds all the features related to the Mizell P and H vector
     * @param title: classification label
     * @param features: list of sliding windows features
     */
    public void addAllFeaturesDataMizell(String title, List<FeaturesSlidingWindow> features) {
        
        for (int i = 0; i < features.size(); i++) {
            
            List<Double> data_row = new ArrayList<Double>();
            
            /**
             * Attaching features related to Mizell P Vector
             */
            List<FeatureSet> baseFeatures = features.get(i).getBaseFeaturesPMitzell();
            for (int j = 0; j < baseFeatures.size(); j++) {
                data_row.add(baseFeatures.get(j).getMean());
                data_row.add(baseFeatures.get(j).getStd());
                data_row.add(baseFeatures.get(j).getVariance());
                data_row.add(baseFeatures.get(j).getDifferenceMinMax());
            }
            
            data_row.addAll(features.get(i).getRatiosPMitzell());
            data_row.addAll(features.get(i).getCorrelationsPMitzell());
            
            data_row.add(features.get(i).getMagnitudeMeanPMitzell());
            data_row.add(features.get(i).getSignalMagnitudeAreaPMitzell());
            
            /**
             * Attaching features related to Mizell H Vector
             */
            baseFeatures = features.get(i).getBaseFeaturesHMitzell();
            for (int j = 0; j < baseFeatures.size(); j++) {
                data_row.add(baseFeatures.get(j).getMean());
                data_row.add(baseFeatures.get(j).getStd());
                data_row.add(baseFeatures.get(j).getVariance());
                data_row.add(baseFeatures.get(j).getDifferenceMinMax());
            }
            
            data_row.addAll(features.get(i).getRatiosHMitzell());
            data_row.addAll(features.get(i).getCorrelationsHMitzell());
            
            data_row.add(features.get(i).getMagnitudeMeanHMitzell());
            data_row.add(features.get(i).getSignalMagnitudeAreaHMitzell());
            
            this.addData(new ARFFData(title, data_row));
        }
    }

    public void addVarianceOnlyData(String title, List<FeatureSet> featureSets) {
        List<Double> data_row = new ArrayList<Double>();
        for (FeatureSet featureSet : featureSets) {
            data_row.add(featureSet.getVariance());
        }
        this.addData(new ARFFData(title, data_row));
    }

    public void addStdOnlyData(String title, List<FeatureSet> featureSets) {
        List<Double> data_row = new ArrayList<Double>();
        for (FeatureSet featureSet : featureSets) {
            data_row.add(featureSet.getStd());
        }
        this.addData(new ARFFData(title, data_row));
    }

        public void addStdAndMeanOnlyData(String title, List<FeatureSet> featureSets) {
        List<Double> data_row = new ArrayList<Double>();
        for (FeatureSet featureSet : featureSets) {
            data_row.add(featureSet.getStd());
            data_row.add(featureSet.getMean());
            data_row.add(featureSet.getVariance());
        }
        this.addData(new ARFFData(title, data_row));
    }
        
    public void addMeanOnlyData(String title, List<FeatureSet> featureSets) {
        List<Double> data_row = new ArrayList<Double>();
        for (FeatureSet featureSet : featureSets) {
            data_row.add(featureSet.getMean());
        }
        this.addData(new ARFFData(title, data_row));
    }

    public void addData(String title, FeatureSet featureSet) {
        List<Double> data_row = new ArrayList<Double>();
        data_row.add(featureSet.getMean());
        data_row.add(featureSet.getVariance());
        data_row.add(featureSet.getStd());
        this.addData(new ARFFData(title, data_row));
    }

    public void addData(ARFFData data) {
        this.data.add(data);
    }
    
    public void resetData() {
        this.data.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@RELATION " + title);
        for (ARFFAttribute attribute : attributes) {
            sb.append("\n" + attribute);
        }
        sb.append("\n@ATTRIBUTE class {" + StringUtils.join(ARFF.classes, ",") + "}");
        sb.append("\n@DATA");
        for (ARFFData row : data) {
            sb.append("\n" + row);
        }
        return sb.toString();
    }
}
