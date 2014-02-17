/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.weka;

import java.util.List;
import java.util.Random;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author Nicola Beghin
 */
public class Weka {

    private Instances isTrainingSet;
    private Classifier classifier=new NaiveBayes();
    
    public void setTrainingSet(ARFF arff) {

        List<ARFFAttribute> attributes = arff.getAttributes();
        List<String> classes = arff.getClasses();
        List<ARFFData> dataset = arff.getData();

        // Declare the class attribute along with its values
        FastVector fvClassVal = new FastVector(classes.size());
        for (String className : classes) {
            fvClassVal.addElement(className);
        }
        Attribute ClassAttribute = new Attribute("theClass", fvClassVal);

        // Declare the feature vector
        FastVector fvWekaAttributes = new FastVector(attributes.size() + 1);
        for (ARFFAttribute attribute : attributes) {
            fvWekaAttributes.addElement(new Attribute(attribute.getName()));
        }
        fvWekaAttributes.addElement(ClassAttribute);

        // Create an empty training set
        isTrainingSet = new Instances(arff.getTitle(), fvWekaAttributes, 10);

        // Set class index
        isTrainingSet.setClassIndex(attributes.size());

        // Create the instance
        for (ARFFData data : arff.getData()) {
            Instance iExample = new Instance(attributes.size()+1);
            List<Double> row_data=data.getNormalizedData();
            for (int i = 0; i < fvWekaAttributes.size() - 1; i++) {
                iExample.setValue((Attribute) fvWekaAttributes.elementAt(i), row_data.get(i));
            }
            Attribute prova=(Attribute) fvWekaAttributes.elementAt(fvWekaAttributes.size()-1);
            iExample.setValue((Attribute) fvWekaAttributes.elementAt(fvWekaAttributes.size()-1), data.getClassName());

            // add the instance
            isTrainingSet.add(iExample);
        }
    }

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }
    
    public Classifier classify() throws Exception {
        this.classifier.buildClassifier(isTrainingSet);
        return this.classifier;
    }

    public void testClassifier(Classifier cModel) throws Exception { // evaluation on training set
        this.testClassifier(cModel, isTrainingSet);
    }
    
    public Evaluation testClassifier(Classifier cModel, Instances isTestingSet) throws Exception {
        Evaluation eTest = new Evaluation(isTrainingSet);
        //eTest.evaluateModel(cModel, isTestingSet);
        eTest.crossValidateModel(classifier, isTestingSet, 10, new Random(1));
        String strSummary = eTest.toSummaryString();
        System.out.println(strSummary);
        double[][] confusionMatrix=eTest.confusionMatrix();
        System.out.println(eTest.toMatrixString());
        double num_stairs=(confusionMatrix[0][0]+confusionMatrix[0][1]);
        double num_nonstairs=(confusionMatrix[1][0]+confusionMatrix[1][1]);
        System.out.println("STAIR instances: "+num_stairs);
        System.out.println("NONSTAIR instances: "+num_nonstairs);        
        System.out.println("% stairs: "+(num_stairs/eTest.numInstances())*100);
        return eTest;
    }
}
